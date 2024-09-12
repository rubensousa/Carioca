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

import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepDelegate
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepScope
import com.rubensousa.carioca.stage.AbstractCariocaStage
import com.rubensousa.carioca.stage.CariocaStage

interface InstrumentedScenarioStage : CariocaStage {

    fun getAttachments(): List<ReportAttachment>

    fun getMetadata(): InstrumentedScenarioMetadata

}

internal class InstrumentedScenarioStageImpl(
    private val id: String,
    private val scenario: InstrumentedTestScenario,
    private val delegate: InstrumentedStepDelegate,
) : AbstractCariocaStage(), InstrumentedScenarioStage, InstrumentedScenarioScope {

    private val stages = mutableListOf<CariocaStage>()
    private val attachments = mutableListOf<ReportAttachment>()

    override fun step(title: String, id: String?, action: InstrumentedStepScope.() -> Unit) {
        val step = delegate.create(title, id)
        stages.add(step)
        delegate.execute(step, action)
    }

    override fun screenshot(description: String) {
        delegate.takeScreenshot(description)?.let { attachments.add(it) }
    }

    override fun getStages() = stages.toList()

    override fun getMetadata(): InstrumentedScenarioMetadata {
        return InstrumentedScenarioMetadata(
            id = id,
            name = scenario.name,
        )
    }

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    fun execute() {
        scenario.run(this)
        pass()
    }

    override fun toString(): String {
        return "Scenario: ${scenario.name} - $id"
    }
}
