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

package com.rubensousa.carioca.report.android.stage

/**
 * The public API for each report. This is the main entry for each test report.
 */
interface InstrumentedTestScope : InstrumentedStageScope {

    @Suppress("FunctionName")
    fun Given(
        scenario: InstrumentedScenario,
        action: InstrumentedStageScope.() -> Unit = {},
    ) {
        scenario(
            object : InstrumentedScenario(
                title = "Given: ${scenario.title}",
                id = scenario.id
            ) {
                override fun run(scope: InstrumentedStageScope) {
                    scenario.run(scope)
                    action(scope)
                }
            }
        )
    }

    @Suppress("FunctionName")
    fun Given(
        title: String,
        action: InstrumentedStageScope.() -> Unit,
    ) {
        step("Given: $title", null, action)
    }

    @Suppress("FunctionName")
    fun When(
        title: String,
        action: InstrumentedStageScope.() -> Unit,
    ) {
        step("When: $title", null, action)
    }

    @Suppress("FunctionName")
    fun When(
        scenario: InstrumentedScenario,
        action: InstrumentedStageScope.() -> Unit = {},
    ) {
        scenario(
            object : InstrumentedScenario(
                title = "Scenario: ${scenario.title}",
                id = scenario.id
            ) {
                override fun run(scope: InstrumentedStageScope) {
                    scenario.run(scope)
                    action(scope)
                }
            }
        )
    }

    @Suppress("FunctionName")
    fun Then(
        title: String,
        action: InstrumentedStageScope.() -> Unit,
    ) {
        step("Then: $title", null, action)
    }

}
