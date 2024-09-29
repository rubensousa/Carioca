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

package com.rubensousa.carioca.report.android.interceptor

import android.util.Log
import com.rubensousa.carioca.report.android.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport

/**
 * A [CariocaInstrumentedInterceptor] that logs the different stages
 */
class LoggerInterceptor : CariocaInstrumentedInterceptor {

    private val tag = "CariocaLogger"

    override fun onTestStarted(test: InstrumentedTestReport) {
        log("Test started: $test")
    }

    override fun onStageStarted(stage: InstrumentedStageReport) {
        log("Stage started: $stage")
    }

    override fun onStagePassed(stage: InstrumentedStageReport) {
        log("Stage passed: $stage")
    }

    override fun onStageFailed(stage: InstrumentedStageReport) {
        log("Stage failed: $stage")
    }

    override fun onTestFailed(test: InstrumentedTestReport) {
        log("Test failed: $test")
    }

    override fun onTestPassed(test: InstrumentedTestReport) {
        log("Test passed: $test")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

}
