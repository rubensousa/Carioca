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

package com.rubensousa.carioca.report.screenshot

import android.graphics.Bitmap

data class ScreenshotOptions(
    val scale: Float = 0.5f,
    val quality: Int = 80,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
) {

    init {
        require(quality in 1..100)
        require(scale > 0f && scale <= 1.0f)
    }

    fun getFileExtension(): String {
        return when (format) {
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.JPEG -> ".jpg"
            else -> ".webp"
        }
    }

}
