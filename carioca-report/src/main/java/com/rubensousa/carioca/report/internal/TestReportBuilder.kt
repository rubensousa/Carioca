package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.CariocaReport
import com.rubensousa.carioca.report.annotations.TestId
import com.rubensousa.carioca.report.stage.ReportTest
import org.junit.runner.Description

internal object TestReportBuilder {

    private val tests = mutableListOf<ReportTest>()
    private var startTime = System.currentTimeMillis()

    fun newTest(description: Description, reports: List<CariocaReport>): ReportTest {
        val test = createTest(description, reports)
        tests.add(test)
        return test
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        tests.clear()
    }

    fun build(): TestReport {
        return TestReport(
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            tests = tests.toList(),
            id = IdGenerator.get()
        )
    }

    private fun createTest(description: Description, reports: List<CariocaReport>): ReportTest {
        return ReportTest(
            id = getTestId(description),
            name = description.methodName,
            className = description.className,
            outputDir = TestOutputLocation.getOutputPath(description),
            reporters = reports
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
