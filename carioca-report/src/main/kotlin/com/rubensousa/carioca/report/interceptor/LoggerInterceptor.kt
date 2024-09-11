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

import android.util.Log
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import org.junit.runner.Description

class LoggerInterceptor : CariocaInterceptor {

    private val tag = "CariocaLogger"

    override fun onTestStarted(report: TestReport, description: Description) {
        log("Test started: $description")
    }

    override fun onTestFailed(report: TestReport, error: Throwable, description: Description) {
        log("Test failed: $description", error)
    }

    override fun onTestPassed(report: TestReport, description: Description) {
        log("Test passed: $description")
    }

    override fun onScenarioStarted(report: TestReport, scenario: ScenarioReport) {
        log("Scenario started: $scenario")
    }

    override fun onScenarioPassed(report: TestReport, scenario: ScenarioReport) {
        log("Scenario passed: $scenario")
    }

    override fun onScenarioFailed(report: TestReport, scenario: ScenarioReport) {
        log("Scenario failed: $scenario")
    }

    override fun onStepStarted(report: TestReport, step: StepReport) {
        log("Step started: $step")
    }

    override fun onStepPassed(report: TestReport, step: StepReport) {
        log("Step passed: $step")
    }

    override fun onStepFailed(report: TestReport, step: StepReport) {
        log("Step failed: $step")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable) {
        Log.e(tag, message, error)
    }

}
