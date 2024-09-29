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

package com.rubensousa.carioca.report.android.interceptor

import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.recording.ScreenRecorder
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.android.storage.FileIdGenerator
import com.rubensousa.carioca.report.runtime.StageAttachment

/**
 * A [CariocaInstrumentedInterceptor] that starts recording the test in [onTestStarted]
 * and then stops the recording when the test finishes.
 *
 * If [onTestPassed] is called,
 * we delete the recording unless [RecordingOptions.keepOnSuccess] is set to true.
 *
 * If [onTestFailed], the recording is always saved
 */
internal class RecordingInterceptor(
    private val recordingOptions: RecordingOptions,
    private val screenRecorder: ScreenRecorder,
    private val idGenerator: FileIdGenerator,
) : CariocaInstrumentedInterceptor {

    override fun onTestStarted(test: InstrumentedTestReport) {
        val recording = screenRecorder.start(
            filename = idGenerator.get(),
            options = recordingOptions,
            outputPath = test.outputPath
        )
        test.attach(
            StageAttachment(
                description = "Screen recording",
                path = recording.relativeFilePath,
                mimeType = "video/mp4",
                keepOnSuccess = recordingOptions.keepOnSuccess
            )
        )
    }

    override fun onTestPassed(test: InstrumentedTestReport) {
        screenRecorder.stop(delete = !recordingOptions.keepOnSuccess)
    }

    override fun onTestFailed(test: InstrumentedTestReport) {
        screenRecorder.stop(delete = false)
    }

}
