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

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Annotates either a test class or a test method with a retry request of [times].
 *
 * If this annotation is used in the same class and test methods,
 * the annotation of the method takes precedence
 *
 * Example:
 *
 * ```kotlin
 * @RetryTest(times = 5)
 * class SampleTest() {
 *
 *      @get:Rule
 *      val retryRule = RetryTestRule()
 *
 *      @RetryTest(times = 2)
 *      @Test
 *      fun testSomething() {
 *          // This test will be retried 2x if it fails
 *      }
 *
 *      @Test
 *      fun testAnotherThing() {
 *          // This test will be retried 5x if it fails,
 *          // because of the class configuration
 *      }
 * }
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS
)
annotation class RetryTest(
    /**
     * The number of times a test should be retried. Minimum should be one
     */
    val times: Int = 1,
)

class RetryTestRule : TestRule {

    var currentExecution = 0

    override fun apply(base: Statement, description: Description): Statement {
        currentExecution = 0
        val times = getTimes(description)
        if (times <= 0) {
            return base
        }
        return object : Statement() {
            override fun evaluate() {
                var lastError: Throwable? = null
                repeat(times + 1) {
                    try {
                        base.evaluate()
                        // Clear the error, since the test now passed
                        lastError = null
                        return@repeat
                    } catch (error: Throwable) {
                        lastError = error
                    }
                    currentExecution++
                }
                lastError?.let {
                    throw it
                }
            }
        }
    }

    private fun getTimes(description: Description): Int {
        var annotation = description.getAnnotation(RetryTest::class.java)
        if (annotation == null) {
            annotation = description.testClass.getAnnotation(RetryTest::class.java)
        }
        if (annotation == null) {
            return 0
        }
        return annotation.times
    }

}
