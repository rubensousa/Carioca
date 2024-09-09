package com.rubensousa.carioca.report.internal

import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.stage.TestReport
import java.io.OutputStream

internal object TestStorageProvider {

    private val testStorage by lazy { PlatformTestStorageRegistry.getInstance() }

    fun getRootOutputDir(): Uri {
        return testStorage.getOutputFileUri("")
    }

    fun getOutputUri(path: String): Uri {
        return testStorage.getOutputFileUri(path)
    }

    fun getTestOutputDir(
        report: TestReport,
        reporter: CariocaReporter,
    ): String {
        return "${getRootOutputDir().path}/${reporter.getOutputDir(report)}"
    }

    fun getOutputStream(uri: Uri): OutputStream {
        return testStorage.openOutputFile(uri.path)
    }

    fun getOutputStream(path: String): OutputStream {
        return testStorage.openOutputFile(path)
    }


}
