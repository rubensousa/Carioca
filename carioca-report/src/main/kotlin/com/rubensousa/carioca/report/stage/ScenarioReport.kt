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

import com.rubensousa.carioca.report.internal.StepReportDelegate

interface ScenarioReport {

    fun getSteps(): List<StepReport>

    fun getMetadata(): ScenarioMetadata
}

data class ScenarioMetadata(
    val id: String,
    val name: String,
    val execution: ExecutionMetadata,
)

internal class ScenarioReportImpl(
    val id: String,
    val name: String,
    private val delegate: StepReportDelegate,
) : StageReport(), ScenarioReport, ScenarioReportScope {

    private val steps = mutableListOf<StepReport>()

    override fun step(title: String, id: String?, action: StepReportScope.() -> Unit) {
        val step = delegate.createStep(title, id)
        steps.add(step)
        delegate.executeStep(action)
    }

    override fun getSteps() = steps.toList()

    override fun getMetadata(): ScenarioMetadata {
        return ScenarioMetadata(
            id = id,
            name = name,
            execution = getExecutionMetadata()
        )
    }

    internal fun report(scenario: TestScenario) {
        scenario.report(this)
        pass()
    }

    override fun toString(): String {
        return "Scenario: $name - $id"
    }
}
