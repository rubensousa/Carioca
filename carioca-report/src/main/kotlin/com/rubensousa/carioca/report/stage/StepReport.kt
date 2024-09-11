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

package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

interface StepReport {

    fun getSteps(): List<StepReport>

    fun getAttachments(): List<ReportAttachment>

    fun getMetadata(): StepReportMetadata

}

data class StepReportMetadata(
    val id: String,
    val title: String,
    val execution: ExecutionMetadata,
)

internal class StepReportImpl(
    val id: String,
    val outputPath: String,
    val title: String,
    private val delegate: StepReportDelegate,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaReporter,
) : StageReport(), StepReport, StepReportScope {

    private val attachments = mutableListOf<ReportAttachment>()
    private val steps = mutableListOf<StepReportImpl>()

    override fun step(title: String, action: StepReportScope.() -> Unit) {
        val step = delegate.createStep(title, null)
        steps.add(step)
        delegate.executeStep(action)
    }

    override fun screenshot(description: String) {
        takeScreenshot(description)?.let { attachments.add(it) }
    }

    override fun getSteps() = steps.toList()

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    override fun getMetadata(): StepReportMetadata {
        return StepReportMetadata(
            id = id,
            title = title,
            execution = getExecutionMetadata()
        )
    }

    fun report(action: StepReportScope.() -> Unit) {
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

