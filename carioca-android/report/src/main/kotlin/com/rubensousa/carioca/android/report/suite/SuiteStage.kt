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

import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.junit.report.ExecutionIdGenerator
import com.rubensousa.carioca.junit.report.ExecutionMetadata
import com.rubensousa.carioca.junit.report.ReportStatus
import org.junit.runner.Result

internal interface SuiteStage {

    fun addTest(
        reporter: CariocaInstrumentedReporter,
        test: InstrumentedTest,
    )

    fun clear()

    fun writeReport(result: Result)

}

internal class InstrumentedSuiteStage : SuiteStage {

    private val tests = mutableListOf<InstrumentedTest>()
    private val reporters = mutableMapOf<Class<*>, CariocaInstrumentedReporter>()
    private var startTime = 0L

    override fun addTest(
        reporter: CariocaInstrumentedReporter,
        test: InstrumentedTest,
    ) {
        reporters[reporter::class.java] = reporter
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
        tests.add(test)
    }

    override fun writeReport(result: Result) {
        /**
         * Nothing to be done if there is only one test.
         * This will happen if test orchestrator is used,
         * since every instrumentation exists in its own process
         */
        if (tests.size <= 1) {
            return
        }
        val statusCount = mutableMapOf<ReportStatus, Int>()
        statusCount[ReportStatus.PASSED] = result.runCount
        statusCount[ReportStatus.FAILED] = result.failureCount
        statusCount[ReportStatus.SKIPPED] = result.ignoreCount
        val report = TestSuiteReport(
            packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName,
            executionMetadata = ExecutionMetadata(
                uniqueId = ExecutionIdGenerator.get(),
                failureCause = null,
                status = if (result.wasSuccessful()) {
                    ReportStatus.PASSED
                } else {
                    ReportStatus.FAILED
                },
                startTime = startTime,
                endTime = System.currentTimeMillis()
            ),
            testStatus = statusCount
        )
        writeReport(report)
    }

    override fun clear() {
        tests.clear()
        startTime = 0L
    }

    private fun writeReport(report: TestSuiteReport) {
        reporters.values.forEach { reporter ->
            val outputStream = TestStorageProvider.getOutputStream(reporter.getSuiteReportFilePath(report))
            reporter.writeSuiteReport(report, outputStream)
        }
    }

}
