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

package com.rubensousa.carioca.report.stage.test

import com.rubensousa.carioca.report.stage.scenario.InstrumentedScenarioScope
import com.rubensousa.carioca.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.report.stage.step.InstrumentedStepScope

fun InstrumentedTestScope.Given(scenario: InstrumentedTestScenario) {
    scenario(
        object : InstrumentedTestScenario(
            name = "Given: ${scenario.name}",
            id = scenario.id
        ) {
            override fun run(scope: InstrumentedScenarioScope) {
                scenario.run(scope)
            }
        }
    )
}

@Suppress("FunctionName")
fun InstrumentedTestScope.Given(
    title: String,
    action: InstrumentedStepScope.() -> Unit,
) {
    step("Given: $title", null, action)
}

@Suppress("FunctionName")
fun InstrumentedTestScope.When(
    title: String,
    action: InstrumentedStepScope.() -> Unit,
) {
    step("When: $title", null, action)
}

@Suppress("FunctionName")
fun InstrumentedTestScope.Then(
    title: String,
    action: InstrumentedStepScope.() -> Unit,
) {
    step("Then: $title", null, action)
}
