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

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.android.report.sample.OpenNotificationScenario
import com.rubensousa.carioca.android.report.sample.SampleInstrumentedReportRule
import com.rubensousa.carioca.junit4.rules.RepeatTest
import com.rubensousa.carioca.junit4.rules.RepeatTestRule
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore
@RepeatTest(times = 3)
class SampleRepeatTest {

    @get:Rule(order = 0)
    val repeatRule = RepeatTestRule()

    @get:Rule(order = 1)
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setup() {
        device.pressHome()
    }

    @Test
    fun testThatWillBeExecutedMultipleTimes() = report {
        Given(OpenNotificationScenario())

        When("User presses home") {
            device.pressHome()
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }
    }

}
