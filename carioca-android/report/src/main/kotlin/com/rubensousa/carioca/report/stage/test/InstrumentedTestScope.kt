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

import com.rubensousa.carioca.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.report.stage.step.InstrumentedStepScope

/**
 * The public API for each report. This is the main entry for each test report.
 */
interface InstrumentedTestScope {

    /**
     * Creates an individual section of a test
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: InstrumentedStepScope.() -> Unit)


    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     */
    fun scenario(scenario: InstrumentedTestScenario)

}
