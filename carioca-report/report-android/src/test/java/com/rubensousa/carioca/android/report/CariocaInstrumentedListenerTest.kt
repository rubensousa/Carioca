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

package com.rubensousa.carioca.android.report

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.junit4.rules.TestDescriptionRule
import com.rubensousa.carioca.report.android.fake.FakeSuiteStage
import com.rubensousa.carioca.report.android.suite.SuiteReportRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CariocaInstrumentedListenerTest {

    @get:Rule
    val testDescriptionRule = TestDescriptionRule()

    private val suiteStage = FakeSuiteStage()
    private lateinit var listener: CariocaInstrumentedListener

    @Before
    fun setup() {
        SuiteReportRegistry.setSuiteStage(suiteStage)
        listener = CariocaInstrumentedListener()
    }

    @Test
    fun `ignored test is added to the suite`() {
        // given
        val description = testDescriptionRule.getDescription()

        // when
        listener.testIgnored(description)

        // then
        assertThat(suiteStage.ignoredDescriptions).isEqualTo(listOf(description))
    }

}
