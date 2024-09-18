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

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Annotates either a test class or a test method for total executions of 1 + [times].
 * This is extremely useful for checking if a test is flaky, but otherwise dangerous.
 *
 * If this annotation is used in the same class and test methods,
 * the annotation of the method takes precedence.
 *
 * Example:
 *
 * ```kotlin
 * @RepeatTest(times = 1000)
 * class SampleTest() {
 *
 *      @RepeatTest(times = 10)
 *      @Test
 *      fun testSomething() {
 *          // This test will be executed 10x or until it fails
 *      }
 *
 *      @Test
 *      fun testAnotherThing() {
 *          // This test will be executed 1000x or until it fails,
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
annotation class RepeatTest(
    /**
     * The number of times a test should be executed. Minimum is 2
     */
    val times: Int = 2,
)

class RepeatTestRule : TestRule {

    var currentExecution = 0

    override fun apply(base: Statement, description: Description): Statement {
        currentExecution = 0
        val times = getTimes(description)
        if (times < 1) {
            return base
        }
        return object : Statement() {
            override fun evaluate() {
                repeat(times + 1) {
                    base.evaluate()
                    currentExecution++
                }
            }
        }
    }

    private fun getTimes(description: Description): Int {
        var annotation = description.getAnnotation(RepeatTest::class.java)
        if (annotation == null) {
            annotation = description.testClass.getAnnotation(RepeatTest::class.java)
        }
        if (annotation == null) {
            return 0
        }
        return annotation.times
    }

}
