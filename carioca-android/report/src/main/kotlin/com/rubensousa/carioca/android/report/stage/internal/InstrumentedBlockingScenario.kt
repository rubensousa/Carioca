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

package com.rubensousa.carioca.android.report.stage.internal

import com.rubensousa.carioca.android.report.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.android.report.stage.InstrumentedScenario
import com.rubensousa.carioca.android.report.stage.InstrumentedScenarioReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStageScope

internal class InstrumentedBlockingScenario(
    delegateFactory: InstrumentedReportDelegateFactory<InstrumentedStageScope>,
    outputPath: String,
    id: String,
    title: String,
    private val scenario: InstrumentedScenario,
) : InstrumentedScenarioReport(
    outputPath = outputPath,
    id = id,
    title = title
), InstrumentedStageScope {

    private val delegate = delegateFactory.create(this)

    override fun screenshot(description: String) {
        delegate.screenshot(description)
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

    internal fun execute() {
        scenario.run(this)
        pass()
    }

}
