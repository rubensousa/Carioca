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

package com.rubensousa.carioca.sample.example

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.android.recording.TestRecording
import com.rubensousa.carioca.report.android.stage.InstrumentedScenario
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope
import com.rubensousa.carioca.report.runtime.TestReport
import com.rubensousa.carioca.sample.SampleInstrumentedReportRule
import com.rubensousa.carioca.sample.SampleScreenScenario
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class SampleTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

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

    @TestRecording
    @TestReport(
        id = "PROJECT-122",
        description = "Opening notification and quick settings should be possible " +
                "in pretty much all devices. This is just an example description",
        links = ["https://developer.android.com/training/testing/other-components/ui-automator"]
    )
    @Test
    fun testThatPasses() = report {
        scenario(SampleScreenScenario())

        step("Open quick settings") {
            device.openQuickSettings()
            screenshot("Quick settings displayed")
        }

        step("Press home") {
            device.pressHome()
            screenshot("Launcher")
        }
    }

    @TestRecording
    @TestReport(
        id = "PROJECT-123",
        title = "Opening notification and quick settings",
        links = ["https://developer.android.com/training/testing/other-components/ui-automator"]
    )
    @Test
    @Ignore("Just for checking reports")
    fun testThatFails() = report {
        scenario(SampleScreenScenario())

        step("Open notification") {
            device.openNotification()
            screenshot("Notification bar visible")
        }
        step("Open quick settings") {
            device.openQuickSettings()
            Espresso.onView(withId(2)).perform(ViewActions.click())
        }
    }

    @TestRecording
    @Test
    fun testGivenWhenThen() = report {
        Given("User opens notifications") {
            device.openNotification()
            Thread.sleep(1000L)
            screenshot("Notifications opened")
        }

        When("User presses home") {
            device.pressHome()
            scenario(WaitDismissScenario())
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

    @Ignore("Just for checking reports")
    @Test
    fun testIsIgnored() {
        // Nothing
    }

    private class WaitDismissScenario : InstrumentedScenario("Wait for dismissal") {

        override fun run(scope: InstrumentedStageScope) {
            Thread.sleep(1000L)
        }
    }

}
