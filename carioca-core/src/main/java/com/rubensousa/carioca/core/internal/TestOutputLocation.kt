package com.rubensousa.carioca.core.internal

import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import org.junit.runner.Description
import java.io.OutputStream
import java.util.UUID

internal object TestOutputLocation {

    private val testStorage by lazy { PlatformTestStorageRegistry.getInstance() }
    private val reportDir = "carioca_report"

    fun getOutputPath(description: Description): Uri {
        val className = description.testClass.name
        val methodName = description.methodName
        val relativePath = "$reportDir/$className/$methodName"
        return testStorage.getOutputFileUri(relativePath)
    }

    fun getScreenshotUri(outputDir: Uri): Uri {
        val filename = UUID.randomUUID().toString() + ".png"
        val screenshotPath = "${outputDir.path}/$filename"
        return testStorage.getOutputFileUri(screenshotPath)
    }

    fun getScreenshotOutputStream(uri: Uri): OutputStream {
        return testStorage.openOutputFile(uri.path)
    }

}
