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
import com.rubensousa.carioca.stage.AbstractCariocaStage
import com.rubensousa.carioca.stage.CariocaStage
import java.io.File
import java.io.OutputStream

abstract class InstrumentedStage(
    protected val outputPath: String,
) : AbstractCariocaStage() {

    private val attachments = mutableListOf<StageAttachment>()
    private val properties = mutableMapOf<String, Any>()
    private val stages = mutableListOf<CariocaStage>()

    fun attach(attachment: StageAttachment) {
        attachments.add(attachment)
    }

    fun getAttachments(): List<StageAttachment> = attachments.toList()

    fun getAttachmentOutputStream(path: String): OutputStream {
        val relativePath = "$outputPath/$path"
        return TestStorageProvider.getOutputStream(relativePath)
    }

    fun addProperty(key: String, value: Any) {
        properties[key] = value
    }

    fun getProperties(): Map<String, Any> = properties.toMap()

    override fun getStages(): List<CariocaStage> = stages.toList()

    protected fun addStage(stage: CariocaStage) {
        stages.add(stage)
    }

    protected fun resetState() {
        stages.clear()
        properties.clear()
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
                deleteAttachmentFile(outputFile)
                outputFile.delete()
            } else {
                val tmpFile = File(TestStorageDirectory.tmpOutputDir, attachment.path)
                if (tmpFile.exists()) {
                    deleteAttachmentFile(tmpFile)
                }
            }
        } catch (exception: Exception) {
            // Ignore
        }
    }

    private fun deleteAttachmentFile(file: File) {
        if (!file.delete()) {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .executeShellCommand("rm ${file.absolutePath}")
        }
    }

}
