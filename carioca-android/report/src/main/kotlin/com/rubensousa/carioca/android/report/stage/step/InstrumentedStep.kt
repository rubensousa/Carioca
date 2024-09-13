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

import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStepScope
import com.rubensousa.carioca.android.report.stage.InstrumentedStageDelegate
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedTestScenario

class InstrumentedStep internal constructor(
    outputPath: String,
    private val metadata: InstrumentedStepMetadata,
    private val stageDelegate: InstrumentedStageDelegate,
) : InstrumentedStageReport(outputPath), InstrumentedStepScope, InstrumentedCoroutineStepScope {

    override fun step(
        title: String,
        id: String?,
        action: InstrumentedStepScope.() -> Unit,
    ) {
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

    override fun scenario(scenario: InstrumentedTestScenario) {
        val newScenario = stageDelegate.createScenario(scenario)
        addStage(newScenario)
        stageDelegate.executeScenario(newScenario)
    }

    override suspend fun scenario(scenario: InstrumentedCoroutineScenario) {
        val newScenario = stageDelegate.createCoroutineScenario(scenario)
        addStage(newScenario)
        stageDelegate.executeScenario(newScenario)
    }

    override fun screenshot(description: String) {
        stageDelegate.takeScreenshot(description)?.let { attach(it) }
    }

    fun getMetadata(): InstrumentedStepMetadata {
        return metadata
    }

    internal fun execute(action: InstrumentedStepScope.() -> Unit) {
        action(this)
        pass()
    }

    internal suspend fun execute(action: suspend InstrumentedCoroutineStepScope.() -> Unit) {
        action(this)
        pass()
    }

    override fun toString(): String {
        return "Step: ${metadata.title} - ${metadata.id}"
    }

}
