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

import com.rubensousa.carioca.report.CariocaInstrumentedReportRule
import com.rubensousa.carioca.report.allure.CariocaAllureInstrumentedReporter
import com.rubensousa.carioca.report.interceptor.DumpHierarchyInterceptor
import com.rubensousa.carioca.report.interceptor.LoggerInterceptor
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

class SampleInstrumentedReportRule : CariocaInstrumentedReportRule(
    reporter = CariocaAllureInstrumentedReporter(),
    recordingOptions = RecordingOptions(
        bitrate = 20_000_000,
        resolutionScale = 1.0f,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    screenshotOptions = ScreenshotOptions(
        scale = 1f
    ),
    interceptors = listOf(LoggerInterceptor(), DumpHierarchyInterceptor())
)