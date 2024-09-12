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

import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.stage.AbstractCariocaStage
import com.rubensousa.carioca.stage.CariocaStage

interface InstrumentedStepStage : CariocaStage {

    fun getAttachments(): List<ReportAttachment>

    fun getMetadata(): InstrumentedStepMetadata

}

internal class InstrumentedStepStageImpl(
    val id: String,
    val title: String,
    private val delegate: InstrumentedStepDelegate,
) : AbstractCariocaStage(), InstrumentedStepStage, InstrumentedStepScope {

    private val attachments = mutableListOf<ReportAttachment>()
    private val stages = mutableListOf<CariocaStage>()

    fun execute(action: InstrumentedStepScope.() -> Unit) {
        action.invoke(this)
        pass()
    }

    override fun step(title: String, action: InstrumentedStepScope.() -> Unit) {
        val step = delegate.create(title, null)
        stages.add(step)
        delegate.execute(step, action)
    }

    override fun screenshot(description: String) {
        delegate.takeScreenshot(description)?.let { attachments.add(it) }
    }

    override fun getStages() = stages.toList()

    override fun getAttachments(): List<ReportAttachment> = attachments.toList()

    override fun getMetadata(): InstrumentedStepMetadata {
        return InstrumentedStepMetadata(
            id = id,
            title = title,
        )
    }

    override fun toString(): String {
        return "Step: $title - $id"
    }

}
