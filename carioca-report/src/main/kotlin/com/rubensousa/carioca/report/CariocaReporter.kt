package com.rubensousa.carioca.report

import android.net.Uri
import com.rubensousa.carioca.report.stage.TestReport
import java.io.OutputStream


interface CariocaReporter {

    /**
     * @param report the test report to be saved
     * @param outputDir the root directory of the test storage
     * @return The output directory for this report
     */
    fun getOutputDir(report: TestReport, outputDir: Uri): String

    /**
     * @return the filename of the report
     */
    fun getReportFilename(report: TestReport): String

    /**
     * @return the filename of the screenshot, excluding the extension
     */
    fun getScreenshotName(id: String): String

    /**
     * @param report test report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeTestReport(report: TestReport, outputStream: OutputStream)

}
