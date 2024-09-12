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

package com.rubensousa.carioca.android.report.stage.test

import android.util.Log
import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.recording.ReportRecording
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenarioDelegate
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepDelegate
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.android.report.storage.IdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.stage.AbstractCariocaStage
import com.rubensousa.carioca.stage.CariocaStage
import com.rubensousa.carioca.stage.StageStack
import org.junit.runner.Description
import java.io.OutputStream


interface InstrumentedTestStage : CariocaStage {

    fun getMetadata(): TestMetadata

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
) : AbstractCariocaStage(), InstrumentedTestStage, InstrumentedTestScope {

    private val extraMetadata = mutableMapOf<String, Any>()
    private val childStages = mutableListOf<CariocaStage>()
    private val stageStack = StageStack()
    private val attachments = mutableListOf<ReportAttachment>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stepDelegate = InstrumentedStepDelegate(
        stack = stageStack,
        reporter = reporter,
        interceptors = interceptors,
        outputPath = outputDir,
        screenshotOptions = screenshotOptions,
    )
    private val scenarioDelegate = InstrumentedScenarioDelegate(
        stack = stageStack,
        stepDelegate = stepDelegate,
        interceptors = interceptors
    )
    private var screenRecording: ReportRecording? = null

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = stepDelegate.create(title, id)
        childStages.add(step)
        stepDelegate.execute(step, action)
    }

    override fun scenario(scenario: InstrumentedTestScenario) {
        val newScenario = scenarioDelegate.create(scenario)
        childStages.add(newScenario)
        scenarioDelegate.execute(newScenario)
    }

    override fun getStages(): List<CariocaStage> = childStages.toList()

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
        // Take a screenshot asap to record the state on failures
        stepDelegate.takeScreenshot("Screenshot of Failure")?.let {
            attachments.add(it)
        }

        // Now stop the recording, if there is one, and attach it
        screenRecording?.let { activeRecording ->
            attachments.add(createRecordingAttachment(activeRecording))
            DeviceScreenRecorder.stopRecording(
                recording = activeRecording,
                delete = false
            )
        }

        // Now loop through the entire stage stack and mark all stages as failed
        var stage = stageStack.pop()
        while (stage != null) {
            val currentStage = stage
            currentStage.fail(error)
            intercept { onStageFailed(currentStage) }
            stage = stageStack.pop()
        }

        fail(error)
        intercept { onTestFailed(this@InstrumentedTestStageImpl) }
        writeReport()
    }

    internal fun succeeded() {
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

    // Ensures there is no persistent state across test re-executions
    private fun reset() {
        childStages.clear()
        stageStack.clear()
        extraMetadata.clear()
        attachments.clear()
        screenRecording = null
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
        reset()
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
