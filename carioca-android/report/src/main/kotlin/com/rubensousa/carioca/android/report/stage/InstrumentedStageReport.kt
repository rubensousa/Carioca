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

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.android.report.storage.TestStorageDirectory
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.junit.report.StageReport
import java.io.File
import java.io.OutputStream

abstract class InstrumentedStageReport(
    reportDirPath: String,
) : StageReport() {

    private val attachments = mutableListOf<StageAttachment>()
    protected val outputPath: String = if (!reportDirPath.startsWith("/")) {
        "/$reportDirPath"
    } else {
        reportDirPath
    }

    fun attach(attachment: StageAttachment) {
        attachments.add(attachment)
    }

    fun getAttachments(): List<StageAttachment> = attachments.toList()

    fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputPath/$path"
        return TestStorageProvider.getOutputStream(relativePath)
    }

    override fun reset() {
        super.reset()
        attachments.forEach { attachment ->
            deleteAttachment(attachment)
        }
        attachments.clear()
    }

    /**
     * This is called when the test passes to remove all attachments
     * that did not request [StageAttachment.keepOnSuccess].
     */
    internal fun deleteUnnecessaryAttachments() {
        val iterator = attachments.iterator()
        while (iterator.hasNext()) {
            val attachment = iterator.next()
            if (!attachment.keepOnSuccess) {
                deleteAttachment(attachment)
                iterator.remove()
            }
        }
    }

    private fun deleteAttachment(attachment: StageAttachment) {
        try {
            val outputFile = File(TestStorageDirectory.outputDir, attachment.path)
            if (outputFile.exists()) {
                deleteFile(outputFile)
                outputFile.delete()
            } else {
                val tmpFile = File(TestStorageDirectory.tmpOutputDir, attachment.path)
                if (tmpFile.exists()) {
                    deleteFile(tmpFile)
                }
            }
        } catch (exception: Exception) {
            // Ignore
        }
    }

    internal fun deleteFile(file: File) {
        if (!file.delete()) {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .executeShellCommand("rm ${file.absolutePath}")
        }
    }

}
