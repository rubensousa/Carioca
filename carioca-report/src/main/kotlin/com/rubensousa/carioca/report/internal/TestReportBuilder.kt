package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.CariocaLogger
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.annotations.TestId
import com.rubensousa.carioca.report.stage.ReportStatus
import com.rubensousa.carioca.report.stage.TestReport
import com.rubensousa.carioca.report.stage.TestSuiteReport
import org.junit.runner.Description

internal object TestReportBuilder {

    private val tests = mutableListOf<TestReport>()
    private var startTime = System.currentTimeMillis()

    fun newTest(
        description: Description,
        logger: CariocaLogger?,
        reporter: CariocaReporter,
    ): TestReport {
        val test = createTest(description, logger, reporter)
        tests.add(test)
        return test
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        tests.clear()
    }

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

    private fun createTest(
        description: Description,
        logger: CariocaLogger?,
        reporter: CariocaReporter,
    ): TestReport {
        return TestReport(
            id = getTestId(description),
            name = description.methodName,
            className = description.className,
            packageName = description.testClass.`package`?.name ?: "",
            logger = logger,
            reporter = reporter
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
