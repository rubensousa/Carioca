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

/**
 * Overrides the screenshot options for a single test
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TestScreenshot(
    /**
     * @see ScreenshotOptions.scale
     */
    val scale: Float = 0.5f,
    /**
     * @see ScreenshotOptions.quality
     */
    val quality: Int = 80,
    /**
     * @see ScreenshotOptions.format
     */
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    /**
     * @see ScreenshotOptions.keepOnSuccess
     */
    val keepOnSuccess: Boolean = true,
)
