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
import com.rubensousa.carioca.report.TestId
import com.rubensousa.carioca.report.TestTitle
import com.rubensousa.carioca.report.stage.given
import com.rubensousa.carioca.report.stage.then
import com.rubensousa.carioca.report.stage.`when`
import org.junit.Rule
import org.junit.Test

class SampleTest {

    @get:Rule
    val reportRule = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @TestId("This is a persistent test id")
    @TestTitle("Opening notification and quick settings works")
    @Test
    fun testSuccessfulTest() = reportRule.report {
        scenario(SampleScreenScenario())

        step("Open notification and quick settings") {
            step("Open notification") {
                device.openNotification()
                screenshot("Notification bar visible")
            }
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
    fun testFailedTest() = reportRule.report {
        scenario(SampleScreenScenario())

        step("Open notification") {
            device.openNotification()
            screenshot("Notification bar visible")
        }
        step("Open quick settings") {
            throw IllegalStateException("Failed")
        }
    }

    @Test
    fun testGivenWhenThen() = reportRule.report {

        given("User opens notifications") {
            device.openNotification()
            screenshot("Notification")
        }

        `when`("User presses home") {
            device.pressHome()
            step("Wait for dismissal") {
                Thread.sleep(1000L)
            }
        }

        then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

}
