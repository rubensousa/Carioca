package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.TestReport
import java.io.OutputStream

interface CariocaReporter {

    /**
     * @param report the test report to be saved
     * @return The output directory for this report
     */
    fun getOutputDir(report: TestReport): String

    /**
     * @return the filename of the report
     */
    fun getReportFilename(report: TestReport): String

    /**
     * @return the filename of the screenshot, excluding the extension
     */
    fun getScreenshotName(id: String): String

    /**
     * @return the filename of the recording, excluding the extension
     */
    fun getRecordingName(id: String): String

    /**
     * @param report test report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeTestReport(report: TestReport, outputStream: OutputStream)

}
