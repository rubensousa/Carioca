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

package com.rubensousa.carioca.android.report.interceptor

import android.util.Log
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestStage
import com.rubensousa.carioca.stage.CariocaStage
import org.junit.runner.Description

class LoggerInterceptor : CariocaInstrumentedInterceptor {

    private val tag = "CariocaLogger"

    override fun onTestStarted(report: InstrumentedTestStage, description: Description) {
        log("Test started: $description")
    }

    override fun onStageStarted(report: CariocaStage) {
        log("Stage started: $report")
    }

    override fun onStagePassed(report: CariocaStage) {
        log("Stage passed: $report")
    }

    override fun onStageFailed(report: CariocaStage) {
        log("Stage failed: $report")
    }

    override fun onTestFailed(report: InstrumentedTestStage) {
        log("Test failed: $report", report.getExecutionMetadata().failureCause)
    }

    override fun onTestPassed(report: InstrumentedTestStage) {
        log("Test passed: $report")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable?) {
        Log.e(tag, message, error)
    }

}
