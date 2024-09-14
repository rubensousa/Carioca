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

package com.rubensousa.carioca.android.report.coroutines

import com.rubensousa.carioca.android.report.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.android.report.stage.InstrumentedScenario

/**
 * A re-usable set of stages that can be used across multiple tests.
 * Use this carefully and only when you have a stable
 * set of steps that need to execute in an consistent order
 *
 * @param title the description of this scenario
 * @param id a persistent id for tracking multiple executions of this scenario.
 * Default: same as [title]
 */
abstract class InstrumentedCoroutineScenario(
    val title: String,
    val id: String = title,
) {

    abstract suspend fun run(scope: InstrumentedCoroutineStageScope)

}

internal class InstrumentedCoroutineScenarioImpl(
    outputPath: String,
    delegateFactory: InstrumentedReportDelegateFactory<InstrumentedCoroutineStageScope>,
    id: String,
    title: String,
    private val scenario: InstrumentedCoroutineScenario,
) : InstrumentedScenario(
    outputPath = outputPath,
    id = id,
    title = title
), InstrumentedCoroutineStageScope {

    private val delegate = delegateFactory.create(this)

    override fun screenshot(description: String) {
        delegate.screenshot(description)
    }

    override suspend fun step(
        title: String,
        id: String?,
        action: suspend InstrumentedCoroutineStageScope.() -> Unit,
    ) {
        delegate.step(title, id, action)
    }

    override suspend fun scenario(scenario: InstrumentedCoroutineScenario) {
        delegate.scenario(scenario)
    }

    internal suspend fun execute() {
        scenario.run(this)
        pass()
    }

}
