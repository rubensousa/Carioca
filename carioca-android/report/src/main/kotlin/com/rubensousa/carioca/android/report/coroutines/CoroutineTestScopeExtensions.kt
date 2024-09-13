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

package com.rubensousa.carioca.android.report.coroutines

suspend fun InstrumentedCoroutineTestScope.Given(
    scenario: InstrumentedCoroutineScenario,
    action: suspend InstrumentedCoroutineScenarioScope.() -> Unit = {},
) {
    scenario(
        object : InstrumentedCoroutineScenario(
            title = "Given: ${scenario.title}",
            id = scenario.id
        ) {
            override suspend fun run(scope: InstrumentedCoroutineScenarioScope) {
                scenario.run(scope)
                action(scope)
            }
        }
    )
}

@Suppress("FunctionName")
suspend fun InstrumentedCoroutineTestScope.Given(
    title: String,
    action: suspend InstrumentedCoroutineStepScope.() -> Unit,
) {
    step("Given: $title", null, action)
}

@Suppress("FunctionName")
suspend fun InstrumentedCoroutineTestScope.When(
    title: String,
    action: suspend InstrumentedCoroutineStepScope.() -> Unit,
) {
    step("When: $title", null, action)
}

@Suppress("FunctionName")
suspend fun InstrumentedCoroutineTestScope.Then(
    title: String,
    action: suspend InstrumentedCoroutineStepScope.() -> Unit,
) {
    step("Then: $title", null, action)
}
