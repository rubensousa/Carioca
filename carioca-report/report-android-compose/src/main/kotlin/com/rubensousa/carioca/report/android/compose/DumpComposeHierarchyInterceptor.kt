/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

package com.rubensousa.carioca.report.android.compose

import android.util.Log
import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.runtime.ExecutionMetadata
import com.rubensousa.carioca.report.runtime.StageAttachment

/**
 * A [CariocaInstrumentedInterceptor] that dumps the compose hierarchy on test failures
 *
 * @param useUnmergedTree Find within merged composables like Buttons
 */
class DumpComposeHierarchyInterceptor(
    private val useUnmergedTree: Boolean = true,
) : CariocaInstrumentedInterceptor {

    override fun onTestFailed(test: InstrumentedTestReport) {
        super.onTestFailed(test)
        dump(test)
    }

    private fun dump(stage: InstrumentedTestReport) {
        try {
            val filename = getFilename(stage.getExecutionMetadata())
            val dump = ComposeHierarchyInspector.dump(useUnmergedTree)
            if (dump.isBlank()) {
                // No compose hierarchies found, do nothing
                return
            }
            val outputStream = stage.getAttachmentOutputStream(filename)
            outputStream.use {
                outputStream.bufferedWriter().apply {
                    write(dump)
                    flush()
                }
            }
            stage.attach(
                StageAttachment(
                    description = "Compose hierarchy dump",
                    path = filename,
                    mimeType = "text/plain",
                    keepOnSuccess = false
                )
            )
        } catch (exception: Exception) {
            Log.e(
                "ComposeHierarchyDump",
                "Failed to dump compose hierarchy", exception
            )
        }
    }

    private fun getFilename(metadata: ExecutionMetadata): String {
        return metadata.uniqueId + "_compose_hierarchy.txt"
    }

}
