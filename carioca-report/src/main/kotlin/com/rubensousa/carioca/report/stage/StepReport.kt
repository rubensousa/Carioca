package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.report.screenshot.ReportScreenshot
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

/**
 * Public API for a step block
 */
interface StepReportScope {

    /**
     * Takes a screenshot with the configuration set through [ScreenshotOptions].
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     */
    fun screenshot(description: String)

}

class StepReport internal constructor(
    id: String,
    val title: String,
    val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaReporter,
) : StageReport(id), StepReportScope {

    private val screenshots = mutableListOf<ReportScreenshot>()

    override fun screenshot(description: String) {
        val screenshot = takeScreenshot(description)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
    }

    internal fun report(action: StepReportScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    fun getScreenshots(): List<ReportScreenshot> {
        return screenshots.toList()
    }

    private fun takeScreenshot(description: String): ReportScreenshot? {
        val screenshotUri = DeviceScreenshot.take(
            storageDir = TestStorageProvider.getOutputUri(outputPath),
            options = screenshotOptions,
            filename = reporter.getScreenshotName(IdGenerator.get())
        ) ?: return null
        return ReportScreenshot(
            path = screenshotUri.path!!,
            description = description,
            extension = screenshotOptions.getFileExtension()
        )
    }

}
