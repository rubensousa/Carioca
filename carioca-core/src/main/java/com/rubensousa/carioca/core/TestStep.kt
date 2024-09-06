package com.rubensousa.carioca.core

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.core.internal.ResultState
import com.rubensousa.carioca.core.internal.TestOutputLocation
import com.rubensousa.carioca.core.internal.TestScreenshot
import java.io.BufferedOutputStream
import java.io.IOException

class TestStep(
    private val outputDir: Uri,
    private val title: String,
) {

    private val TAG = "CariocaTestStep"
    private val screenshots = mutableListOf<TestScreenshot>()
    private var state = ResultState.SKIPPED
    private var startTime = System.currentTimeMillis()
    private var endTime = startTime

    internal fun pass() {
        state = ResultState.PASSED
        saveEndTime()
    }

    internal fun fail() {
        state = ResultState.FAILED
        saveEndTime()
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

    fun screenshot(description: String, scale: Float = 0.5f) {
        val screenshot = takeScreenshot(description, scale)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
    }

    private fun takeScreenshot(description: String, scale: Float): TestScreenshot? {
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
                    Math.round(scale * screenshot.width),
                    Math.round(scale * screenshot.height),
                    false
                )
                scaledScreenshot.compress(Bitmap.CompressFormat.PNG, 70, stream)
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

}
