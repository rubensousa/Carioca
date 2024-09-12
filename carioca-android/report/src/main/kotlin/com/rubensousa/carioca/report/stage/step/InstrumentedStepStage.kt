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

package com.rubensousa.carioca.report.stage.step

import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.AbstractStage
import com.rubensousa.carioca.report.stage.InstrumentedStage
import com.rubensousa.carioca.report.storage.IdGenerator
import com.rubensousa.carioca.report.storage.TestStorageProvider

interface InstrumentedStepStage : InstrumentedStage {

    fun getStages(): List<InstrumentedStage>

    fun getAttachments(): List<ReportAttachment>

    fun getMetadata(): InstrumentedStepMetadata

}

internal class InstrumentedStepStageImpl(
    val id: String,
    val outputPath: String,
    val title: String,
    private val delegate: InstrumentedStepDelegate,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaInstrumentedReporter,
) : AbstractStage(), InstrumentedStepStage, InstrumentedStepScope {

    private val attachments = mutableListOf<ReportAttachment>()
    private val steps = mutableListOf<InstrumentedStage>()

    override fun step(title: String, action: InstrumentedStepScope.() -> Unit) {
        val step = delegate.createStep(title, null)
        steps.add(step)
        delegate.executeStep(action)
    }

    override fun screenshot(description: String) {
        takeScreenshot(description)?.let { attachments.add(it) }
    }

    override fun getStages() = steps.toList()

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    override fun getMetadata(): InstrumentedStepMetadata {
        return InstrumentedStepMetadata(
            id = id,
            title = title,
            execution = getExecutionMetadata()
        )
    }

    fun report(action: InstrumentedStepScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    private fun takeScreenshot(description: String): ReportAttachment? {
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

    override fun toString(): String {
        return "Step: $title - $id"
    }

}