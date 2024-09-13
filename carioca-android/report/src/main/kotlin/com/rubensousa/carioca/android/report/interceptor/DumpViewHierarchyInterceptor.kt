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

package com.rubensousa.carioca.android.report.interceptor

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.android.report.stage.InstrumentedStage
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest
import com.rubensousa.carioca.stage.ExecutionMetadata

class DumpViewHierarchyInterceptor(
    private val dumpOnEveryStage: Boolean = false,
    private val keepOnSuccess: Boolean = false,
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()),
) : CariocaInstrumentedInterceptor {

    override fun onStageStarted(stage: InstrumentedStage) {
        if (dumpOnEveryStage) {
            dump(stage)
        }
    }

    override fun onTestFailed(test: InstrumentedTest) {
        dump(test)
    }

    private fun getFilename(metadata: ExecutionMetadata): String {
        return metadata.uniqueId + "${metadata.status.name.lowercase()}_view_hierarchy.txt"
    }

    private fun dump(stage: InstrumentedStage) {
        try {
            val filename = getFilename(stage.getExecutionMetadata())
            val outputStream = stage.getAttachmentOutputStream(filename)
            device.dumpWindowHierarchy(outputStream)
            stage.attach(
                StageAttachment(
                    description = "View hierarchy dump",
                    path = filename,
                    mimeType = "text/plain",
                    keepOnSuccess = keepOnSuccess
                )
            )
        } catch (exception: Exception) {
            // Ignore
        }
    }

}
