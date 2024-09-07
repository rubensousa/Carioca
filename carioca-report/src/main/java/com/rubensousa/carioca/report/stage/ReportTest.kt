package com.rubensousa.carioca.report.stage

import android.net.Uri
import com.rubensousa.carioca.report.CariocaReport
import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.TestOutputLocation
import com.rubensousa.carioca.report.scope.ReportScenarioScope
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.scope.ReportTestScope
import org.junit.runner.Description

internal class ReportTest(
    id: String,
    val name: String,
    val className: String,
    val outputDir: Uri,
    val reporters: List<CariocaReport>,
) : ReportStage(id), ReportTestScope {

    private var currentStep: ReportStep? = null
    private var currentScenario: ReportScenario? = null
    private val stages = mutableListOf<ReportStage>()
    private val scenarioScope = object : ReportScenarioScope {
        override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
            this@ReportTest.step(title, id, action)
        }
    }

    override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
        val step = createStep(title, id)
        currentStep = step
        forEachReporter { onStepStarted(step) }
        step.run(action)
        forEachReporter { onStepPassed(step) }
        currentStep = null
    }

    override fun scenario(scenario: TestScenario) {
        currentStep = null
        currentScenario = null
        val newScenario = createScenario(scenario)
        forEachReporter { onScenarioStarted(newScenario) }
        newScenario.run(scenarioScope)
        forEachReporter { onScenarioPassed(newScenario) }
    }

    internal fun starting(description: Description) {
        forEachReporter { onTestStarted(description) }
    }

    internal fun failed(error: Throwable, description: Description) {
        currentStep?.let { step ->
            step.fail()
            // Take a screenshot to record the state on failures
            step.screenshot("Failed")
            forEachReporter { onStepFailed(step) }
        }
        currentScenario?.let { scenario ->
            scenario.fail()
            forEachReporter { onScenarioFailed(scenario) }
        }
        currentStep = null
        currentScenario = null
        fail()
        forEachReporter { onTestFailed(error, description) }
    }

    internal fun succeeded(description: Description) {
        currentStep = null
        currentScenario = null
        pass()
        forEachReporter { onTestPassed(description) }
    }

    private fun newScenario(scenario: TestScenario): ReportScenario {
        return ReportScenario(
            id = getScenarioId(scenario),
            delegate = scenario,
        )
    }

    private fun createStep(title: String, id: String?): ReportStep {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = ReportStep(
            id = stepId,
            outputDir = TestOutputLocation.getStepUri(outputDir, uniqueId),
            title = title,
        )
        stages.add(step)
        return step
    }

    internal fun getStages(): List<ReportStage> = stages.toList()

    private fun getScenarioId(scenario: TestScenario): String {
        val scenarioId = scenario::class.java.getAnnotation(ScenarioId::class.java)
        return scenarioId?.id ?: IdGenerator.get()
    }

    private fun createScenario(scenario: TestScenario): ReportScenario {
        return newScenario(scenario)
    }

    override fun toString(): String {
        return "Test(id='$id', name='$name', className='$className')"
    }

    private fun forEachReporter(action: CariocaReport.() -> Unit) {
        reporters.forEach { reporter ->
            action(reporter)
        }
    }

}
