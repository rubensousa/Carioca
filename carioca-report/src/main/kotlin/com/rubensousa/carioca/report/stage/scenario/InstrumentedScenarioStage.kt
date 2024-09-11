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

package com.rubensousa.carioca.report.stage.scenario

import com.rubensousa.carioca.report.stage.AbstractStage
import com.rubensousa.carioca.report.stage.InstrumentedStage
import com.rubensousa.carioca.report.stage.step.InstrumentedStepDelegate
import com.rubensousa.carioca.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.report.stage.step.InstrumentedStepStage

interface InstrumentedScenarioStage : InstrumentedStage {

    fun getSteps(): List<InstrumentedStepStage>

    fun getMetadata(): InstrumentedScenarioMetadata
}

internal class InstrumentedScenarioStageImpl(
    val id: String,
    val name: String,
    private val delegate: InstrumentedStepDelegate,
) : AbstractStage(), InstrumentedScenarioStage, InstrumentedScenarioScope {

    private val steps = mutableListOf<InstrumentedStepStage>()

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = delegate.createStep(title, id)
        steps.add(step)
        delegate.executeStep(action)
    }

    override fun getSteps() = steps.toList()

    override fun getMetadata(): InstrumentedScenarioMetadata {
        return InstrumentedScenarioMetadata(
            id = id,
            name = name,
        )
    }

    fun report(scenario: InstrumentedTestScenario) {
        scenario.run(this)
        pass()
    }

    override fun toString(): String {
        return "Scenario: $name - $id"
    }
}
