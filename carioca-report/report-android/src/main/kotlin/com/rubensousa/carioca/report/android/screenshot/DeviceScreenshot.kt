/*
 * Copyright 2024 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.carioca.report.android.screenshot

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.test.core.app.takeScreenshotNoSync
import com.rubensousa.carioca.report.android.storage.FileIdGenerator
import com.rubensousa.carioca.report.android.storage.TestStorageProvider
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
    @SuppressLint("RestrictedApi")
    fun take(
        storageDir: Uri,
        options: ScreenshotOptions,
        filename: String? = null,
    ): Uri? {
        val screenshot: Bitmap? = try {
            takeScreenshotNoSync()
        } catch (exception: Exception) {
            null
        }
        if (screenshot == null) {
            Log.w(TAG, "Failed to take screenshot")
            return null
        }
        try {
            val screenshotName = filename ?: FileIdGenerator.get()
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
