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

package com.rubensousa.carioca.sample.reports.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.android.recording.RecordingOrientation
import com.rubensousa.carioca.report.android.recording.TestRecording
import com.rubensousa.carioca.sample.reports.SampleInstrumentedReportRule
import org.junit.After
import org.junit.Rule
import org.junit.Test

class SampleRecordingTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @After
    fun after() {
        device.setOrientationNatural()
    }

    // Overrides the screen recording options
    @TestRecording(
        scale = 1.0f,
        keepOnSuccess = true,
        orientation = RecordingOrientation.LANDSCAPE
    )
    @Test
    fun testRecordingOverride() = report {
        step("Open settings") {
            device.executeShellCommand("am start -a android.settings.SETTINGS")
            Thread.sleep(2000L)
        }

        step("Rotate device to landscape") {
            device.setOrientationLandscape()
            Thread.sleep(2000L)
        }

        step("Open quick settings") {
            device.openQuickSettings()
            Thread.sleep(2000L)
        }

        step("Press home") {
            device.pressHome()
        }
    }

}