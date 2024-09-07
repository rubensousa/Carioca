package com.rubensousa.carioca.report.stage

import android.graphics.Bitmap
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.internal.TestOutputLocation
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.screenshot.CariocaScreenshots
import com.rubensousa.carioca.report.screenshot.TestScreenshot
import java.io.BufferedOutputStream
import java.io.IOException

class StepReport internal constructor(
    id: String,
    val title: String,
    val outputPath: String,
    private val reporter: CariocaReporter,
) : StageReport(id), ReportStepScope {

    private val TAG = "CariocaTestStep"
    private val screenshots = mutableListOf<TestScreenshot>()

    override fun screenshot(description: String) {
        val screenshot = takeScreenshot(description)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
    }

    internal fun report(action: ReportStepScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    fun getScreenshots(): List<TestScreenshot> {
        return screenshots.toList()
    }

    private fun takeScreenshot(description: String): TestScreenshot? {
        val screenshot: Bitmap? = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        if (screenshot == null) {
            Log.w(TAG, "Failed to take screenshot")
            return null
        }
        try {
            val screenshotUri = TestOutputLocation.getScreenshotUri(reporter, outputPath, getImageExtension())
            val outputStream = TestOutputLocation.getOutputStream(screenshotUri)
            BufferedOutputStream(outputStream).use { stream ->
                val scaledScreenshot = Bitmap.createScaledBitmap(
                    screenshot,
                    Math.round(CariocaScreenshots.scale * screenshot.width),
                    Math.round(CariocaScreenshots.scale * screenshot.height),
                    false
                )
                scaledScreenshot.compress(CariocaScreenshots.format, CariocaScreenshots.quality, stream)
                stream.flush()
                return TestScreenshot(
                    path = screenshotUri.path!!,
                    description = description,
                    extension = getImageExtension()
                )
            }
        } catch (exception: IOException) {
            Log.w(TAG, "Failed to take screenshot", exception)
            return null
        } finally {
            screenshot.recycle()
        }
    }

    private fun getImageExtension(): String {
        return when (CariocaScreenshots.format) {
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.JPEG -> ".jpg"
            else -> ".webp"
        }
    }

    override fun toString(): String {
        return "$title - $id"
    }

}
