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

package com.rubensousa.carioca.report.android.screenshot

import com.rubensousa.carioca.report.android.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.android.storage.FileIdGenerator
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.StageAttachment

class ScreenshotDelegate(
    private val outputPath: String,
    private val defaultOptions: ScreenshotOptions,
    private val storageProvider: ReportStorageProvider,
) {

    fun takeScreenshot(
        stage: InstrumentedStageReport,
        description: String,
        optionsOverride: ScreenshotOptions? = null,
    ) {
        val options = optionsOverride ?: defaultOptions
        val screenshotUri = DeviceScreenshot.take(
            storageDir = storageProvider.getOutputUri(outputPath),
            options = options,
            filename = FileIdGenerator.get()
        ) ?: return
        val newAttachment = StageAttachment(
            path = screenshotUri.path!!,
            description = description,
            mimeType = getScreenshotMimeType(options),
            keepOnSuccess = options.keepOnSuccess
        )
        stage.attach(newAttachment)
    }

    private fun getScreenshotMimeType(options: ScreenshotOptions): String {
        return when (options.getFileExtension()) {
            ".png" -> "image/png"
            ".webp" -> "image/webp"
            else -> "image/jpg"
        }
    }

}
