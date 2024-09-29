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

package com.rubensousa.carioca.junit4.rules

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

class MainDispatcherRuleTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `dispatcher rule uses UnconfinedTestDispatcher by default`() = runTest {
        // given
        val mainDispatcher = Dispatchers.Main
        var dispatcherThreadId: Long?
        withContext(mainDispatcherRule.testDispatcher) {
            dispatcherThreadId = Thread.currentThread().id
        }

        // when
        var mainThreadId: Long?
        withContext(mainDispatcher) {
            mainThreadId = Thread.currentThread().id
        }

        // then
        assertThat(mainThreadId).isNotNull()
        assertThat(mainThreadId).isEqualTo(dispatcherThreadId)
    }

}

class MainDispatcherRuleDispatcherTest {

    private val dispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    @Test
    fun `dispatcher rule uses dispatcher passed in constructor`() = runTest {
        // given
        val mainDispatcher = Dispatchers.Main
        var dispatcherThreadId: Long?
        withContext(dispatcher) {
            dispatcherThreadId = Thread.currentThread().id
        }

        // when
        var mainThreadId: Long?
        withContext(mainDispatcher) {
            mainThreadId = Thread.currentThread().id
        }
        dispatcher.scheduler.advanceUntilIdle()

        // then
        assertThat(mainThreadId).isNotNull()
        assertThat(mainThreadId).isEqualTo(dispatcherThreadId)
    }

}

