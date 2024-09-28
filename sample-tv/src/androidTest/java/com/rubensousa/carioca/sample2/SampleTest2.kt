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

package com.rubensousa.carioca.sample2

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.android.InstrumentedReportRule
import com.rubensousa.carioca.report.runtime.TestReport
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SampleTest2 {

    @get:Rule
    val report = InstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun before() = report.before {
        step("Press home") {
            device.pressHome()
        }
        step("Set device orientation to natural") {
            device.setOrientationNatural()
        }
    }

    @After
    fun after() = report.after {
        step("Press home") {
            device.pressHome()
        }
        step("Unfreeze orientation") {
            device.unfreezeRotation()
        }
    }

    @TestReport(
        id = "PROJECT-122",
        description = "Opening notification and quick settings should be possible " +
                "in pretty much all devices. This is just an example description",
        links = ["https://developer.android.com/training/testing/other-components/ui-automator"]
    )
    @Test
    fun testSample2() = report {
        step("Open quick settings") {
            device.openQuickSettings()
            screenshot("Quick settings displayed")
        }

        step("Press home") {
            device.pressHome()
            screenshot("Launcher")
        }
    }

}
