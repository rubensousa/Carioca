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

package com.rubensousa.carioca.android.report.sample.test

import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.android.report.sample.SampleInstrumentedReportRule
import com.rubensousa.carioca.report.android.screenshot.TestScreenshot
import org.junit.Rule
import org.junit.Test

class SampleScreenshotTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Overrides the screenshot options
    @TestScreenshot(
        scale = 1.0f,
        format = Bitmap.CompressFormat.PNG,
    )
    @Test
    fun testScreenshotOverride() = report {

        Given("User opens notifications") {
            device.openNotification()
            Thread.sleep(1000L)
            screenshot("Notifications opened")
        }

        When("User presses home") {
            device.pressHome()
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

}