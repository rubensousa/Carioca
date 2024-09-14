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
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStageScope

import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test

class CoroutineSampleTest {

    @get:Rule
    val report = SampleCoroutineInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun testGivenWhenThenCoroutines() = report {
        Given(OpenNotificationScenario())

        When("User presses home") {
            device.pressHome()
            delay(1000L)
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

    @Test
    fun testCoroutine() = report {
        delay(1000L)

        step("Some step") {
            delay(2000L)
        }
    }

    private class OpenNotificationScenario : InstrumentedCoroutineScenario("Open Notification") {

        private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        override suspend fun run(scope: InstrumentedCoroutineStageScope) = with(scope) {
            screenshot("Before opening notifications")

            step("Request notification open") {
                device.openNotification()
            }

            step("Wait for animation") {
                delay(1000L)
                screenshot("Notification")
            }

            screenshot("After opening notifications")
        }
    }

}
