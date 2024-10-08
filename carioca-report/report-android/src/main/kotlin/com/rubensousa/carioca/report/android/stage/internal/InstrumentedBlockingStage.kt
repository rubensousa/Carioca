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

package com.rubensousa.carioca.report.android.stage.internal

import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.report.android.stage.InstrumentedScenario
import com.rubensousa.carioca.report.android.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope
import com.rubensousa.carioca.report.android.stage.InstrumentedStageType
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider

internal class InstrumentedBlockingStage(
    type: InstrumentedStageType,
    outputPath: String,
    delegateFactory: InstrumentedReportDelegateFactory<InstrumentedStageScope>,
    storageProvider: ReportStorageProvider,
    private val id: String,
    private val title: String,
) : InstrumentedStageReport(
    type = type,
    outputPath = outputPath,
    storageProvider = storageProvider
), InstrumentedStageScope {

    private val delegate = delegateFactory.create(this)

    override fun getId(): String = id

    override fun getTitle(): String = title

    override fun screenshot(description: String, options: ScreenshotOptions?) {
        delegate.screenshot(description, options)
    }

    override fun step(
        title: String,
        id: String?,
        action: InstrumentedStageScope.() -> Unit,
    ) {
        delegate.step(title, id, action)
    }

    override fun scenario(scenario: InstrumentedScenario) {
        delegate.scenario(scenario)
    }

    override fun param(key: String, value: String) {
        delegate.param(key, value)
    }

    internal fun execute(action: InstrumentedStageScope.() -> Unit) {
        action(this)
        pass()
    }

    internal fun executeScenario(scenario: InstrumentedScenario) {
        scenario.run(this)
        pass()
    }

}
