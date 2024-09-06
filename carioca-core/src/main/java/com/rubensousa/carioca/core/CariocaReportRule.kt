package com.rubensousa.carioca.core

import com.rubensousa.carioca.core.internal.IdGenerator
import com.rubensousa.carioca.core.internal.Test
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

    private val steps = mutableListOf<TestStep>()
    private var test: Test? = null

    constructor(reporter: CariocaReporter) : this(listOf(reporter))

    override fun starting(description: Description) {
        super.starting(description)
        test = Test.from(description)
        forEachReporter { onTestStarted(description) }
    }

    override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        steps.lastOrNull()?.let { step ->
            step.fail()
            forEachReporter { onStepFailed(step) }
        }
        forEachReporter { onTestFailed(e, description) }
    }

    override fun succeeded(description: Description) {
        super.succeeded(description)
        forEachReporter { onTestPassed(description) }
    }

    fun step(title: String, action: TestStep.() -> Unit) {
        val currentTest = requireNotNull(test) { "Test not started yet" }
        val stepId = IdGenerator.get()
        val step = TestStep(
            id = stepId,
            outputDir = currentTest.outputDir,
            title = title
        )
        steps.add(step)
        forEachReporter { onStepStarted(step) }
        action(step)
        step.pass()
        forEachReporter { onStepPassed(step) }
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
