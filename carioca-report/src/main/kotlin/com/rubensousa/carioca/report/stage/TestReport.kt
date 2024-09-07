package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.CariocaLogger
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestOutputLocation
import com.rubensousa.carioca.report.internal.TestReportWriter
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.scope.ReportTestScope
import org.junit.runner.Description

class TestReport internal constructor(
    id: String,
    val name: String,
    val className: String,
    val packageName: String,
    val reporter: CariocaReporter,
    private val logger: CariocaLogger?,
) : StageReport(id), ReportTestScope {

    private val stageReports = mutableListOf<StageReport>()
    private val stepDelegate = StepReportDelegate(
        outputPath = reporter.getOutputDir(this, TestOutputLocation.getRootOutputDir()),
        logger = logger,
        reporter = reporter
    )
    private var currentScenario: ScenarioReport? = null

    fun getStageReports(): List<StageReport> = stageReports.toList()

    override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
        stageReports.add(stepDelegate.step(title, id, action))
    }

    override fun scenario(scenario: TestScenario) {
        stepDelegate.clearStep()
        currentScenario = null
        val scenarioReport = createScenarioReport(scenario)
        stageReports.add(scenarioReport)
        log { onScenarioStarted(scenarioReport) }
        scenarioReport.report(scenario)
        log { onScenarioPassed(scenarioReport) }
    }

    internal fun starting(description: Description) {
        log { onTestStarted(description) }
    }

    internal fun failed(error: Throwable, description: Description) {
        stepDelegate.currentStep?.let { step ->
            step.fail()
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            log { onStepFailed(step) }
        }
        currentScenario?.let { scenario ->
            scenario.fail()
            log { onScenarioFailed(scenario) }
        }
        stepDelegate.clearStep()
        currentScenario = null
        fail()
        log { onTestFailed(error, description) }
        TestReportWriter.write(this)
    }

    internal fun succeeded(description: Description) {
        stepDelegate.clearStep()
        currentScenario = null
        pass()
        log { onTestPassed(description) }
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

    private fun log(action: CariocaLogger.() -> Unit) {
        if (logger != null) {
            with(logger) {
                action()
            }
        }
    }

}
