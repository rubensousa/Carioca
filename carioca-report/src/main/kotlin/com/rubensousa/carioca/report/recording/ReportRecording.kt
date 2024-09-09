package com.rubensousa.carioca.report.recording

import com.rubensousa.carioca.report.internal.TestStorageDirectory
import java.io.File

/**
 * A recording taken during a test report
 *
 * @param absoluteFilePath the absolute file path to the video recording
 * @param relativeFilePath the relative file path inside the test storage directory
 * @param filename the filename of the video recording file
 */
data class ReportRecording(
    val absoluteFilePath: String,
    val relativeFilePath: String,
    val filename: String,
) {

    internal val tmpFile: File = createTmpFile()

    private fun createTmpFile(): File {
        val outputDir = TestStorageDirectory.outputDir
        outputDir.mkdirs()
        val file = File(outputDir, "tmp_$filename")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

}
