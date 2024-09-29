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

package com.rubensousa.carioca.hilt.compose

import android.content.ComponentName
import android.content.Intent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.rubensousa.carioca.hilt.manifest.EmptyHiltActivity

/**
 * Creates an [AndroidComposeTestRule] for [EmptyHiltActivity],
 * an empty activity that doesn't set any content.
 *
 * This allows testing Composables that require hilt injections.
 *
 * [AndroidComposeTestRule.setContent] must be called inside the test body
 */
fun createHiltComposeRule(): AndroidComposeTestRule<
        ActivityScenarioRule<EmptyHiltActivity>, EmptyHiltActivity> {
    val componentName = ComponentName(
        ApplicationProvider.getApplicationContext(),
        EmptyHiltActivity::class.java
    )
    val startActivityIntent = Intent.makeMainActivity(componentName)
    val rule = ActivityScenarioRule<EmptyHiltActivity>(startActivityIntent)
    return AndroidComposeTestRule(
        activityRule = rule,
        activityProvider = {
            var activity: EmptyHiltActivity? = null
            rule.scenario.onActivity { activity = it }
            if (activity == null) {
                throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
            }
            activity!!
        }
    )
}
