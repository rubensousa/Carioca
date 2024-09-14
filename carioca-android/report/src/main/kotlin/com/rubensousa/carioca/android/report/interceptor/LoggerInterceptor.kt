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
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTest

class LoggerInterceptor : CariocaInstrumentedInterceptor {

    private val tag = "CariocaLogger"

    override fun onTestStarted(test: InstrumentedTest) {
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

    override fun onTestFailed(test: InstrumentedTest) {
        log("Test failed: $test", test.getExecutionMetadata().failureCause)
    }

    override fun onTestPassed(test: InstrumentedTest) {
        log("Test passed: $test")
    }

    private fun log(message: String) {
        Log.i(tag, message)
    }

    private fun log(message: String, error: Throwable?) {
        Log.e(tag, message, error)
    }

}
