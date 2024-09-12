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

import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepDelegate
import com.rubensousa.carioca.android.report.storage.IdGenerator
import com.rubensousa.carioca.stage.StageStack

internal class InstrumentedScenarioDelegate(
    private val stack: StageStack,
    private val stepDelegate: InstrumentedStepDelegate,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
) {

    fun create(scenario: InstrumentedTestScenario): InstrumentedScenarioStageImpl {
        val newScenario = InstrumentedScenarioStageImpl(
            id = getScenarioId(scenario),
            delegate = stepDelegate,
            scenario = scenario
        )
        stack.push(newScenario)
        return newScenario
    }

    fun execute(scenario: InstrumentedScenarioStageImpl) {
        interceptors.intercept { onStageStarted(scenario) }
        scenario.execute()
        stack.pop()
        interceptors.intercept { onStagePassed(scenario) }
    }

    private fun getScenarioId(scenario: InstrumentedTestScenario): String {
        return scenario.id ?: IdGenerator.get()
    }

}
