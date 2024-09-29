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

package com.rubensousa.carioca.report.android.interceptor

import android.graphics.Bitmap
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport

/**
 * A [CariocaInstrumentedInterceptor] that triggers a screenshot when the test fails
 * using the configuration provided via [screenshotOptions]
 *
 * @param screenshotOptions the options for the screenshot file
 * @param description the description of the attachment file
 */
class TakeScreenshotOnFailureInterceptor(
    private val screenshotOptions: ScreenshotOptions = ScreenshotOptions(
        format = Bitmap.CompressFormat.PNG,
        scale = 1.0f
    ),
    private val description: String = "Screenshot of failure",
) : CariocaInstrumentedInterceptor {

    override fun onTestFailed(test: InstrumentedTestReport) {
        test.screenshot(
            description = description,
            options = screenshotOptions,
        )
    }

}
