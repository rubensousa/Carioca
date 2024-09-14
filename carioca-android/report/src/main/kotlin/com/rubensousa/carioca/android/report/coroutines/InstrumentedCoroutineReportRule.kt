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

package com.rubensousa.carioca.android.report.coroutines

import com.rubensousa.carioca.android.report.AbstractInstrumentedReportRule
import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.internal.InstrumentedTestBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class InstrumentedCoroutineReportRule(
    reporter: CariocaInstrumentedReporter,
    recordingOptions: RecordingOptions = RecordingOptions(),
    screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
    interceptors: List<CariocaInstrumentedInterceptor> = listOf(DumpViewHierarchyInterceptor()),
) : AbstractInstrumentedReportRule(
    reporter = reporter,
    recordingOptions = recordingOptions,
    screenshotOptions = screenshotOptions,
    interceptors = interceptors,
    testCreator = { description ->
        InstrumentedTestBuilder.build(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            interceptors = interceptors,
            reporter = reporter
        )
    }
) {

    /*
     * Runs the report inside a coroutine scope
     */
    operator fun invoke(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend InstrumentedCoroutineTestScope.() -> Unit,
    ) {
        kotlinx.coroutines.test.runTest(context) {
            block(getCurrentTest())
        }
    }

}
