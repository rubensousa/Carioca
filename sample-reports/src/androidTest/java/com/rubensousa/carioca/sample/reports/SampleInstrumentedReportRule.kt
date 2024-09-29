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

package com.rubensousa.carioca.sample.reports

import com.rubensousa.carioca.report.android.InstrumentedReportRule
import com.rubensousa.carioca.report.android.coroutines.InstrumentedCoroutineReportRule
import com.rubensousa.carioca.report.android.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.report.android.interceptor.LoggerInterceptor
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions

class SampleInstrumentedReportRule : InstrumentedReportRule(
    recordingOptions = RecordingOptions(
        enabled = false,
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
)

// Rule for tests with coroutines
class SampleCoroutineInstrumentedReportRule : InstrumentedCoroutineReportRule(
    recordingOptions = RecordingOptions(enabled = false),
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
            dumpOnSuccess = true  // Optional. Default is false
        )
    )
)