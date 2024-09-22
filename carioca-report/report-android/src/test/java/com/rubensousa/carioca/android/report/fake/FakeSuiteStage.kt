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

package com.rubensousa.carioca.android.report.fake

import com.rubensousa.carioca.report.android.InstrumentedReporter
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.android.suite.SuiteStage
import org.junit.runner.Description

class FakeSuiteStage : SuiteStage {

    val ignoredDescriptions = mutableListOf<Description>()

    private val reporters = mutableListOf<InstrumentedReporter>()
    private val tests = mutableListOf<InstrumentedTestReport>()

    override fun registerReporter(reporter: InstrumentedReporter) {
        reporters.add(reporter)
    }

    override fun testStarted(test: InstrumentedTestReport) {
        tests.add(test)
    }

    override fun testIgnored(description: Description) {
        ignoredDescriptions.add(description)
    }

    override fun getTests(): List<InstrumentedTestReport> {
        return tests.toList()
    }

}
