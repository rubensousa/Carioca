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

import android.util.Log
import com.rubensousa.carioca.report.storage.TestStorageDirectory
import com.rubensousa.carioca.report.storage.TestStorageProvider
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.concurrent.Executors


object DeviceScreenRecorder {

    private const val TAG = "ScreenRecorder"

    private val executor by lazy { Executors.newFixedThreadPool(1) }
    private val buffer by lazy { ByteArray(1_000_000) }
    private var task: RecordingTask? = null

    fun startRecording(
        options: RecordingOptions,
        relativeOutputDirPath: String,
        filename: String,
    ): ReportRecording {
        task?.stop(delete = true)
        val outputDir = TestStorageDirectory.outputDir
        val videoFilename = "${filename}.mp4"
        val relativePath = "$relativeOutputDirPath/$videoFilename"
        val absolutePath = "${outputDir.absolutePath}${relativePath}"
        val recording = ReportRecording(
            absoluteFilePath = absolutePath,
            relativeFilePath = relativePath,
            filename = videoFilename,
        )
        val newTask = RecordingTask(
            tag = TAG,
            executor = executor,
            recordingFile = recording.tmpFile,
            options = options
        )
        Log.i(TAG, "Requested recording for: $relativePath")
        task = newTask
        newTask.start()
        return recording
    }

    fun stopRecording(recording: ReportRecording, delete: Boolean) {
        Log.i(TAG, "Stopping recording: ${recording.absoluteFilePath}")
        task?.stop(delete)
        // Now copy the file to the test storage
        if (!delete) {
            copyRecordingToTestStorage(recording)
        }
    }

    private fun copyRecordingToTestStorage(recording: ReportRecording) {
        val outputStream = TestStorageProvider.getOutputStream(recording.relativeFilePath)
        val tmpFile = recording.tmpFile
        val inputStream = BufferedInputStream(FileInputStream(tmpFile))
        var bytesRead = inputStream.read(buffer)
        while (bytesRead >= 0) {
            outputStream.write(buffer, 0, bytesRead)
            outputStream.flush()
            bytesRead = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
        tmpFile.delete()
    }


}
