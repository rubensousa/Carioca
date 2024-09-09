package com.rubensousa.carioca.report.recording

import android.util.Log
import com.rubensousa.carioca.report.internal.TestStorageDirectory
import com.rubensousa.carioca.report.internal.TestStorageProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors


object ScreenRecorder {

    private val TAG = "ScreenRecorder"
    private val executor by lazy { Executors.newFixedThreadPool(2) }
    private val buffer by lazy { ByteArray(1_000_000) }
    private var task: RecordingTask? = null

    fun startRecording(
        filename: String,
        options: RecordingOptions,
        outputDirPath: String,
    ): ScreenRecording {
        task?.stop(delete = true)
        val outputDir = TestStorageDirectory.outputDir
        val videoFilename = "${filename}.mp4"
        val relativePath = "$outputDirPath/$videoFilename"
        val absolutePath = "${outputDir.absolutePath}${relativePath}"
        val recording = ScreenRecording(
            absoluteFilePath = absolutePath,
            relativeFilePath = relativePath,
            filename = videoFilename,
            options = options
        )
        val newTask = RecordingTask(
            tag = TAG,
            executor = executor,
            recording = recording,
            recordingFile = getTmpRecordingFile(recording)
        )
        Log.i(TAG, "Requested recording for: $relativePath")
        task = newTask
        newTask.start()
        return recording
    }

    fun stopRecording(recording: ScreenRecording, delete: Boolean) {
        task?.stop(delete)
        // Now copy the file to the test storage
        if (!delete) {
            copyRecordingToTestStorage(recording)
        }
    }

    private fun copyRecordingToTestStorage(recording: ScreenRecording) {
        val outputStream = TestStorageProvider.getOutputStream(recording.relativeFilePath)
        val tmpFile = getTmpRecordingFile(recording)
        val inputStream = BufferedInputStream(FileInputStream(tmpFile))
        var bytesRead = inputStream.read(buffer)
        while (bytesRead >= 0) {
            outputStream.write(buffer, 0, bytesRead)
            outputStream.flush()
            bytesRead = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
        // tmpFile.delete()
    }

    private fun getTmpRecordingFile(recording: ScreenRecording): File {
        val outputDir = TestStorageDirectory.outputDir
        outputDir.mkdirs()
        val file = File(outputDir, "tmp_"+recording.filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

}
