package com.rubensousa.carioca.report.internal

import android.util.Log
import com.rubensousa.carioca.report.stage.TestReport

internal object TestReportWriter {

    fun write(report: TestReport) {
        val reporter = report.reporter
        val dir = TestStorageProvider.getTestOutputDir(report, reporter)
        val file = "$dir/${reporter.getReportFilename(report)}"
        val outputStream = TestStorageProvider.getOutputStream(file)
        try {
            reporter.writeTestReport(report, outputStream)
        } catch (exception: Exception) {
            Log.e("CariocaReport", "Failed writing report for test ${report.name}", exception)
        } finally {
            outputStream.close()
        }
    }

}
