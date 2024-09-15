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

package com.rubensousa.carioca.android.rules

import com.google.common.truth.Truth.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

@RepeatTest(times = 9)
class RepeatTestRuleClassTest {

    @get:Rule
    val repeatRule = RepeatTestRule()

    @Test
    fun `test is executed with class configuration`() {
        assertThat(repeatRule.currentExecution).isEqualTo(SingletonState.iteration)
        SingletonState.iteration++
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun before() {
            SingletonState.iteration = 0
        }

        @AfterClass
        @JvmStatic
        fun after() {
            SingletonState.assertIterations(10)
        }
    }

}

@RepeatTest(times = 9)
class RepeatTestRuleMethodTest {

    @get:Rule
    val repeatRule = RepeatTestRule()

    @RepeatTest(times = 2)
    @Test
    fun `test is executed with its own configuration`() {
        SingletonState.iteration++
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun before() {
            SingletonState.iteration = 0
        }

        @AfterClass
        @JvmStatic
        fun after() {
            SingletonState.assertIterations(3)
        }
    }

}

class RepeatTestRuleEmptyTest {

    @get:Rule
    val repeatRule = RepeatTestRule()

    @Test
    fun `test is executed once`() {
        SingletonState.iteration++
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun before() {
            SingletonState.iteration = 0
        }

        @AfterClass
        @JvmStatic
        fun after() {
            SingletonState.assertIterations(1)
        }
    }

}