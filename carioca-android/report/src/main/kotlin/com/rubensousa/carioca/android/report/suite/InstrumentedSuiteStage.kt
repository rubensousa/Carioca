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
package com.rubensousa.carioca.android.report.suite

import com.rubensousa.carioca.android.report.InstrumentedReporter
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.internal.InstrumentedBlockingTestBuilder
import com.rubensousa.carioca.junit4.report.getTestMetadata
import com.rubensousa.carioca.junit4.report.getTestReportConfig
import org.junit.runner.Description

internal class InstrumentedSuiteStage(
    private val testBuilder: InstrumentedBlockingTestBuilder,
) : SuiteStage {

    private val reporters = mutableMapOf<Class<*>, InstrumentedReporter>()
    private val tests = mutableListOf<InstrumentedTestReport>()

    override fun registerReporter(reporter: InstrumentedReporter) {
        reporters[reporter::class.java] = reporter
    }

    override fun testStarted(test: InstrumentedTestReport) {
        tests.add(test)
    }

    override fun testIgnored(description: Description) {
        val allReporters = reporters.values.toList()
        allReporters.forEach { reporter ->
            val test = testBuilder.build(
                reportConfig = description.getTestReportConfig(),
                testMetadata = description.getTestMetadata(),
                recordingOptions = RecordingOptions(enabled = false),
                screenshotOptions = ScreenshotOptions(),
                reporter = reporter,
                interceptors = emptyList()
            )
            tests.add(test)
            test.onIgnored()
        }
    }

    override fun getTests() = tests.toList()

}
