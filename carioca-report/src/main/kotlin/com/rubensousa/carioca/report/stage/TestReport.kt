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

import com.rubensousa.carioca.report.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.internal.TestReportWriter
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
    val name: String,
    val className: String,
    val packageName: String,
    val reporter: CariocaReporter,
    private val interceptor: CariocaInterceptor?,
) : StageReport(id), ReportTestScope {

    private val stageReports = mutableListOf<StageReport>()
    private val recordings = mutableListOf<ReportRecording>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stepDelegate = StepReportDelegate(
        outputPath = outputDir,
        interceptor = interceptor,
        reporter = reporter,
        screenshotOptions = screenshotOptions
    )
    private var currentScenario: ScenarioReport? = null
    private var screenRecording: ReportRecording? = null

    fun getStageReports(): List<StageReport> = stageReports.toList()

    override fun step(title: String, id: String?, action: StepReportScope.() -> Unit) {
        stageReports.add(stepDelegate.step(title, id, action))
    }

    override fun scenario(scenario: TestScenario) {
        stepDelegate.clearStep()
        currentScenario = null
        val scenarioReport = createScenarioReport(scenario)
        stageReports.add(scenarioReport)
        intercept { onScenarioStarted(scenarioReport) }
        scenarioReport.report(scenario)
        intercept { onScenarioPassed(scenarioReport) }
    }

    internal fun starting(description: Description) {
        intercept { onTestStarted(description) }
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
        stepDelegate.currentStep?.let { step ->
            step.fail()
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            intercept { onStepFailed(step) }
        }
        screenRecording?.let {
            DeviceScreenRecorder.stopRecording(it, delete = false)
        }
        currentScenario?.let { scenario ->
            scenario.fail()
            intercept { onScenarioFailed(scenario) }
        }
        stepDelegate.clearStep()
        currentScenario = null
        fail()
        intercept { onTestFailed(error, description) }
        TestReportWriter.write(this)
    }

    internal fun succeeded(description: Description) {
        stepDelegate.clearStep()
        currentScenario = null
        pass()
        intercept { onTestPassed(description) }
        screenRecording?.let {
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = !recordingOptions.keepOnSuccess
            )
        }
        TestReportWriter.write(this)
    }

    private fun createScenarioReport(scenario: TestScenario): ScenarioReport {
        return ScenarioReport(
            id = getScenarioId(scenario),
            delegate = stepDelegate,
            name = scenario.name
        )
    }

    private fun getScenarioId(scenario: TestScenario): String {
        val scenarioId = scenario::class.java.getAnnotation(ScenarioId::class.java)
        return scenarioId?.id ?: IdGenerator.get()
    }

    override fun toString(): String {
        return "Test(id='$id', name='$name', className='$className')"
    }

    private fun intercept(action: CariocaInterceptor.() -> Unit) {
        if (interceptor != null) {
            with(interceptor) {
                action()
            }
        }
    }

}
