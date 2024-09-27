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

package com.rubensousa.carioca.report.android.stage

import android.util.Log
import com.rubensousa.carioca.report.android.InstrumentedReporter
import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.interceptor.intercept
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.recording.RecordingTaskFactoryImpl
import com.rubensousa.carioca.report.android.recording.ReportRecording
import com.rubensousa.carioca.report.android.recording.ScreenRecorder
import com.rubensousa.carioca.report.android.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.report.android.storage.FileIdGenerator
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.ReportProperty
import com.rubensousa.carioca.report.runtime.ReportStatus
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageStack
import com.rubensousa.carioca.report.runtime.TestMetadata

/**
 * The main entry point for all reports.
 *
 * Get the metadata of this test through [metadata] and/or [getProperty].
 *
 * To get the stages for reporting, use [getTestStages]
 */
abstract class InstrumentedTestReport internal constructor(
    outputPath: String,
    storageProvider: ReportStorageProvider,
    val metadata: TestMetadata,
    protected val interceptors: List<CariocaInstrumentedInterceptor>,
    private val recordingOptions: RecordingOptions,
    private val screenshotDelegate: ScreenshotDelegate,
    private val reporter: InstrumentedReporter,
    private val screenRecorder: ScreenRecorder,
) : InstrumentedStageReport(
    type = InstrumentedStageType.TEST,
    outputPath = outputPath,
    storageProvider = storageProvider
) {

    constructor(
        outputPath: String,
        metadata: TestMetadata,
        recordingOptions: RecordingOptions,
        screenshotDelegate: ScreenshotDelegate,
        reporter: InstrumentedReporter,
        interceptors: List<CariocaInstrumentedInterceptor>,
        storageProvider: ReportStorageProvider,
    ) : this(
        outputPath = outputPath,
        metadata = metadata,
        recordingOptions = recordingOptions,
        screenshotDelegate = screenshotDelegate,
        reporter = reporter,
        interceptors = interceptors,
        storageProvider = storageProvider,
        screenRecorder = ScreenRecorder(
            storageProvider,
            RecordingTaskFactoryImpl()
        ),
    )

    protected val stageStack = StageStack<InstrumentedStageReport>()

    override fun getTitle(): String {
        return getProperty<String>(ReportProperty.Title) ?: metadata.methodName
    }

    override fun getId(): String {
        return getProperty<String>(ReportProperty.Id) ?: metadata.fullName
    }

    fun onStarted() {
        interceptors.intercept { onTestStarted(this@InstrumentedTestReport) }
        if (recordingOptions.enabled) {
            startRecording()
        }
    }

    fun onPassed() {
        screenRecorder.stop(
            delete = !recordingOptions.keepOnSuccess
        )
        pass()
        interceptors.intercept { onTestPassed(this@InstrumentedTestReport) }
        writeReport()
    }

    fun onIgnored() {
        ignore()
        writeReport()
    }

    fun onFailed(error: Throwable) {
        /**
         * We also call [onFailed] internally inside the test action reports,
         * to ensure the report file is saved before the instrumentation marks the test as over.
         * Since tests can just use the rule without using the stage reports,
         * we can't distinguish the 2 use-cases without doing this
         */
        if (getExecutionMetadata().status == ReportStatus.FAILED) {
            return
        }
        // Take a screenshot asap to record the state on failures
        screenshotDelegate.takeScreenshot(this, "Screenshot of failure")

        // Now stop the recording, if there is one
        screenRecorder.stop(delete = false)

        // Now loop through the entire stage stack and mark all stages as failed
        var stage = stageStack.pop()
        while (stage != null) {
            val currentStage = stage
            currentStage.fail(error)
            interceptors.intercept { onStageFailed(currentStage) }
            stage = stageStack.pop()
        }

        fail(error)
        interceptors.intercept { onTestFailed(this@InstrumentedTestReport) }
        writeReport()
    }

    override fun reset() {
        super.reset()
        storageProvider.deleteTemporaryFiles()
        stageStack.clear()
    }

    private fun writeReport() {
        reporter.writeTestReport(metadata, this, storageProvider)
            .onFailure { error ->
                Log.e(
                    "InstrumentedTestReport",
                    "Failed writing report for test ${this.metadata.methodName}",
                    error
                )
            }
    }

    private fun startRecording() {
        val newRecording = screenRecorder.start(
            filename = FileIdGenerator.get(),
            options = recordingOptions,
            outputPath = outputPath
        )
        attach(createRecordingAttachment(newRecording))
    }

    private fun createRecordingAttachment(recording: ReportRecording): StageAttachment {
        return StageAttachment(
            description = "Screen recording",
            path = recording.relativeFilePath,
            mimeType = "video/mp4",
            keepOnSuccess = recordingOptions.keepOnSuccess
        )
    }

    override fun toString(): String {
        return "Test(fullName='${metadata.fullName}')"
    }

}
