/*
 * Copyright 2024 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.carioca.report.stage.test

import android.util.Log
import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.interceptor.intercept
import com.rubensousa.carioca.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.recording.ReportRecording
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.AbstractStage
import com.rubensousa.carioca.report.stage.InstrumentedStage
import com.rubensousa.carioca.report.stage.scenario.InstrumentedScenarioStageImpl
import com.rubensousa.carioca.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.report.stage.step.InstrumentedStepDelegate
import com.rubensousa.carioca.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.report.storage.IdGenerator
import com.rubensousa.carioca.report.storage.TestStorageProvider
import org.junit.runner.Description
import java.io.OutputStream


interface InstrumentedTestStage : InstrumentedStage {

    fun getMetadata(): TestMetadata

    fun getStages(): List<InstrumentedStage>

    fun getAttachments(): List<ReportAttachment>

    fun addMetadata(metadata: Map<String, Any>)

    fun attach(attachment: ReportAttachment)

    fun getAttachmentOutputStream(path: String): OutputStream

}

internal class InstrumentedTestStageImpl(
    val id: String,
    val title: String,
    val methodName: String,
    val className: String,
    val packageName: String,
    private val recordingOptions: RecordingOptions,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaInstrumentedReporter,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
) : AbstractStage(), InstrumentedTestStage, InstrumentedTestScope {

    private val extraMetadata = mutableMapOf<String, Any>()
    private val stages = mutableListOf<InstrumentedStage>()
    private val attachments = mutableListOf<ReportAttachment>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stepDelegate = InstrumentedStepDelegate(
        outputPath = outputDir,
        interceptors = interceptors,
        reporter = reporter,
        screenshotOptions = screenshotOptions
    )
    private var currentScenario: InstrumentedScenarioStageImpl? = null
    private var screenRecording: ReportRecording? = null

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = stepDelegate.createStep(title, id)
        stages.add(step)
        stepDelegate.executeStep(action)
    }

    override fun scenario(scenario: InstrumentedTestScenario) {
        stepDelegate.clearStep()
        currentScenario = null
        val scenarioReport = createScenarioReport(scenario)
        stages.add(scenarioReport)
        intercept { onStageStarted(this@InstrumentedTestStageImpl) }
        scenarioReport.report(scenario)
        intercept { onStagePassed(this@InstrumentedTestStageImpl) }
    }

    override fun getStages(): List<InstrumentedStage> = stages.toList()

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    override fun addMetadata(metadata: Map<String, Any>) {
        extraMetadata.putAll(metadata)
    }

    override fun attach(attachment: ReportAttachment) {
        attachments.add(attachment)
    }

    override fun getMetadata(): TestMetadata {
        return TestMetadata(
            testId = id,
            testTitle = title,
            packageName = packageName,
            className = className,
            methodName = methodName,
            extra = extraMetadata.toMap()
        )
    }

    override fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputDir/$path"
        return TestStorageProvider.getOutputStream(relativePath)
    }

    internal fun starting(description: Description) {
        intercept { onTestStarted(this@InstrumentedTestStageImpl, description) }
        if (recordingOptions.enabled) {
            val filename = reporter.getRecordingName(IdGenerator.get())
            screenRecording = DeviceScreenRecorder.startRecording(
                filename = filename,
                options = recordingOptions,
                relativeOutputDirPath = outputDir
            )
        }
    }

    internal fun failed(error: Throwable) {
        screenRecording?.let {
            attachments.add(createRecordingAttachment(it))
            DeviceScreenRecorder.stopRecording(it, delete = false)
        }
        stepDelegate.currentStep?.let { step ->
            step.fail(error)
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            intercept { onStageFailed(step) }
        }
        currentScenario?.let { scenario ->
            scenario.fail(error)
            intercept { onStageFailed(scenario) }
        }
        stepDelegate.clearStep()
        currentScenario = null
        fail(error)
        intercept { onTestFailed(this@InstrumentedTestStageImpl) }
        writeReport()
    }

    internal fun succeeded() {
        stepDelegate.clearStep()
        currentScenario = null
        pass()
        intercept { onTestPassed(this@InstrumentedTestStageImpl) }
        screenRecording?.let {
            val delete = !recordingOptions.keepOnSuccess
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = delete
            )
            if (delete) {
                screenRecording = null
            } else {
                attachments.add(createRecordingAttachment(it))
            }
        }
        writeReport()
    }

    private fun createScenarioReport(scenario: InstrumentedTestScenario): InstrumentedScenarioStageImpl {
        return InstrumentedScenarioStageImpl(
            id = getScenarioId(scenario),
            delegate = stepDelegate,
            name = scenario.name
        )
    }

    private fun getScenarioId(scenario: InstrumentedTestScenario): String {
        return scenario.id ?: IdGenerator.get()
    }

    private fun writeReport() {
        val file = "$outputDir/${reporter.getReportFilename(this)}"
        val outputStream = TestStorageProvider.getOutputStream(file)
        try {
            reporter.writeTestReport(this, outputStream)
        } catch (exception: Exception) {
            Log.e("CariocaReport", "Failed writing report for test ${this.methodName}", exception)
        } finally {
            outputStream.close()
        }
    }

    private fun intercept(action: CariocaInstrumentedInterceptor.() -> Unit) {
        interceptors.intercept(action)
    }

    private fun createRecordingAttachment(recording: ReportRecording): ReportAttachment {
        return ReportAttachment(
            description = "Screen recording",
            path = recording.relativeFilePath,
            mimeType = "video/mp4"
        )
    }

    override fun toString(): String {
        return "Test(id='$id', name='$methodName', className='$className')"
    }

}
