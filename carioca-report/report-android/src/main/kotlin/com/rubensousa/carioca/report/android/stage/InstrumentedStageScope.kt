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

import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions

/**
 * The main entry point for stage reports
 */
interface InstrumentedStageScope : InstrumentedReportScope {

    /**
     * Takes a screenshot with the configuration set through the report rule.
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     * @param options the optional options that will override the setup from the test.
     * If null, the default options set in the rule will apply
     */
    fun screenshot(description: String, options: ScreenshotOptions? = null)

    /**
     * Creates an individual section of a test
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(
        title: String,
        id: String? = null,
        action: InstrumentedStageScope.() -> Unit = {},
    )

    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     *
     * @param scenario the re-usable set of stages
     */
    fun scenario(scenario: InstrumentedScenario)

    /**
     * Sets a parameter for the current stage
     *
     * @param key the unique identifier of the parameter
     * @param value the value of the parameter
     */
    fun param(key: String, value: String)

}
