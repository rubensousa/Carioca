package com.rubensousa.carioca.report.recording

import android.util.Log
import com.rubensousa.carioca.report.internal.TestStorageDirectory
import com.rubensousa.carioca.report.internal.TestStorageProvider
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.concurrent.Executors


object DeviceScreenRecorder {

    private const val TAG = "ScreenRecorder"

    // 1 thread for starting the recording, another thread to stop the recording
    private val executor by lazy { Executors.newFixedThreadPool(2) }
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
            recording = recording,
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
