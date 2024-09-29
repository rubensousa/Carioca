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

package com.rubensousa.carioca.report.android.interceptor

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.android.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.runtime.ExecutionMetadata
import com.rubensousa.carioca.report.runtime.StageAttachment

/**
 * A [CariocaInstrumentedInterceptor] that dumps the view hierarchy to a file whenever the test fails
 */
class DumpViewHierarchyInterceptor internal constructor(
    private val device: UiDevice,
) : CariocaInstrumentedInterceptor {

    constructor() : this(
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    )

    override fun onTestFailed(test: InstrumentedTestReport) {
        dump(test)
    }

    private fun getFilename(metadata: ExecutionMetadata): String {
        return metadata.uniqueId + "${metadata.status.name.lowercase()}_view_hierarchy.txt"
    }

    private fun dump(stage: InstrumentedStageReport) {
        try {
            val filename = getFilename(stage.getExecutionMetadata())
            val outputStream = stage.getAttachmentOutputStream(filename)
            device.dumpWindowHierarchy(outputStream)
            stage.attach(
                StageAttachment(
                    description = "View hierarchy dump",
                    path = filename,
                    mimeType = "text/plain",
                    keepOnSuccess = false
                )
            )
        } catch (exception: Exception) {
            // Ignore
        }
    }

}
