package com.rubensousa.carioca.report.screenshot

/**
 * A screenshot taken during a test report
 *
 * @param path the relative path to the test storage
 * @param description the description of this screenshot
 * @param extension the file extension of this screenshot
 */
data class ReportScreenshot(
    val path: String,
    val description: String,
    val extension: String,
)
