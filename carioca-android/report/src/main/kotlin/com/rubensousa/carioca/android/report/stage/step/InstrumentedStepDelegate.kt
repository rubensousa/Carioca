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

package com.rubensousa.carioca.android.report.stage.step

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.storage.IdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.stage.StageStack

internal class InstrumentedStepDelegate(
    private val stack: StageStack,
    private val reporter: CariocaInstrumentedReporter,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
) {

    fun create(title: String, id: String?): InstrumentedStepStageImpl {
        val step = buildStep(title, id)
        stack.push(step)
        return step
    }

    fun execute(
        step: InstrumentedStepStageImpl,
        action: InstrumentedStepScope.() -> Unit,
    ) {
        interceptors.intercept { onStageStarted(step) }
        step.execute(action)
        stack.pop()
        interceptors.intercept { onStagePassed(step) }
    }

    fun takeScreenshot(description: String): ReportAttachment? {
        val screenshotUri = DeviceScreenshot.take(
            storageDir = TestStorageProvider.getOutputUri(outputPath),
            options = screenshotOptions,
            filename = reporter.getScreenshotName(IdGenerator.get())
        ) ?: return null
        return ReportAttachment(
            path = screenshotUri.path!!,
            description = description,
            mimeType = getScreenshotMimeType(),
        )
    }

    private fun getScreenshotMimeType(): String {
        return when (screenshotOptions.getFileExtension()) {
            ".png" -> "image/png"
            ".webp" -> "image/webp"
            else -> "image/jpg"
        }
    }

    private fun buildStep(title: String, id: String?): InstrumentedStepStageImpl {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = InstrumentedStepStageImpl(
            id = stepId,
            title = title,
            delegate = this,
        )
        return step
    }

}
