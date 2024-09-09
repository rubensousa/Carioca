package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.internal.TestReportWriter
import com.rubensousa.carioca.report.recording.ScreenRecorder
import com.rubensousa.carioca.report.recording.ScreenRecording
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.scope.ReportTestScope
import org.junit.runner.Description

class TestReport internal constructor(
    id: String,
    val recordingOptions: RecordingOptions,
    val name: String,
    val className: String,
    val packageName: String,
    val reporter: CariocaReporter,
    private val interceptor: CariocaInterceptor?,
) : StageReport(id), ReportTestScope {

    private val stageReports = mutableListOf<StageReport>()
    private val recordings = mutableListOf<ScreenRecording>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stepDelegate = StepReportDelegate(
        outputPath = outputDir,
        interceptor = interceptor,
        reporter = reporter
    )
    private var currentScenario: ScenarioReport? = null
    private var screenRecording: ScreenRecording? = null

    fun getStageReports(): List<StageReport> = stageReports.toList()

    override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
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
            screenRecording = ScreenRecorder.startRecording(filename, recordingOptions, outputDir)
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
            ScreenRecorder.stopRecording(it, delete = false)
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
            ScreenRecorder.stopRecording(
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
