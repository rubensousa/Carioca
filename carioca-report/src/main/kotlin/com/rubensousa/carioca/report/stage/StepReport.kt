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
import com.rubensousa.carioca.report.internal.IdGenerator
import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.internal.TestStorageProvider
import com.rubensousa.carioca.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.report.screenshot.ReportScreenshot
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

/**
 * Public API for a step block
 */
interface StepReportScope {

    /**
     * Takes a screenshot with the configuration set through [ScreenshotOptions].
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     */
    fun screenshot(description: String)

    /**
     * Creates a nested step inside the current step
     *
     * @param title the name of the step
     * @param action the step block that will be executed
     */
    fun step(title: String, action: StepReportScope.() -> Unit)

}

class StepReport internal constructor(
    id: String,
    val title: String,
    val outputPath: String,
    private val delegate: StepReportDelegate,
    private val screenshotOptions: ScreenshotOptions,
    private val reporter: CariocaReporter,
) : StageReport(id), StepReportScope {

    private val screenshots = mutableListOf<ReportScreenshot>()
    private val steps = mutableListOf<StepReport>()

    fun getSteps() = steps.toList()

    override fun screenshot(description: String) {
        val screenshot = takeScreenshot(description)
        if (screenshot != null) {
            screenshots.add(screenshot)
        }
    }

    override fun step(title: String, action: StepReportScope.() -> Unit) {
        val step = delegate.createStep(title, null)
        steps.add(step)
        delegate.executeStep(action)
    }

    internal fun report(action: StepReportScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    fun getScreenshots(): List<ReportScreenshot> {
        return screenshots.toList()
    }

    private fun takeScreenshot(description: String): ReportScreenshot? {
        val screenshotUri = DeviceScreenshot.take(
            storageDir = TestStorageProvider.getOutputUri(outputPath),
            options = screenshotOptions,
            filename = reporter.getScreenshotName(IdGenerator.get())
        ) ?: return null
        return ReportScreenshot(
            path = screenshotUri.path!!,
            description = description,
            extension = screenshotOptions.getFileExtension()
        )
    }

}
