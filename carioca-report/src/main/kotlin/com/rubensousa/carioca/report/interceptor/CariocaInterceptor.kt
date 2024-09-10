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

package com.rubensousa.carioca.report.interceptor

import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
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

    fun onTestStarted(report: TestReport, description: Description) {}

    fun onScenarioStarted(report: TestReport, scenario: ScenarioReport) {}

    fun onStepStarted(report: TestReport, step: StepReport) {}

    fun onStepPassed(report: TestReport, step: StepReport) {}

    fun onScenarioPassed(report: TestReport, scenario: ScenarioReport) {}

    fun onTestPassed(report: TestReport, description: Description) {}

    fun onStepFailed(report: TestReport, step: StepReport) {}

    fun onScenarioFailed(report: TestReport, scenario: ScenarioReport) {}

    fun onTestFailed(report: TestReport, error: Throwable, description: Description) {}

}

internal fun List<CariocaInterceptor>.intercept(action: CariocaInterceptor.() -> Unit) {
    forEach { interceptor ->
        action(interceptor)
    }
}
