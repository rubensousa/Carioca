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

package com.rubensousa.carioca.sample.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.rubensousa.carioca.android.sample.SampleActivity
import com.rubensousa.carioca.sample.SampleInstrumentedReportRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * Checks that SampleActivity renders the SampleScreen
 */
@HiltAndroidTest
class SampleActivityTest {

    @get:Rule
    val report = SampleInstrumentedReportRule()

    @get:Rule(order = 0)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule(SampleActivity::class.java)

    @Test
    fun testSampleScreenIsDisplayed() {
        val sampleScreen = SampleTestScreen(composeTestRule)

        sampleScreen.assertIsDisplayed()
    }

}
