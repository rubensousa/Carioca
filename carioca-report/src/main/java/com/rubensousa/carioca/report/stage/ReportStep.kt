package com.rubensousa.carioca.report.stage

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.report.internal.TestOutputLocation
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.screenshot.CariocaScreenshots
import com.rubensousa.carioca.report.screenshot.TestScreenshot
import java.io.BufferedOutputStream
import java.io.IOException

class ReportStep(
    id: String,
    val title: String,
    val outputDir: Uri,
) : ReportStage(id), ReportStepScope {

    private val TAG = "CariocaTestStep"
    private val screenshots = mutableListOf<TestScreenshot>()

    override fun screenshot(description: String) {
        val screenshot = takeScreenshot(description)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
    }

    internal fun run(action: ReportStepScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    internal fun getScreenshots(): List<TestScreenshot> {
        return screenshots.toList()
    }

    private fun takeScreenshot(description: String): TestScreenshot? {
        val screenshot: Bitmap? = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        if (screenshot == null) {
            Log.w(TAG, "Failed to take screenshot")
            return null
        }
        val screenshotUri = TestOutputLocation.getScreenshotUri(outputDir)
        try {
            val outputStream = TestOutputLocation.getScreenshotOutputStream(screenshotUri)
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
                    uri = screenshotUri,
                    description = description
                )
            }
        } catch (exception: IOException) {
            Log.w(TAG, "Failed to take screenshot", exception)
            return null
        } finally {
            screenshot.recycle()
        }
    }

    override fun toString(): String {
        return "$title - $id"
    }

}
