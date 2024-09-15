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

import com.rubensousa.carioca.android.report.stage.InstrumentedScenario
import com.rubensousa.carioca.android.report.stage.InstrumentedStageScope

fun sampleScreen(
    reportScope: InstrumentedStageScope,
    action: SampleScreen.() -> Unit,
) {
    action(SampleScreen(reportScope))
}

class SampleScreenScenario : InstrumentedScenario(
    title = "Sample Scenario",
    id = "Persistent scenario id"
) {

    override fun run(scope: InstrumentedStageScope) = with(scope) {
        step("Step 1 of Scenario") {

        }

        step("Step 2 of Scenario") {

        }
    }

}

class SampleScreen(
    private val reportScope: InstrumentedStageScope,
) {

    fun assertIsDisplayed() {
        reportScope.step("Checking screen is displayed")
    }

    fun assertIsNotDisplayed() {
        reportScope.step("Checking screen is not displayed")
    }

}
