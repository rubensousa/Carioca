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

package com.rubensousa.carioca.android.report.stage

import com.rubensousa.carioca.stage.AbstractCariocaStage
import com.rubensousa.carioca.stage.CariocaStage

abstract class InstrumentedStage<T> : AbstractCariocaStage() {

    private val attachments = arrayListOf<StageAttachment>()
    private val properties = mutableMapOf<String, Any>()
    private val stages = mutableListOf<CariocaStage>()

    abstract fun getMetadata(): T

    fun getAttachments(): List<StageAttachment> = attachments.toList()

    fun attach(attachment: StageAttachment) {
        attachments.add(attachment)
    }

    fun addProperty(key: String, value: Any) {
        properties[key] = value
    }

    fun getProperties(): Map<String, Any> = properties.toMap()

    override fun getStages(): List<CariocaStage> = stages.toList()

    protected fun addStage(stage: CariocaStage) {
        stages.add(stage)
    }

    protected fun resetState() {
        stages.clear()
        properties.clear()
        attachments.clear()
    }

}
