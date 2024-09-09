package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.internal.TestReportBuilder
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.stage.ReportTestScope
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.TestReport
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * A test rule that builds a detailed report for a test, including its steps.
 *
 * Start by calling [report] to start the report. Then use either [ReportTestScope.step] or [ReportTestScope.scenario]
 * to start describing the report in detail
 *
 * Extend this class to provide a default report configuration across all tests
 */
open class CariocaReportRule(
    private val reporter: CariocaReporter,
    private val recordingOptions: RecordingOptions = RecordingOptions(),
    private val screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
    private val logger: CariocaInterceptor? = null,
) : TestWatcher() {

    private var test: TestReport? = null
    private val reportBuilder = TestReportBuilder

    final override fun starting(description: Description) {
        super.starting(description)
        test = reportBuilder.newTest(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            logger = logger,
            reporter = reporter
        )
        getCurrentTest().starting(description)
    }

    final override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        getCurrentTest().failed(e, description)
        test = null
    }

    final override fun succeeded(description: Description) {
        super.succeeded(description)
        getCurrentTest().succeeded(description)
        test = null
    }

    fun report(block: ReportTestScope.() -> Unit) {
        block(getCurrentTest())
    }

    private fun getCurrentTest(): TestReport {
        return requireNotNull(test) { "Test not started yet" }
    }

}
