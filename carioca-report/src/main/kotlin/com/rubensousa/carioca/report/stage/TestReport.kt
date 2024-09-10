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
import com.rubensousa.carioca.report.AttachmentRequest
import com.rubensousa.carioca.report.interceptor.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.interceptor.intercept
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.report.recording.ReportRecording
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import org.junit.runner.Description

/**
 * The public API for each report. This is the main entry for each test report.
 */
interface ReportTestScope {

    /**
     * Creates an individual section of a test
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: StepReportScope.() -> Unit)

    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     */
    fun scenario(scenario: TestScenario)

}

class TestReport internal constructor(
    id: String,
    val recordingOptions: RecordingOptions,
    val screenshotOptions: ScreenshotOptions,
    val methodName: String,
    val className: String,
    val packageName: String,
    val reporter: CariocaReporter,
    private val interceptors: List<CariocaInterceptor>,
) : StageReport(id), ReportTestScope {

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
    private var currentScenario: ScenarioReport? = null
    private var screenRecording: ReportRecording? = null
    private var failureCause: Throwable? = null

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
        intercept { onScenarioStarted(this@TestReport, scenarioReport) }
        scenarioReport.report(scenario)
        intercept { onScenarioPassed(this@TestReport, scenarioReport) }
    }

    fun getStageReports(): List<StageReport> = stageReports.toList()

    fun getRecording(): ReportRecording? = screenRecording

    fun attach(request: AttachmentRequest) {
        attachments.add(
            ReportAttachment(
                description = request.description,
                path = request.relativeFilePath,
                mimeType = request.mimeType
            )
        )
    }

    fun getAttachments(): List<ReportAttachment> = attachments.toList()

    fun getFailureCause(): Throwable? = failureCause

    fun createAttachment(
        filename: String,
        description: String,
        mimeType: String,
    ): AttachmentRequest {
        val path = "$outputDir/$filename"
        return AttachmentRequest(
            description = description,
            mimeType = mimeType,
            relativeFilePath = path,
            outputStream = TestStorageProvider.getOutputStream(path)
        )
    }

    internal fun starting(description: Description) {
        intercept { onTestStarted(this@TestReport, description) }
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
        failureCause = error
        stepDelegate.currentStep?.let { step ->
            step.fail()
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            intercept { onStepFailed(this@TestReport, step) }
        }
        screenRecording?.let {
            DeviceScreenRecorder.stopRecording(it, delete = false)
        }
        currentScenario?.let { scenario ->
            scenario.fail()
            intercept { onScenarioFailed(this@TestReport, scenario) }
        }
        stepDelegate.clearStep()
        currentScenario = null
        fail()
        intercept { onTestFailed(this@TestReport, error, description) }
        writeReport()
    }

    internal fun succeeded(description: Description) {
        stepDelegate.clearStep()
        currentScenario = null
        pass()
        intercept { onTestPassed(this@TestReport, description) }
        screenRecording?.let {
            val delete = !recordingOptions.keepOnSuccess
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = delete
            )
            if (delete) {
                screenRecording = null
            }
        }
        writeReport()
    }

    private fun createScenarioReport(scenario: TestScenario): ScenarioReport {
        return ScenarioReport(
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

    override fun toString(): String {
        return "Test(id='$id', name='$methodName', className='$className')"
    }

}
