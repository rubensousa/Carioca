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

package com.rubensousa.carioca.report.android

import android.annotation.SuppressLint
import androidx.test.internal.runner.listener.InstrumentationRunListener
import com.rubensousa.carioca.report.android.suite.SuiteReportRegistry
import org.junit.runner.Description

/**
 * Use this if you're interested in recording ignored tests,
 * as there is no other way to do it at the moment
 */
@Suppress("unused")
@SuppressLint("RestrictedApi")
class CariocaInstrumentedListener : InstrumentationRunListener() {

    private val stage = SuiteReportRegistry.getSuiteStage()

    override fun testIgnored(description: Description) {
        super.testIgnored(description)
        stage.testIgnored(description)
    }

}
