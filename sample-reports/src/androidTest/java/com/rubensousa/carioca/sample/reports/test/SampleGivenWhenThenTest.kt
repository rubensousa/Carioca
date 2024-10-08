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
import com.rubensousa.carioca.report.android.recording.TestRecording
import com.rubensousa.carioca.report.android.stage.InstrumentedScenario
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope
import com.rubensousa.carioca.sample.reports.OpenNotificationScenario
import com.rubensousa.carioca.sample.reports.SampleInstrumentedReportRule
import org.junit.Rule
import org.junit.Test

class SampleGivenWhenThenTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @TestRecording
    @Test
    fun testGivenWhenThen() = report {
        Given("User opens notifications") {
            // Do something
            device.openNotification()
        }

        When("User presses home") {
            device.pressHome()
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

    @TestRecording
    @Test
    fun testGivenWhenThenScenario() = report {
        Given(OpenNotificationScenario())

        When(PressHomeScenario())

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }


    class PressHomeScenario : InstrumentedScenario("Pressing home") {

        private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        override fun run(scope: InstrumentedStageScope) {
            device.pressHome()
            Thread.sleep(1000L)
        }
    }

}