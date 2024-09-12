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

package com.rubensousa.carioca.report.storage

import android.net.Uri
import androidx.test.platform.io.PlatformTestStorageRegistry
import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.stage.test.InstrumentedTestStageImpl
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
        report: InstrumentedTestStageImpl,
        reporter: CariocaInstrumentedReporter,
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