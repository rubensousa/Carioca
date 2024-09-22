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

package com.rubensousa.carioca.android.report.screenshot

import android.graphics.Bitmap
import org.junit.runner.Description

/**
 * Configurable screenshot options that apply to all tests.
 *
 * To override a configuration for a single test, annotate it with [TestScreenshot]
 *
 * @param scale the scale of the screenshot in relation to the original display size. Default: 0.5
 * @param quality the quality of the screenshot. From 0 to 100. Default: 80
 * @param format the image format of the screenshot. Default: JPG
 * @param keepOnSuccess true if images should be kept if the test passes. Default: true
 */
data class ScreenshotOptions(
    val scale: Float = 0.5f,
    val quality: Int = 80,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    val keepOnSuccess: Boolean = true
) {

    init {
        require(quality in 1..100)
        require(scale > 0f && scale <= 1.0f)
    }

    /**
     * @return the file extension of the [format] of these screenshot options
     */
    fun getFileExtension(): String {
        return when (format) {
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.JPEG -> ".jpg"
            else -> ".webp"
        }
    }

    companion object {

        fun from(description: Description): ScreenshotOptions? {
            val annotation = description.getAnnotation(TestScreenshot::class.java)
                ?: return null
            return ScreenshotOptions(
                scale = annotation.scale,
                quality = annotation.quality,
                keepOnSuccess = annotation.keepOnSuccess,
                format = annotation.format,
            )
        }

    }

}
