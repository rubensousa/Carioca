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

package com.rubensousa.carioca.report.suite

import androidx.test.platform.app.InstrumentationRegistry
import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.stage.ExecutionMetadata
import com.rubensousa.carioca.report.stage.ExecutionStatus
import com.rubensousa.carioca.report.stage.test.InstrumentedTestStage
import com.rubensousa.carioca.report.storage.IdGenerator
import com.rubensousa.carioca.report.storage.TestStorageProvider
import org.junit.runner.Result

internal interface SuiteStage {

    fun addTest(reporter: CariocaInstrumentedReporter, stage: InstrumentedTestStage)

    fun clear()

    fun writeReport(result: Result)

}

internal class SuiteStageImpl : SuiteStage {

    private val tests = mutableListOf<InstrumentedTestStage>()
    private val reporters = mutableMapOf<Class<*>, CariocaInstrumentedReporter>()
    private var startTime = 0L

    override fun addTest(reporter: CariocaInstrumentedReporter, stage: InstrumentedTestStage) {
        reporters[reporter::class.java] = reporter
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
        tests.add(stage)
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
        val statusCount = mutableMapOf<ExecutionStatus, Int>()
        statusCount[ExecutionStatus.PASSED] = result.runCount
        statusCount[ExecutionStatus.FAILED] = result.failureCount
        statusCount[ExecutionStatus.SKIPPED] = result.ignoreCount
        val report = TestSuiteReport(
            packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName,
            executionMetadata = ExecutionMetadata(
                uniqueId = IdGenerator.get(),
                failureCause = null,
                status = if (result.wasSuccessful()) {
                    ExecutionStatus.PASSED
                } else {
                    ExecutionStatus.FAILED
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
