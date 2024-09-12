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

package com.rubensousa.carioca.android.report.stage.test

import android.util.Log
import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.recording.ReportRecording
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedStage
import com.rubensousa.carioca.android.report.stage.InstrumentedStageDelegate
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.android.report.storage.FileIdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.stage.StageStack
import java.io.OutputStream

/**
 * The main entry point for all reports.
 *
 * Get the metadata of this test through [getMetadata] and/or [getProperties].
 *
 * To get the stages for reporting, use [getStages]
 */
class InstrumentedTest internal constructor(
    private val metadata: InstrumentedTestMetadata,
    private val recordingOptions: RecordingOptions,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaInstrumentedReporter,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
) : InstrumentedStage<InstrumentedTestMetadata>(), InstrumentedTestScope {

    private val stageStack = StageStack()
    private val attachments = mutableListOf<ReportAttachment>()
    private val outputDir = TestStorageProvider.getTestOutputDir(this, reporter)
    private val stageDelegate = InstrumentedStageDelegate(
        stack = stageStack,
        reporter = reporter,
        interceptors = interceptors,
        outputPath = outputDir,
        screenshotOptions = screenshotOptions,
    )
    private var screenRecording: ReportRecording? = null

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = stageDelegate.createStep(title, id)
        addStage(step)
        stageDelegate.executeStep(step, action)
    }

    override fun scenario(scenario: InstrumentedTestScenario) {
        val newScenario = stageDelegate.createScenario(scenario)
        addStage(newScenario)
        stageDelegate.executeScenario(newScenario)
    }

    override fun getMetadata(): InstrumentedTestMetadata = metadata

    fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputDir/$path"
        return TestStorageProvider.getOutputStream(relativePath)
    }

    internal fun starting() {
        intercept { onTestStarted(this@InstrumentedTest) }
        if (recordingOptions.enabled) {
            val filename = reporter.getRecordingName(FileIdGenerator.get())
            screenRecording = DeviceScreenRecorder.startRecording(
                filename = filename,
                options = recordingOptions,
                relativeOutputDirPath = outputDir
            )
        }
    }

    internal fun failed(error: Throwable) {
        // Take a screenshot asap to record the state on failures
        stageDelegate.takeScreenshot("Screenshot of Failure")?.let {
            attachments.add(it)
        }

        // Now stop the recording, if there is one, and attach it
        screenRecording?.let { activeRecording ->
            attachments.add(createRecordingAttachment(activeRecording))
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
            intercept { onStageFailed(currentStage) }
            stage = stageStack.pop()
        }

        fail(error)
        intercept { onTestFailed(this@InstrumentedTest) }
        writeReport()
    }

    internal fun succeeded() {
        pass()
        intercept { onTestPassed(this@InstrumentedTest) }
        screenRecording?.let {
            val delete = !recordingOptions.keepOnSuccess
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = delete
            )
            if (delete) {
                screenRecording = null
            } else {
                attachments.add(createRecordingAttachment(it))
            }
        }
        writeReport()
    }

    // Ensures there is no persistent state across test re-executions
    private fun reset() {
        resetState()
        stageStack.clear()
        screenRecording = null
    }

    private fun writeReport() {
        val file = "$outputDir/${reporter.getReportFilename(this)}"
        val outputStream = TestStorageProvider.getOutputStream(file)
        try {
            reporter.writeTestReport(this, outputStream)
        } catch (exception: Exception) {
            Log.e("CariocaReport", "Failed writing report for test ${this.metadata.methodName}", exception)
        } finally {
            outputStream.close()
        }
        reset()
    }

    private fun intercept(action: CariocaInstrumentedInterceptor.() -> Unit) {
        interceptors.intercept(action)
    }

    private fun createRecordingAttachment(recording: ReportRecording): ReportAttachment {
        return ReportAttachment(
            description = "Screen recording",
            path = recording.relativeFilePath,
            mimeType = "video/mp4"
        )
    }

    override fun toString(): String {
        return "Test(id='${metadata.testId}', fullName='${metadata.getTestFullName()}')"
    }

}
