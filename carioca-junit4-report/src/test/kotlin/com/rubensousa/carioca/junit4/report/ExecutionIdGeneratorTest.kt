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

package com.rubensousa.carioca.junit4.report

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExecutionIdGeneratorTest {
    
    @Test
    fun `ids are unique across many generations`() {
        // given
        val iterations = 1000
        val generatedIds = mutableSetOf<String>()

        // when
        repeat(iterations) {
            generatedIds.add(ExecutionIdGenerator.get())
        }

        // then
        assertThat(generatedIds).hasSize(iterations)
    }

}
