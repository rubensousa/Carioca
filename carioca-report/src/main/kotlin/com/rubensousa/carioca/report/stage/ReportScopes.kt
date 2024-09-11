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

package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

/**
 * The public API for each report. This is the main entry for each test report.
 */
interface TestReportScope {

    /**
     * Creates an individual section of a test
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: StepReportScope.() -> Unit)


    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     */
    fun scenario(scenario: TestScenario)

}

fun TestReportScope.given(scenario: TestScenario) {
    scenario(object : TestScenario {
        override val name: String = "Given: ${scenario.name}"
        override fun getId(): String? = scenario.getId()
        override fun report(scope: ScenarioReportScope) {
            scenario.report(scope)
        }
    })
}

fun TestReportScope.given(
    title: String,
    action: StepReportScope.() -> Unit,
) {
    step("Given: $title", null, action)
}

fun TestReportScope.`when`(
    title: String,
    action: StepReportScope.() -> Unit,
) {
    step("When: $title", null, action)
}

fun TestReportScope.then(
    title: String,
    action: StepReportScope.() -> Unit,
) {
    step("Then: $title", null, action)
}

/**
 * Public API for a scenario block
 */
interface ScenarioReportScope {
    /**
     * Creates an individual section of a scenario
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: StepReportScope.() -> Unit)
}

/**
 * Public API for a step block
 */
interface StepReportScope {

    /**
     * Takes a screenshot with the configuration set through [ScreenshotOptions].
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     */
    fun screenshot(description: String)

    /**
     * Creates a nested step inside the current step
     *
     * @param title the name of the step
     * @param action the step block that will be executed
     */
    fun step(title: String, action: StepReportScope.() -> Unit)

}
