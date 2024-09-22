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

package com.rubensousa.carioca.android.report.coroutines.internal

import com.rubensousa.carioca.android.report.InstrumentedReporter
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStageScope
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineTestScope
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.TestMetadata

internal class InstrumentedCoroutineTest(
    outputPath: String,
    metadata: TestMetadata,
    recordingOptions: RecordingOptions,
    reporter: InstrumentedReporter,
    screenshotDelegate: ScreenshotDelegate,
    interceptors: List<CariocaInstrumentedInterceptor>,
    storageProvider: ReportStorageProvider,
) : InstrumentedTestReport(
    outputPath = outputPath,
    metadata = metadata,
    recordingOptions = recordingOptions,
    screenshotDelegate = screenshotDelegate,
    reporter = reporter,
    interceptors = interceptors,
    storageProvider = storageProvider
), InstrumentedCoroutineTestScope {

    private val coroutineDelegateFactory =
        object : InstrumentedReportDelegateFactory<InstrumentedCoroutineStageScope> {
            override fun create(report: InstrumentedStageReport): InstrumentedCoroutineDelegate {
                return InstrumentedCoroutineDelegate(
                    delegateFactory = this,
                    host = report,
                    screenshotDelegate = screenshotDelegate,
                    stack = stageStack,
                    interceptors = interceptors,
                    outputPath = outputPath,
                    storageProvider = storageProvider
                )
            }
        }
    private val delegate = coroutineDelegateFactory.create(this)

    override fun screenshot(description: String, options: ScreenshotOptions?) {
        delegate.screenshot(description, options)
    }

    override suspend fun step(
        title: String,
        id: String?,
        action: suspend InstrumentedCoroutineStageScope.() -> Unit,
    ) {
        delegate.step(title, id, action)
    }

    override suspend fun scenario(scenario: InstrumentedCoroutineScenario) {
        delegate.scenario(scenario)
    }

    override fun param(key: String, value: String) {
        delegate.param(key, value)
    }

}