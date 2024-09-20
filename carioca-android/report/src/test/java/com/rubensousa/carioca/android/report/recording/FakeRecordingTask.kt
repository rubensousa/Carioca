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

package com.rubensousa.carioca.android.report.recording

import com.rubensousa.carioca.android.report.storage.ReportStorageProvider

class FakeRecordingTask(
    private val recording: ReportRecording,
    private val storageProvider: ReportStorageProvider,
) : RecordingTask {

    override fun start() {
        storageProvider.getOutputStream(recording.tmpFile.name)
            .use {
                it.write(0)
                it.flush()
            }
    }

    override fun stop(delete: Boolean) {
        if (delete) {
            storageProvider.delete(recording.tmpFile.name)
        }
    }

    override fun getRecording(): ReportRecording {
        return recording
    }

}
