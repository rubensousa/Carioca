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
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement

@RetryTest(times = 9)
class RetryTestRuleClassTest {

    @get:Rule
    val retryRule = RetryTestRule()

    @Test
    fun `test is executed with class configuration`() {
        assertThat(retryRule.currentExecution).isEqualTo(SingletonState.iteration)
        SingletonState.iteration++
        if (retryRule.currentExecution < 9) {
            throw IllegalArgumentException("Failed")
        }
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

@RetryTest(times = 9)
class RetryTestRuleMethodTest {

    @get:Rule
    val retryRule = RetryTestRule()

    @RetryTest(times = 2)
    @Test
    fun `test is executed with its own configuration`() {
        SingletonState.iteration++
        if (SingletonState.iteration != 3) {
            throw IllegalArgumentException("Failed")
        }
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

class RetryTestRuleFailureTest {

    @Test(expected = IllegalStateException::class)
    fun `test throws exception after iterations`() {
        // given
        val description = Description.createTestDescription(
            this::class.java, "", RetryTest(retryTimes)
        )

        // when
        val rule = RetryTestRule()
        rule.apply(
            object : Statement() {
                override fun evaluate() {
                    SingletonState.iteration++
                    throw IllegalStateException()
                }
            }, description
        ).evaluate()
    }

    companion object {

        val retryTimes = 5

        @BeforeClass
        @JvmStatic
        fun before() {
            SingletonState.iteration = 0
        }

        @AfterClass
        @JvmStatic
        fun after() {
            SingletonState.assertIterations(retryTimes + 1)
        }
    }

}

@RetryTest(times = 10)
class RetryTestRuleSuccessfulTest {

    @get:Rule
    val retryRule = RetryTestRule()

    @Test
    fun `test is executed once because it passes`() {
        assertThat(retryRule.currentExecution).isEqualTo(0)
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

class RetryTestRuleEmptyTest {

    @get:Rule
    val retryRule = RetryTestRule()

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