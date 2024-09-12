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

package com.rubensousa.carioca

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.android.report.TestId
import com.rubensousa.carioca.android.report.TestTitle
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenarioScope
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.android.report.stage.test.Given
import com.rubensousa.carioca.android.report.stage.test.Then
import com.rubensousa.carioca.android.report.stage.test.When
import org.junit.Rule
import org.junit.Test

class SampleTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @TestId("This is a persistent test id")
    @TestTitle("Opening notification and quick settings works")
    @Test
    fun testSuccessfulTest() = report {
        scenario(SampleScreenScenario())

        step("Open notification and quick settings") {

            scenario(OpenNotificationScenario())

            step("Open quick settings") {
                device.openQuickSettings()
                screenshot("Quick settings displayed")
            }
        }

        step("Press home") {
            device.pressHome()
            sampleScreen {
                assertIsNotDisplayed()
                screenshot("Launcher")
            }
        }
    }

    @TestId(id = "This is a persistent test id 2")
    @TestTitle("Opening notification and quick settings")
    @Test
    fun testFailedTest() = report {
        scenario(SampleScreenScenario())

        step("Open notification") {
            device.openNotification()
            screenshot("Notification bar visible")
        }
        step("Open quick settings") {
            device.openQuickSettings()
            throw IllegalStateException("Failed")
        }
    }

    @Test
    fun testGivenWhenThen() = report {
        Given("User opens notifications") {
            device.openNotification()
            screenshot("Notification")
        }

        When("User presses home") {
            device.pressHome()
            scenario(WaitDismissScenario())
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

    @Test
    fun testGivenWhenThenScenario() = report {
        Given(OpenNotificationScenario())

        When("User presses home") {
            device.pressHome()
            scenario(WaitDismissScenario())
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }

    }

    private class OpenNotificationScenario : InstrumentedTestScenario("Open Notification") {

        private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        override fun run(scope: InstrumentedScenarioScope) = with(scope) {
            screenshot("Before opening notifications")

            step("Request notification open") {
                device.openNotification()
            }

            step("Wait for animation") {
                Thread.sleep(1000L)
                screenshot("Notification")
            }

            screenshot("After opening notifications")
        }
    }

    private class WaitDismissScenario : InstrumentedTestScenario("Wait for dismissal") {

        override fun run(scope: InstrumentedScenarioScope) = with(scope) {
            step("Wait for dismissal") {
                Thread.sleep(1000L)
            }
        }
    }

}
