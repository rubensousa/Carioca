package com.rubensousa.carioca.report

import android.net.Uri
import com.rubensousa.carioca.report.stage.TestReport
import java.io.OutputStream


interface CariocaReporter {

    fun getOutputDir(report: TestReport, outputDir: Uri): String

    fun getReportFilename(report: TestReport): String

    fun getScreenshotName(id: String): String

    /**
     * @param report test report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeTestReport(report: TestReport, outputStream: OutputStream)

}
