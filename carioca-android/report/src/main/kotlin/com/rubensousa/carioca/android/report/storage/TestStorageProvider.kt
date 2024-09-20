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

package com.rubensousa.carioca.android.report.storage

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.io.OutputStream

/**
 * An implementation of [ReportStorageProvider] that uses the test platform's directories
 */
object TestStorageProvider : ReportStorageProvider {

    private val testStorage by lazy { PlatformTestStorageRegistry.getInstance() }

    override fun getOutputDir(): File {
        return TestStorageDirectory.outputDir
    }

    override fun getOutputStream(path: String): OutputStream {
        return testStorage.openOutputFile(path)
    }

    override fun delete(path: String) {
        try {
            val outputFile = File(TestStorageDirectory.outputDir, path)
            if (outputFile.exists()) {
                deleteFile(outputFile)
                outputFile.delete()
            } else {
                val tmpFile = File(TestStorageDirectory.tmpOutputDir, path)
                if (tmpFile.exists()) {
                    deleteFile(tmpFile)
                }
            }
        } catch (exception: Exception) {
            // Ignore
        }
    }

    override fun deleteTemporaryFiles() {
        TestStorageDirectory.tmpOutputDir.listFiles()?.forEach { file ->
            try {
                deleteFile(file)
            } catch (exception: Exception) {
                // Ignore
            }
        }
    }

    override fun getOutputUri(path: String): Uri {
        return testStorage.getOutputFileUri(path)
    }

    fun getOutputStream(uri: Uri): OutputStream {
        return testStorage.openOutputFile(uri.path)
    }

    private fun deleteFile(file: File) {
        if (!file.delete()) {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .executeShellCommand("rm ${file.absolutePath}")
        }
    }

}
