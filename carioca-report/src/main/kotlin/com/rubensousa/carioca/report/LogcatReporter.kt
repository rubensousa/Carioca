package com.rubensousa.carioca.report

import android.util.Log
import com.rubensousa.carioca.report.stage.TestSuiteReport
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import org.junit.runner.Description
import java.io.OutputStream

class LogcatReporter : CariocaReporter {

    override val filename: String
        get() = ""

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

    override fun onScenarioStarted(scenario: ScenarioReport) {
        log("Scenario started: ${scenario.id}")
    }

    override fun onScenarioPassed(scenario: ScenarioReport) {
        log("Step passed: ${scenario.id}")
    }

    override fun onScenarioFailed(scenario: ScenarioReport) {
        log("Step passed: ${scenario.id}")
    }

    override fun onStepStarted(step: StepReport) {
        log("Step started: ${step.title}")
    }

    override fun onStepPassed(step: StepReport) {
        log("Step passed: ${step.title}")
    }

    override fun onStepFailed(step: StepReport) {
        log("Step failed: ${step.title}")
    }

    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        // No-op
    }

    override fun writeTestSuiteReport(report: TestSuiteReport, outputStream: OutputStream) {
        // No-op
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable) {
        Log.e(tag, message, error)
    }

}
