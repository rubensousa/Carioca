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

import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest

/**
 * Lifecycle of a report:
 *
 * 1. [onTestStarted]
 * 2. [onStageStarted]
 * 3. [onStagePassed] or [onStageFailed]
 * 4. [onTestPassed] or [onTestFailed]
 *
 * Each stage can start a nested stage before it's over.
 * Example: scenario is a stage that starts nested step stages
 */
interface CariocaInstrumentedInterceptor {

    fun onTestStarted(test: InstrumentedTest) {}

    fun onStageStarted(stage: InstrumentedStageReport) {}

    fun onStagePassed(stage: InstrumentedStageReport) {}

    fun onStageFailed(stage: InstrumentedStageReport) {}

    fun onTestPassed(test: InstrumentedTest) {}

    fun onTestFailed(test: InstrumentedTest) {}

}

internal fun List<CariocaInstrumentedInterceptor>.intercept(action: CariocaInstrumentedInterceptor.() -> Unit) {
    forEach { interceptor ->
        action(interceptor)
    }
}
