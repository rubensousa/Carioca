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

package com.rubensousa.carioca.report.stage

import android.util.Log
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.interceptor.CariocaInterceptor
import com.rubensousa.carioca.report.interceptor.intercept
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.recording.ReportRecording
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import org.junit.runner.Description
import java.io.OutputStream


interface TestReport {

    fun getMetadata(): TestReportMetadata

    fun getStageReports(): List<StageReport>

    fun getAttachments(): List<ReportAttachment>

    fun addMetadata(metadata: Map<String, Any>)

    fun attach(attachment: ReportAttachment)

    fun getAttachmentOutputStream(path: String): OutputStream

}

data class TestReportMetadata(
    val execution: ExecutionMetadata,
    val testId: String,
    val testTitle: String,
    val packageName: String,
    val className: String,
    val methodName: String,
    val extra: Map<String, Any>,
) {

    fun getTestFullName(): String {
        return "$className.$methodName"
    }

}

internal class TestReportImpl(
    val id: String,
    val recordingOptions: RecordingOptions,
    val screenshotOptions: ScreenshotOptions,
    val title: String,
    val methodName: String,
    val className: String,
    val packageName: String,
    val reporter: CariocaReporter,
    private val interceptors: List<CariocaInterceptor>,
) : StageReport(), TestReport, TestReportScope {

    private val extraMetadata = mutableMapOf<String, Any>()
    private val stageReports = mutableListOf<StageReport>()
    private val attachments = mutableListOf<ReportAttachment>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stepDelegate = StepReportDelegate(
        report = this,
        outputPath = outputDir,
        interceptors = interceptors,
        reporter = reporter,
        screenshotOptions = screenshotOptions
    )
    private var currentScenario: ScenarioReportImpl? = null
    private var screenRecording: ReportRecording? = null

    override fun step(title: String, id: String?, action: StepReportScope.() -> Unit) {
        val step = stepDelegate.createStep(title, id)
        stageReports.add(step)
        stepDelegate.executeStep(action)
    }

    override fun scenario(scenario: TestScenario) {
        stepDelegate.clearStep()
        currentScenario = null
        val scenarioReport = createScenarioReport(scenario)
        stageReports.add(scenarioReport)
        intercept { onScenarioStarted(this@TestReportImpl, scenarioReport) }
        scenarioReport.report(scenario)
        intercept { onScenarioPassed(this@TestReportImpl, scenarioReport) }
    }

    override fun getStageReports(): List<StageReport> = stageReports.toList()

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    override fun addMetadata(metadata: Map<String, Any>) {
        extraMetadata.putAll(metadata)
    }

    override fun attach(attachment: ReportAttachment) {
        attachments.add(attachment)
    }

    override fun getMetadata(): TestReportMetadata {
        return TestReportMetadata(
            execution = getExecutionMetadata(),
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
        intercept { onTestStarted(this@TestReportImpl, description) }
        if (recordingOptions.enabled) {
            val filename = reporter.getRecordingName(IdGenerator.get())
            screenRecording = DeviceScreenRecorder.startRecording(
                filename = filename,
                options = recordingOptions,
                relativeOutputDirPath = outputDir
            )
        }
    }

    internal fun failed(error: Throwable, description: Description) {
        screenRecording?.let {
            attachments.add(createRecordingAttachment(it))
            DeviceScreenRecorder.stopRecording(it, delete = false)
        }
        stepDelegate.currentStep?.let { step ->
            step.fail(error)
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            intercept { onStepFailed(this@TestReportImpl, step) }
        }
        currentScenario?.let { scenario ->
            scenario.fail(error)
            intercept { onScenarioFailed(this@TestReportImpl, scenario) }
        }
        stepDelegate.clearStep()
        currentScenario = null
        fail(error)
        intercept { onTestFailed(this@TestReportImpl, error, description) }
        writeReport()
    }

    internal fun succeeded(description: Description) {
        stepDelegate.clearStep()
        currentScenario = null
        pass()
        intercept { onTestPassed(this@TestReportImpl, description) }
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

    private fun createScenarioReport(scenario: TestScenario): ScenarioReportImpl {
        return ScenarioReportImpl(
            id = getScenarioId(scenario),
            delegate = stepDelegate,
            name = scenario.name
        )
    }

    private fun getScenarioId(scenario: TestScenario): String {
        return scenario.getId() ?: IdGenerator.get()
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

    private fun intercept(action: CariocaInterceptor.() -> Unit) {
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
