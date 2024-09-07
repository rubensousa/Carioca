package com.rubensousa.carioca.core

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.core.internal.TestOutputLocation
import java.io.BufferedOutputStream
import java.io.IOException

data class TestStep(
    val id: String,
    val title: String,
    val outputDir: Uri,
) {

    var status = ReportStatus.SKIPPED
        private set
    var startTime = System.currentTimeMillis()
        private set
    var endTime = startTime
        private set

    private val TAG = "CariocaTestStep"
    private val screenshots = mutableListOf<TestScreenshot>()

    internal fun pass() {
        status = ReportStatus.PASSED
        saveEndTime()
    }

    internal fun fail() {
        status = ReportStatus.FAILED
        saveEndTime()
        // Take a screenshot to record the state on failures
        screenshot("Failed")
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

    fun screenshot(description: String) {
        val screenshot = takeScreenshot(description)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
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
