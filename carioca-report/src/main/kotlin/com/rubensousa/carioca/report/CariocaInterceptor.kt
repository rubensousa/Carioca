package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import org.junit.runner.Description

/**
 * Lifecycle of a report:
 *
 * 1. [onTestStarted]
 * 2. [onScenarioStarted] if there is a scenario
 * 3. [onStepStarted]
 * 4. [onStepPassed] or [onStepFailed]
 * 5. [onScenarioPassed] or [onScenarioFailed] if a scenario was started
 * 6. [onTestPassed] or [onTestFailed]
 */
interface CariocaInterceptor {

    fun onTestStarted(description: Description) {}

    fun onScenarioStarted(scenario: ScenarioReport) {}

    fun onStepStarted(step: StepReport) {}

    fun onStepPassed(step: StepReport) {}

    fun onScenarioPassed(scenario: ScenarioReport) {}

    fun onTestPassed(description: Description) {}

    fun onStepFailed(step: StepReport) {}

    fun onScenarioFailed(scenario: ScenarioReport) {}

    fun onTestFailed(error: Throwable, description: Description) {}

}
