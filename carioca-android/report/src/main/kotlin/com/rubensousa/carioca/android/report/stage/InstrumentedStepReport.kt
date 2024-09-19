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

/**
 * @param outputPath the output directory for this step's attachments
 * @param id unique id that identifies a step. By default, unique per execution
 * @param title the title of the step
 */
abstract class InstrumentedStepReport(
    outputPath: String,
    private val id: String,
    private val title: String,
) : InstrumentedStageReport(
    reportDirPath = outputPath
) {

    override fun toString(): String {
        return "Step: $title - $id"
    }

    override fun getType(): String = "Step"

    override fun getTitle(): String = title

    override fun getId(): String = id

}
