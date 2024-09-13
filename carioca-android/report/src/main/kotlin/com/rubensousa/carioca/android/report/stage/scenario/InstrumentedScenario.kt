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

package com.rubensousa.carioca.android.report.stage.scenario

import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenarioScope
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStepScope
import com.rubensousa.carioca.android.report.stage.InstrumentedStageDelegate
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope

class InstrumentedScenario internal constructor(
    outputPath: String,
    private val metadata: InstrumentedScenarioMetadata,
    private val scenario: InstrumentedTestScenario?,
    private val coroutineScenario: InstrumentedCoroutineScenario?,
    private val stageDelegate: InstrumentedStageDelegate,
) : InstrumentedStageReport(outputPath), InstrumentedScenarioScope,
    InstrumentedCoroutineScenarioScope {

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = stageDelegate.createStep(title, id)
        addStage(step)
        stageDelegate.executeStep(step, action)
    }

    override suspend fun step(
        title: String,
        id: String?,
        action: suspend InstrumentedCoroutineStepScope.() -> Unit,
    ) {
        val step = stageDelegate.createStep(title, id)
        addStage(step)
        stageDelegate.executeStep(step, action)
    }

    override fun screenshot(description: String) {
        stageDelegate.takeScreenshot(description)?.let { attach(it) }
    }

    fun getMetadata(): InstrumentedScenarioMetadata = metadata

    internal fun execute() {
        scenario?.run(this)
        pass()
    }

    internal suspend fun executeAwait() {
        coroutineScenario?.run(this)
        pass()
    }

    override fun toString(): String {
        return "Scenario: ${metadata.title} - ${metadata.id}"
    }

}
