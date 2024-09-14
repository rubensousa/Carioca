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

package com.rubensousa.carioca

import com.rubensousa.carioca.android.report.InstrumentedReportRule
import com.rubensousa.carioca.android.report.allure.AllureInstrumentedReporter
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineReportRule
import com.rubensousa.carioca.android.report.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.android.report.interceptor.LoggerInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions

class SampleInstrumentedReportRule : InstrumentedReportRule(
    reporter = AllureInstrumentedReporter(),
    recordingOptions = RecordingOptions(
        bitrate = 20_000_000,
        scale = 1.0f,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    screenshotOptions = ScreenshotOptions(
        scale = 0.5f,
        quality = 100,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    interceptors = listOf(
        LoggerInterceptor(),
        DumpViewHierarchyInterceptor(
            dumpOnEveryStage = false
        )
    )
)

// Rule for tests with coroutines
class SampleCoroutineInstrumentedReportRule : InstrumentedCoroutineReportRule(
    reporter = AllureInstrumentedReporter(),
    recordingOptions = RecordingOptions(
        bitrate = 20_000_000,
        scale = 1.0f,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    screenshotOptions = ScreenshotOptions(
        scale = 0.5f,
        quality = 100,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    interceptors = listOf(
        LoggerInterceptor(),
        DumpViewHierarchyInterceptor(
            dumpOnEveryStage = false
        )
    )
)