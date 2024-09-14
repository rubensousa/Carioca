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
import com.rubensousa.carioca.android.report.stage.test.Given
import com.rubensousa.carioca.android.report.stage.test.Then
import com.rubensousa.carioca.android.report.stage.test.When
import com.rubensousa.carioca.android.rules.RetryTest
import com.rubensousa.carioca.android.rules.RetryTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SampleRetryTest {

    @get:Rule(order = 0)
    val retryRule = RetryTestRule()

    @get:Rule(order = 1)
    val report = SampleInstrumentedReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setup() {
        device.pressHome()
    }

    @RetryTest(times = 2)
    @Test
    fun testGivenWhenThenScenario() = report {
        Given(OpenNotificationScenario())

        When("User presses home") {
            device.pressHome()
        }

        Then("Launcher is displayed") {
            screenshot("Launcher")
        }

        if (retryRule.currentExecution != 6) {
            throw IllegalStateException("Fail test on purpose")
        }
    }

}
