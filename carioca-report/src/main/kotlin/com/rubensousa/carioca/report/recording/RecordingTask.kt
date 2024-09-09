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

package com.rubensousa.carioca.report.recording

import android.os.Build
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

internal class RecordingTask(
    private val tag: String,
    private val executor: Executor,
    private val recordingFile: File,
    private val options: RecordingOptions,
) {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val recordingLatch = RecordingLatch()
    private val fileObserver = FileObserverCompat(recordingFile.path) { event ->
        if (event == FileObserver.CLOSE_WRITE) {
            recordingLatch.setWritingFinished()
        } else if (event == FileObserver.CREATE || event == FileObserver.MODIFY) {
            recordingLatch.setWritingStarted()
        }
    }
    private var requestStartTime = 0L

    fun start() {
        executor.execute {
            startRecording()
        }
        recordingLatch.awaitFirstWrite()
    }

    @WorkerThread
    private fun startRecording() {
        val displayMetrics = InstrumentationRegistry.getInstrumentation().targetContext
            .applicationContext.resources.displayMetrics
        try {
            val width = scaleToDivisibleByEight(displayMetrics.widthPixels, options.resolutionScale)
            val height = scaleToDivisibleByEight(displayMetrics.heightPixels, options.resolutionScale)
            val command = getScreenRecordingCommand(
                primaryResolution = if (width > height) width else height,
                secondaryResolution = if (width > height) height else width
            )
            Log.i(tag, "Starting screen recording: $command")
            requestStartTime = System.currentTimeMillis()
            fileObserver.startWatching()
            device.executeShellCommand(command)
        } catch (e: Exception) {
            Log.e(tag, "Screen recording failed", e)
        } finally {
            recordingLatch.setWritingFinished()
        }
    }

    fun stop(delete: Boolean) {
        val stopLatch = CountDownLatch(1)
        executor.execute {
            stopRecording(delete)
            stopLatch.countDown()
        }

        stopLatch.await()
        fileObserver.stopWatching()
    }

    @WorkerThread
    private fun stopRecording(delete: Boolean) {
        try {
            if (delete) {
                // Kill the process immediately and delete the file
                device.executeShellCommand("pkill -9 screenrecord")
                device.executeShellCommand("rm ${recordingFile.path}")
            } else {
                // Wait for a minimum amount of time before finishing to ensure the recording contains the last steps
                Thread.sleep(options.stopDelay)

                // Kill the process safely
                device.executeShellCommand("pkill -SIGINT screenrecord")

                // Wait for the last write before exiting
                recordingLatch.awaitLastWrite()
            }
        } catch (exception: Exception) {
            // Ignore
            Log.e(tag, "Error while trying to stop the recording", exception)
        }
    }

    private fun getScreenRecordingCommand(
        primaryResolution: Int,
        secondaryResolution: Int,
    ): String {
        val commandBuilder = StringBuilder()
        commandBuilder.append("screenrecord --size ${primaryResolution}x${secondaryResolution} ")
        commandBuilder.append("--bit-rate ${options.bitrate} ")
        commandBuilder.append(recordingFile.absolutePath)
        return commandBuilder.toString()
    }

    private fun scaleToDivisibleByEight(value: Int, scale: Float): Int {
        val scaledValue = (value * scale).toInt()
        return (scaledValue / 8) * 8
    }

    private class RecordingLatch {

        private val startLatch = CountDownLatch(1)
        private val finishLatch = CountDownLatch(1)
        private val handler = Handler(Looper.getMainLooper())
        private val firstWriteTimeout = 2L
        private val lastWriteTimeout = 5L
        private var startedWriting = false
        private var finishedWriting = false

        fun awaitFirstWrite() {
            try {
                startLatch.await(firstWriteTimeout, TimeUnit.SECONDS)
            } catch (exception: Exception) {
                // Ignore
            }
        }

        fun setWritingStarted() {
            if (startedWriting) {
                return
            }
            startedWriting
            handler.post {
                startLatch.countDown()
            }
        }

        fun awaitLastWrite() {
            try {
                finishLatch.await(lastWriteTimeout, TimeUnit.SECONDS)
            } catch (exception: Exception) {
                // Ignore
            }
        }

        fun setWritingFinished() {
            if (finishedWriting) {
                return
            }
            finishedWriting = true
            handler.post {
                finishLatch.countDown()
            }
        }
    }

    /**
     * Keeps track of video recording file modifications
     */
    private class FileObserverCompat(
        path: String,
        onEvent: (Int) -> Unit,
    ) {

        private var stopWatching = false
        private val delegate: FileObserver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(File(path)) {
                override fun onEvent(event: Int, path: String?) {
                    if (!stopWatching) {
                        onEvent(event)
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            object : FileObserver(path) {
                override fun onEvent(event: Int, path: String?) {
                    if (!stopWatching) {
                        onEvent(event)
                    }
                }
            }
        }

        fun startWatching() {
            delegate.startWatching()
        }

        fun stopWatching() {
            delegate.stopWatching()
        }

    }

}
