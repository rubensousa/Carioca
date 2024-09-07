package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.ReportScenario
import com.rubensousa.carioca.report.stage.ReportStep
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
interface CariocaReport {

    fun onTestStarted(description: Description)

    fun onScenarioStarted(scenario: ReportScenario)

    fun onStepStarted(step: ReportStep)

    fun onStepPassed(step: ReportStep)

    fun onScenarioPassed(scenario: ReportScenario)

    fun onTestPassed(description: Description)

    fun onStepFailed(step: ReportStep)

    fun onScenarioFailed(scenario: ReportScenario)

    fun onTestFailed(error: Throwable, description: Description)

}
