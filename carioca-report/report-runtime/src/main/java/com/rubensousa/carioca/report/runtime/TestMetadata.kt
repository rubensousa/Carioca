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
 * @param packageName the package name of the test class
 * @param className the class under test
 * @param methodName the name of the test method
 */
data class TestMetadata(
    val packageName: String,
    val className: String,
    val methodName: String,
) {

    /**
     * The full identifier of the test in the form of: packageName.className.methodName
     */
    val fullName: String
        get() = "$packageName.$className.$methodName"

}
