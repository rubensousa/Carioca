package com.rubensousa.carioca.report

import android.util.Log
import com.rubensousa.carioca.report.stage.ReportScenario
import com.rubensousa.carioca.report.stage.ReportStep
import org.junit.runner.Description

class LogcatReport : CariocaReport {

    private val tag = "CariocaReport"

    override fun onTestStarted(description: Description) {
        log("Test started: $description")
    }

    override fun onTestFailed(error: Throwable, description: Description) {
        log("Test failed: $description", error)
    }

    override fun onTestPassed(description: Description) {
        log("Test passed: $description")
    }

    override fun onScenarioStarted(scenario: ReportScenario) {
        log("Scenario started: ${scenario.id}")
    }

    override fun onScenarioPassed(scenario: ReportScenario) {
        log("Step passed: ${scenario.id}")
    }

    override fun onScenarioFailed(scenario: ReportScenario) {
        log("Step passed: ${scenario.id}")
    }

    override fun onStepStarted(step: ReportStep) {
        log("Step started: ${step.title}")
    }

    override fun onStepPassed(step: ReportStep) {
        log("Step passed: ${step.title}")
    }

    override fun onStepFailed(step: ReportStep) {
        log("Step failed: ${step.title}")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable) {
        Log.e(tag, message, error)
    }

}
