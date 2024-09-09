package com.rubensousa.carioca.report.recording

import com.rubensousa.carioca.report.internal.TestStorageDirectory
import java.io.File

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
