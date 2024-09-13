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

package com.rubensousa.carioca.junit.report

import org.junit.runner.Description

data class TestMetadata(
    val packageName: String,
    val className: String,
    val methodName: String,
    val fullName: String,
) {

    companion object {

        fun from(description: Description): TestMetadata {
            val packageName = description.testClass.`package`?.name ?: ""
            val className = description.testClass.name
                .replace("$packageName.", "")
            val methodName = description.methodName
            return TestMetadata(
                packageName = packageName,
                className = className,
                methodName = methodName,
                fullName = "$packageName.$className.$methodName"
            )
        }

    }

}
