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

package com.rubensousa.carioca.sample.test

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.android.sample.SampleActivity
import com.rubensousa.carioca.report.android.InstrumentedReportRule
import com.rubensousa.carioca.report.android.compose.DumpComposeHierarchyInterceptor
import com.rubensousa.carioca.report.android.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.report.android.interceptor.TakeScreenshotOnFailureInterceptor
import com.rubensousa.carioca.report.android.recording.TestRecording
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SampleIntegrationTest {

    @get:Rule
    val report = InstrumentedReportRule(
        interceptors = listOf(
            TakeScreenshotOnFailureInterceptor(),
            DumpComposeHierarchyInterceptor(),
            DumpViewHierarchyInterceptor(),
        )
    )

    @get:Rule(order = 0)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    @get:Rule(order = 2)
    val activityScenarioRule = ActivityScenarioRule(SampleActivity::class.java)

    @Inject
    lateinit var logger: TestLogger

    private val sampleScreen = SampleTestScreen(composeTestRule)

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    @TestRecording(keepOnSuccess = true)
    @Test
    fun testButtonClickTriggersLogMessage() = report {
        Given("Wait until screen is displayed") {
            composeTestRule.waitUntil {
                sampleScreen.isDisplayed()
            }
        }

        When("Click main button") {
            sampleScreen.clickMainButton()
        }

        Then("Logger contains message") {
            assertThat(logger.getMessages()).isEqualTo(listOf("Button clicked"))
        }

    }

}
