package com.rubensousa.carioca.report.internal

import android.util.Log
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.stage.TestReport
import com.rubensousa.carioca.report.stage.TestSuiteReport

internal object TestReportWriter {

    fun write(report: TestReport) {
        report.reporters.forEach { reporter ->
            val outputStream = TestOutputLocation.getOutputStream(
                dir = report.outputDir,
                filename = reporter.filename
            )
            try {
                reporter.writeTestReport(report, outputStream)
            } catch (exception: Exception) {
                Log.e("CariocaReport", "Failed writing report for test ${report.name}", exception)
            } finally {
                outputStream.close()
            }
        }
    }

    fun write(report: TestSuiteReport) {
        val uniqueReporters = getUniqueReporters(report.tests)
        uniqueReporters.forEach { reporter ->
            val outputStream = TestOutputLocation.getGlobalReportOutputStream(reporter.filename)
            try {
                reporter.writeTestSuiteReport(report, outputStream)
            } catch (exception: Exception) {
                Log.e("CariocaReport", "Failed writing test suite report", exception)
            } finally {
                outputStream.close()
            }
        }
    }

    private fun getUniqueReporters(tests: List<TestReport>): List<CariocaReporter> {
        val reporters = mutableListOf<CariocaReporter>()
        tests.forEach { test ->
            test.reporters.forEach { reporter ->
                if (!reporters.any { it::class == reporter::class }) {
                    reporters.add(reporter)
                }
            }
        }
        return reporters
    }

}
