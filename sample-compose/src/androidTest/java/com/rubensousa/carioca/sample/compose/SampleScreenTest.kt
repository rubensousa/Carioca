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

package com.rubensousa.carioca.sample.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.android.sample.SampleScreen
import com.rubensousa.carioca.hilt.compose.createHiltComposeRule
import com.rubensousa.carioca.report.android.recording.TestRecording
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Checks that SampleScreen works without depending on SampleActivity
 */
@HiltAndroidTest
class SampleScreenTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    @get:Rule(order = 0)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createHiltComposeRule()

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
        Given("Setup compose content") {
            composeTestRule.setContent {
                SampleScreen(
                    modifier = Modifier.fillMaxSize()
                )
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
