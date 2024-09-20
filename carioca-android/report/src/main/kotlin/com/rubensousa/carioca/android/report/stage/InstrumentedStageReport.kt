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

package com.rubensousa.carioca.android.report.stage

import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport
import java.io.OutputStream

abstract class InstrumentedStageReport(
    reportDirPath: String,
    protected val storageProvider: ReportStorageProvider,
) : StageReport() {

    val outputPath: String = if (!reportDirPath.startsWith("/")) {
        "/$reportDirPath"
    } else {
        reportDirPath
    }

    override fun deleteAttachment(attachment: StageAttachment) {
        storageProvider.delete(attachment.path)
    }

    fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputPath/$path"
        return TestStorageProvider.getOutputStream(relativePath)
    }

}
