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

import android.util.Log
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import org.junit.runner.Description

class CariocaLogcatInterceptor : CariocaInterceptor {

    private val tag = "CariocaLogger"

    override fun onTestStarted(description: Description) {
        log("Test started: $description")
    }

    override fun onTestFailed(error: Throwable, description: Description) {
        log("Test failed: $description", error)
    }

    override fun onTestPassed(description: Description) {
        log("Test passed: $description")
    }

    override fun onScenarioStarted(scenario: ScenarioReport) {
        log("Scenario started: ${scenario.id}")
    }

    override fun onScenarioPassed(scenario: ScenarioReport) {
        log("Step passed: ${scenario.id}")
    }

    override fun onScenarioFailed(scenario: ScenarioReport) {
        log("Step passed: ${scenario.id}")
    }

    override fun onStepStarted(step: StepReport) {
        log("Step started: ${step.title}")
    }

    override fun onStepPassed(step: StepReport) {
        log("Step passed: ${step.title}")
    }

    override fun onStepFailed(step: StepReport) {
        log("Step failed: ${step.title}")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable) {
        Log.e(tag, message, error)
    }

}
