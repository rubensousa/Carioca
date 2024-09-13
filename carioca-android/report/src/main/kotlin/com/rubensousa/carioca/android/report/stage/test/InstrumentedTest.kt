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
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStepScope
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineTestScope
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.recording.DeviceScreenRecorder
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.recording.ReportRecording
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedStage
import com.rubensousa.carioca.android.report.stage.InstrumentedStageDelegate
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.android.report.storage.FileIdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.junit.report.StageStack

/**
 * The main entry point for all reports.
 *
 * Get the metadata of this test through [getMetadata] and/or [getProperties].
 *
 * To get the stages for reporting, use [getStages]
 */
class InstrumentedTest internal constructor(
    outputPath: String,
    private val metadata: InstrumentedTestMetadata,
    private val recordingOptions: RecordingOptions,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaInstrumentedReporter,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
) : InstrumentedStage(outputPath), InstrumentedTestScope, InstrumentedCoroutineTestScope {

    private val stageStack = StageStack<InstrumentedStage>()
    private val stageDelegate = InstrumentedStageDelegate(
        stack = stageStack,
        reporter = reporter,
        interceptors = interceptors,
        outputPath = outputPath,
        screenshotOptions = screenshotOptions,
    )
    private var screenRecording: ReportRecording? = null

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = stageDelegate.createStep(title, id)
        addStage(step)
        stageDelegate.executeStep(step, action)
    }

    override suspend fun step(
        title: String,
        id: String?,
        action: suspend InstrumentedCoroutineStepScope.() -> Unit,
    ) {
        val step = stageDelegate.createStep(title, id)
        addStage(step)
        stageDelegate.executeStep(step, action)
    }

    override fun scenario(scenario: InstrumentedTestScenario) {
        val newScenario = stageDelegate.createScenario(scenario)
        addStage(newScenario)
        stageDelegate.executeScenario(newScenario)
    }

    override suspend fun scenario(scenario: InstrumentedCoroutineScenario) {
        val newScenario = stageDelegate.createCoroutineScenario(scenario)
        addStage(newScenario)
        stageDelegate.executeCoroutineScenario(newScenario)
    }

    fun getMetadata(): InstrumentedTestMetadata = metadata

    internal fun starting() {
        intercept { onTestStarted(this@InstrumentedTest) }
        if (recordingOptions.enabled) {
            startRecording()
        }
    }

    internal fun failed(error: Throwable) {
        // Take a screenshot asap to record the state on failures
        stageDelegate.takeScreenshot("Screenshot of Failure")?.let {
            attach(it)
        }

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
            DeviceScreenRecorder.stopRecording(
                recording = it,
                delete = !recordingOptions.keepOnSuccess
            )
        }
        deleteAttachmentsOnSuccess()
        writeReport()
    }

    // Ensures there is no persistent state across test re-executions
    override fun reset() {
        super.reset()
        stageStack.clear()
        screenRecording = null
    }

    private fun writeReport() {
        val file = "$outputPath/${reporter.getReportFilename(this)}"
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

    private fun startRecording() {
        val filename = reporter.getRecordingName(FileIdGenerator.get())
        val newRecording = DeviceScreenRecorder.startRecording(
            filename = filename,
            options = recordingOptions,
            relativeOutputDirPath = outputPath
        )
        attach(createRecordingAttachment(newRecording))
        screenRecording = newRecording
    }

    private fun deleteAttachmentsOnSuccess() {
        stageStack.getAll().forEach { stage ->
            stage.deleteUnnecessaryAttachments()
        }
        deleteUnnecessaryAttachments()
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
        return "Test(fullName='${metadata.getTestFullName()}')"
    }

}
