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

package com.rubensousa.carioca.android.report.stage

import android.util.Log
import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.recording.ReportRecording
import com.rubensousa.carioca.android.report.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.android.report.storage.FileIdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageDirectory
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.report.runtime.ReportProperty
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
abstract class InstrumentedTestReport(
    outputPath: String,
    val metadata: TestMetadata,
    protected val recordingOptions: RecordingOptions,
    protected val screenshotDelegate: ScreenshotDelegate,
    protected val reporter: CariocaInstrumentedReporter,
    protected val interceptors: List<CariocaInstrumentedInterceptor>,
) : InstrumentedStageReport(reportDirPath = outputPath) {

    protected val stageStack = StageStack<InstrumentedStageReport>()
    private var screenRecording: ReportRecording? = null

    override fun getType(): String = "Test"

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
        screenRecording?.let {
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = !recordingOptions.keepOnSuccess
            )
        }
        pass()
        interceptors.intercept { onTestPassed(this@InstrumentedTestReport) }
        writeReport()
    }

    fun onIgnored() {
        ignore()
        writeReport()
    }

    fun onFailed(error: Throwable) {
        // Take a screenshot asap to record the state on failures
        screenshotDelegate.takeScreenshot(this, "Screenshot of failure")

        // Now stop the recording, if there is one
        screenRecording?.let { activeRecording ->
            DeviceScreenRecorder.stopRecording(
                recording = activeRecording,
                delete = false
            )
        }

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
        deleteReportFiles()
        stageStack.clear()
        screenRecording = null
    }

    private fun writeReport() {
        try {
            reporter.writeTestReport(metadata, this, TestStorageProvider)
        } catch (exception: Exception) {
            Log.e("CariocaReport", "Failed writing report for test ${this.metadata.methodName}", exception)
        }
    }

    private fun deleteReportFiles() {
        TestStorageDirectory.tmpOutputDir.listFiles()?.forEach { file ->
            deleteFile(file)
        }
    }

    private fun startRecording() {
        val newRecording = DeviceScreenRecorder.startRecording(
            filename = FileIdGenerator.get(),
            options = recordingOptions,
            relativeOutputDirPath = outputPath
        )
        attach(createRecordingAttachment(newRecording))
        screenRecording = newRecording
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
