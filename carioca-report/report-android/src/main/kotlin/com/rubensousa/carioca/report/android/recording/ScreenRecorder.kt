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

package com.rubensousa.carioca.report.android.recording

import android.util.Log
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

internal class ScreenRecorder(
    private val storageProvider: ReportStorageProvider,
    private val taskFactory: RecordingTaskFactory,
) {

    private val buffer by lazy { ByteArray(1_000_000) }
    private var task: RecordingTask? = null

    fun start(
        options: RecordingOptions,
        outputPath: String,
        filename: String,
    ): ReportRecording {
        // We don't support concurrent recordings,
        // so cancel any ongoing task
        stop(delete = true)
        val outputDir = storageProvider.getOutputDir()
        val videoFilename = "${filename}.mp4"
        val relativePath = "$outputPath/$videoFilename"
        val absolutePath = "${outputDir.absolutePath}${relativePath}"
        val recording = ReportRecording(
            absoluteFilePath = absolutePath,
            relativeFilePath = relativePath,
            filename = videoFilename,
            tmpFile = createTmpFile(videoFilename)
        )
        val newTask = taskFactory.create(
            recording = recording,
            options = options
        )
        task = newTask
        newTask.start()
        return recording
    }

    fun stop(delete: Boolean) {
        val currentTask = task ?: return
        currentTask.stop(delete)
        // Now copy the file to the report storage
        if (!delete) {
            copyTemporaryFileToReportStorage(currentTask.getRecording())
        }
        task = null
    }

    private fun createTmpFile(filename: String): File {
        val outputDir = storageProvider.getOutputDir()
        outputDir.mkdirs()
        return File(outputDir, "tmp_$filename")
    }

    private fun copyTemporaryFileToReportStorage(recording: ReportRecording) {
        try {
            val outputStream = storageProvider.getOutputStream(recording.relativeFilePath)
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
        } catch (exception: Exception) {
           Log.e("ScreenRecorder", "Couldn't copy recording to test storage", exception)
        }
    }

}
