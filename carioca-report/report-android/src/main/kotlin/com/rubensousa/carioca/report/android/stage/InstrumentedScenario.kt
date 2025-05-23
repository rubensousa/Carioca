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

package com.rubensousa.carioca.report.android.stage

/**
 * A re-usable set of stages that can be used across multiple tests.
 * Use this carefully and only when you have a stable
 * set of steps that need to execute in an consistent order
 *
 * @param title the description of this scenario
 * @param id a persistent id for tracking multiple executions of this scenario.
 * Default: same as [title]
 */
abstract class InstrumentedScenario(
    title: String? = null,
    id: String? = null
) {

    private var requestedTitle: String? = title
    private var requestedId: String? = id

    open fun getTitle(): String {
        return requestedTitle ?: this::class.simpleName.orEmpty()
    }

    open fun getId(): String = requestedId ?: getTitle()

    abstract fun run(scope: InstrumentedStageScope)

}
