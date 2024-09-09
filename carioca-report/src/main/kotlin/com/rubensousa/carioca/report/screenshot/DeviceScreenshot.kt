package com.rubensousa.carioca.report.screenshot

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.TestStorageProvider
import java.io.BufferedOutputStream
import java.io.IOException

object DeviceScreenshot {

    private const val TAG = "DeviceScreenshot"

    /**
     * @param storageDir the target directory from the test storage where the screenshot will be saved
     * @param options the configurable options for this screenshot
     * @param filename the desired screenshot filename. Defaults to a random uuid
     * @return a [Uri] that points to the new screenshot
     */
    fun take(
        storageDir: Uri,
        options: ScreenshotOptions,
        filename: String? = null,
    ): Uri? {
        val screenshot: Bitmap? = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        if (screenshot == null) {
            Log.w(TAG, "Failed to take screenshot")
            return null
        }
        try {
            val screenshotName = filename ?: IdGenerator.get()
            val path = storageDir.path!! + "/$screenshotName${options.getFileExtension()}"
            val outputUri = TestStorageProvider.getOutputUri(path)
            val outputStream = TestStorageProvider.getOutputStream(outputUri)
            BufferedOutputStream(outputStream).use { stream ->
                val scaledScreenshot = Bitmap.createScaledBitmap(
                    screenshot,
                    Math.round(options.scale * screenshot.width),
                    Math.round(options.scale * screenshot.height),
                    false
                )
                scaledScreenshot.compress(options.format, options.quality, stream)
                stream.flush()
                return outputUri
            }
        } catch (exception: IOException) {
            Log.w(TAG, "Failed to take screenshot", exception)
            return null
        } finally {
            screenshot.recycle()
        }
    }


}
