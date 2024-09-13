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

interface InstrumentedCoroutineStepScope {

    /**
     * Creates a nested step inside the current step
     *
     * @param title the name of the step
     * @param action the step block that will be executed
     */
    suspend fun step(title: String, id: String? = null, action: suspend InstrumentedCoroutineStepScope.() -> Unit)

    /**
     * Takes a screenshot with the configuration set through [ScreenshotOptions].
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     */
    fun screenshot(description: String)


    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     */
    suspend fun scenario(scenario: InstrumentedCoroutineScenario)

}
