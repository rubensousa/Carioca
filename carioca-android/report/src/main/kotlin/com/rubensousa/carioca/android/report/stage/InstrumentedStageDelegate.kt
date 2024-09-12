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

package com.rubensousa.carioca.android.report.stage

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.screenshot.DeviceScreenshot
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenario
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenarioMetadata
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStep
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepMetadata
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.android.report.storage.FileIdGenerator
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import com.rubensousa.carioca.stage.ExecutionIdGenerator
import com.rubensousa.carioca.stage.StageStack

internal class InstrumentedStageDelegate(
    private val stack: StageStack,
    private val reporter: CariocaInstrumentedReporter,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
) {

    fun createStep(title: String, id: String?): InstrumentedStep {
        return InstrumentedStep(
            metadata = InstrumentedStepMetadata(
                id = getStepId(id),
                title = title,
            ),
            stageDelegate = this
        )
    }

    fun executeStep(
        step: InstrumentedStep,
        action: InstrumentedStepScope.() -> Unit,
    ) {
        stack.push(step)
        interceptors.intercept { onStageStarted(step) }
        step.execute(action)
        stack.pop()
        interceptors.intercept { onStagePassed(step) }
    }

    fun createScenario(scenario: InstrumentedTestScenario): InstrumentedScenario {
        return InstrumentedScenario(
            metadata = InstrumentedScenarioMetadata(
                id = getScenarioId(scenario),
                title = scenario.title
            ),
            scenario = scenario,
            stageDelegate = this
        )
    }

    fun executeScenario(scenario: InstrumentedScenario) {
        stack.push(scenario)
        interceptors.intercept { onStageStarted(scenario) }
        scenario.execute()
        stack.pop()
        interceptors.intercept { onStagePassed(scenario) }
    }

    fun takeScreenshot(description: String): StageAttachment? {
        val screenshotUri = DeviceScreenshot.take(
            storageDir = TestStorageProvider.getOutputUri(outputPath),
            options = screenshotOptions,
            filename = reporter.getScreenshotName(FileIdGenerator.get())
        ) ?: return null
        return StageAttachment(
            path = screenshotUri.path!!,
            description = description,
            mimeType = getScreenshotMimeType(),
        )
    }

    private fun getScenarioId(scenario: InstrumentedTestScenario): String {
        return scenario.id ?: ExecutionIdGenerator.get()
    }

    private fun getStepId(id: String?): String {
        return id ?: ExecutionIdGenerator.get()
    }

    private fun getScreenshotMimeType(): String {
        return when (screenshotOptions.getFileExtension()) {
            ".png" -> "image/png"
            ".webp" -> "image/webp"
            else -> "image/jpg"
        }
    }

}
