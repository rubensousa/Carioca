package com.rubensousa.carioca.core

import com.rubensousa.carioca.core.internal.Test
import com.rubensousa.carioca.core.internal.TestReportBuilder
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A test rule that builds a detailed report for a test, including its steps.
 *
 * Use [step] to create an individual test step for granular reports.
 */
class CariocaReportRule(
    private val reporters: List<CariocaReporter>,
) : TestWatcher() {

    private var test: Test? = null
    private val reportBuilder = TestReportBuilder

    constructor(reporter: CariocaReporter) : this(listOf(reporter))

    override fun starting(description: Description) {
        super.starting(description)
        test = reportBuilder.newTest(description)
        forEachReporter { onTestStarted(description) }
    }

    override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        test?.getLastStep()?.let { step ->
            step.fail()
            forEachReporter { onStepFailed(step) }
        }
        test?.fail()
        forEachReporter { onTestFailed(e, description) }
    }

    override fun succeeded(description: Description) {
        super.succeeded(description)
        forEachReporter { onTestPassed(description) }
        test?.pass()
    }

    fun step(title: String, action: TestStep.() -> Unit) {
        val step = createStep(title)
        forEachReporter { onStepStarted(step) }
        action(step)
        step.pass()
        forEachReporter { onStepPassed(step) }
    }

    private fun createStep(title: String): TestStep {
        val currentTest = requireNotNull(test) { "Test not started yet" }
        return currentTest.newStep(title)
    }

    private fun forEachReporter(action: CariocaReporter.() -> Unit) {
        reporters.forEach { reporter ->
            action(reporter)
        }
    }

}


fun report(reportRule: CariocaReportRule, block: CariocaReportRule.() -> Unit) {
    block(reportRule)
}
