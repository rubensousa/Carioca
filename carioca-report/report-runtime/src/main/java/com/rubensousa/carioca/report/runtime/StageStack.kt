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

package com.rubensousa.carioca.report.runtime

/**
 * Holds the current state of the stage execution.
 *
 * The top of the stack points to the current active stage
 */
class StageStack<T> {

    private val stack = ArrayDeque<T>()
    private val stages = mutableListOf<T>()

    fun push(stage: T) {
        stack.addLast(stage)
        stages.add(stage)
    }

    fun pop(): T? {
        return stack.removeLastOrNull()
    }

    fun clear() {
        stack.clear()
        stages.clear()
    }

    fun getAll() = stages.toList()

    internal fun getActive() = stack.toList()

}
