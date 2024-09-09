package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.annotations.TestId
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.stage.ReportStatus
import com.rubensousa.carioca.report.stage.TestReport
import com.rubensousa.carioca.report.stage.TestSuiteReport
import org.junit.runner.Description

internal object TestReportBuilder {

    private val tests = mutableListOf<TestReport>()
    private var startTime = System.currentTimeMillis()

    // TODO: Get the previous test if this test was retried with a retry rule
    fun newTest(
        description: Description,
        recordingOptions: RecordingOptions,
        logger: CariocaInterceptor?,
        reporter: CariocaReporter,
    ): TestReport {
        val test = TestReport(
            id = getTestId(description),
            recordingOptions = recordingOptions,
            name = description.methodName,
            className = description.className,
            packageName = description.testClass.`package`?.name ?: "",
            interceptor = logger,
            reporter = reporter
        )
        tests.add(test)
        return test
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        tests.clear()
    }

    // TODO: Write the suite report too, since we want to know the suite duration
    fun buildSuiteReport(): TestSuiteReport {
        val hasAnyFailure = tests.any { it.status == ReportStatus.FAILED }
        return TestSuiteReport(
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            tests = tests.toList(),
            status = if (hasAnyFailure) {
                ReportStatus.FAILED
            } else {
                ReportStatus.PASSED
            },
            id = IdGenerator.get()
        )
    }

    private fun getTestId(description: Description): String {
        val testId = description.getAnnotation(TestId::class.java)
        return testId?.id ?: getDefaultTestId(description)
    }

    private fun getDefaultTestId(description: Description): String {
        return "${description.className}.${description.methodName}"
    }

}
