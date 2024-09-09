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

package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import org.junit.runner.Description

/**
 * Lifecycle of a report:
 *
 * 1. [onTestStarted]
 * 2. [onScenarioStarted] if there is a scenario
 * 3. [onStepStarted]
 * 4. [onStepPassed] or [onStepFailed]
 * 5. [onScenarioPassed] or [onScenarioFailed] if a scenario was started
 * 6. [onTestPassed] or [onTestFailed]
 */
interface CariocaInterceptor {

    fun onTestStarted(description: Description) {}

    fun onScenarioStarted(scenario: ScenarioReport) {}

    fun onStepStarted(step: StepReport) {}

    fun onStepPassed(step: StepReport) {}

    fun onScenarioPassed(scenario: ScenarioReport) {}

    fun onTestPassed(description: Description) {}

    fun onStepFailed(step: StepReport) {}

    fun onScenarioFailed(scenario: ScenarioReport) {}

    fun onTestFailed(error: Throwable, description: Description) {}

}
