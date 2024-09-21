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
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport
import java.io.OutputStream

/**
 * A [StageReport] for instrumented tests.
 * Attachments should be stored in [storageProvider]
 */
abstract class InstrumentedStageReport(
    private val type: InstrumentedStageType,
    protected val outputPath: String,
    protected val storageProvider: ReportStorageProvider,
) : StageReport() {

    override fun getType(): String = type.id

    override fun deleteAttachment(attachment: StageAttachment) {
        storageProvider.delete(attachment.path)
    }

    fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputPath/$path"
        return storageProvider.getOutputStream(relativePath)
    }

    override fun toString(): String {
        return "$type - ${getTitle()}"
    }

}
