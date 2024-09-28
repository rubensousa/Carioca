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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope

class SampleTestScreen(
    private val composeTestRule: ComposeTestRule,
) {

    private val mainButton by lazy {
        composeTestRule.onNodeWithText("Main button")
    }

    private val fabButton by lazy {
        composeTestRule.onNodeWithText("FAB")
    }

    fun assertIsDisplayed() {
        mainButton.assertIsDisplayed()
        fabButton.assertIsDisplayed()
    }

    fun clickMainButton() {
        mainButton.performClick()
    }

    fun clickFab(scope: InstrumentedStageScope) {
        scope.step("Click fab button") {
            fabButton.performClick()
        }
    }

}
