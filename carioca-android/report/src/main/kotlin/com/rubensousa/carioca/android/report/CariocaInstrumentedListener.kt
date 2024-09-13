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

package com.rubensousa.carioca.android.report

import android.annotation.SuppressLint
import androidx.test.internal.runner.listener.InstrumentationRunListener
import com.rubensousa.carioca.android.report.suite.SuiteReportRegistry
import org.junit.runner.Description
import org.junit.runner.Result

/**
 * Use this if you're interested in test suite reports.
 *
 * Please note that this does not work if you run tests using test orchestrator.
 */
@Suppress("unused")
@SuppressLint("RestrictedApi")
class CariocaInstrumentedListener : InstrumentationRunListener() {

    private val stage = SuiteReportRegistry.getSuiteStage()

    override fun testRunStarted(description: Description) {
        super.testRunStarted(description)
        stage.clear()
    }

    override fun testRunFinished(result: Result) {
        super.testRunFinished(result)
        stage.writeReport(result)
    }

}