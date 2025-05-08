package com.rubensousa.carioca.sample.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

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

    fun isDisplayed(): Boolean {
        return mainButton.isDisplayed()
    }

    fun clickMainButton() {
        mainButton.performClick()
    }

}
