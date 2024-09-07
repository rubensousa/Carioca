package com.rubensousa.carioca.report.internal

import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import com.rubensousa.carioca.report.CariocaReporter
import java.io.OutputStream

internal object TestOutputLocation {

    private val testStorage by lazy { PlatformTestStorageRegistry.getInstance() }

    fun getRootOutputDir(): Uri {
        return testStorage.getOutputFileUri("")
    }

    fun getScreenshotUri(
        reporter: CariocaReporter,
        testOutputPath: String,
        extension: String,
    ): Uri {
        val id = IdGenerator.get()
        val name = reporter.getScreenshotName(id)
        val filename = name + extension
        val screenshotPath = "$testOutputPath/$filename"
        return testStorage.getOutputFileUri(screenshotPath)
    }

    fun getOutputStream(uri: Uri): OutputStream {
        return testStorage.openOutputFile(uri.path)
    }

    fun getOutputStream(path: String): OutputStream {
        return testStorage.openOutputFile(path)
    }


}
