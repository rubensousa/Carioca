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

package com.rubensousa.carioca.android.report.sample.test

import com.rubensousa.carioca.report.android.sample.SampleCoroutineInstrumentedReportRule
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test


class SampleCoroutineTest {

    @get:Rule
    val report = SampleCoroutineInstrumentedReportRule()

    @Test
    fun testCoroutine() = report {
        delay(500L)

        step("Some step") {
            delay(500L)
        }
    }

}
