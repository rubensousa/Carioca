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
 * @param outputPath the output directory for this stage's attachments
 * @param title the title of this stage
 */
abstract class InstrumentedBeforeAfterReport(
    outputPath: String,
    private val title: String,
    private val before: Boolean,
) : InstrumentedStageReport(outputPath) {

    override fun toString(): String {
        return if (before) {
            "Before: $title"
        } else {
            "After: $title"
        }
    }

    override fun getType(): String {
        return if (before) {
            "Before"
        } else {
            "After"
        }
    }

    override fun getTitle(): String = title

}
