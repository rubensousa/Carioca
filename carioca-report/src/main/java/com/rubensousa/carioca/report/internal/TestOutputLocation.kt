package com.rubensousa.carioca.report.internal

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import com.rubensousa.carioca.report.screenshot.CariocaScreenshots
import org.junit.runner.Description
import java.io.OutputStream

internal object TestOutputLocation {

    private val testStorage by lazy { PlatformTestStorageRegistry.getInstance() }
    private val reportDir = "carioca_report"

    fun getOutputPath(description: Description): Uri {
        val className = description.testClass.name
        val methodName = description.methodName
        val relativePath = "$reportDir/$className/$methodName"
        return testStorage.getOutputFileUri(relativePath)
    }

    fun getGlobalReportOutputStream(filename: String): OutputStream {
        val reportPath = "$reportDir/$filename"
        return testStorage.openOutputFile(reportPath)
    }

    fun getScreenshotUri(outputDir: Uri): Uri {
        val filename = IdGenerator.get() + getExtension()
        val screenshotPath = "${outputDir.path}/screenshots/$filename"
        return testStorage.getOutputFileUri(screenshotPath)
    }

    fun getOutputStream(uri: Uri): OutputStream {
        return testStorage.openOutputFile(uri.path)
    }

    fun getOutputStream(dir: Uri, filename: String): OutputStream {
        val path = "${dir.path}/$filename"
        return testStorage.openOutputFile(path)
    }

    private fun getExtension(): String {
        return when (CariocaScreenshots.format) {
            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.JPEG -> ".jpg"
            else -> ".webp"
        }
    }

}
