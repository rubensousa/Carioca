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

package com.rubensousa.carioca.report.android.fake

import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport

class FakeStageReport(
    private val storageProvider: ReportStorageProvider = FakeReportStorageProvider(),
) : StageReport() {

    override fun deleteAttachment(attachment: StageAttachment) {

    }

    override fun getType(): String = "Type"

    override fun getTitle(): String = "Title"
}