package com.rubensousa.carioca.core

import com.rubensousa.carioca.core.internal.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CariocaReportRule(
    private val reporter: CariocaReporter,
) : TestWatcher() {

    private val steps = mutableListOf<TestStep>()
    private var test: Test? = null

    override fun starting(description: Description) {
        super.starting(description)
        test = Test.from(description)
        reporter.onTestStarted(description)
    }

    override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        steps.lastOrNull()?.fail()
        reporter.onTestFailed(e, description)
    }

    override fun succeeded(description: Description) {
        super.succeeded(description)
        reporter.onTestPassed(description)
    }

    fun step(title: String, action: TestStep.() -> Unit) {
        val currentTest = requireNotNull(test) { "Test not started yet" }
        val step = TestStep(
            outputDir = currentTest.outputDir,
            title = title
        )
        steps.add(step)
        action(step)
        step.pass()
    }

}


fun report(reportRule: CariocaReportRule, block: CariocaReportRule.() -> Unit) {
    block(reportRule)
}
