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

package com.rubensousa.carioca.android.report.suite

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.android.report.DefaultInstrumentedReporter
import com.rubensousa.carioca.android.report.DescriptionInterceptorRule
import com.rubensousa.carioca.android.report.fake.FakeReportStorageProvider
import com.rubensousa.carioca.android.report.stage.internal.InstrumentedTestBuilder
import org.junit.After
import org.junit.Rule
import org.junit.Test

class InstrumentedSuiteStageTest {

    private val reporter = DefaultInstrumentedReporter()
    private val storageProvider = FakeReportStorageProvider()
    private val testBuilder = InstrumentedTestBuilder(
        storageProvider = storageProvider
    )

    @get:Rule
    val descriptionInterceptorRule = DescriptionInterceptorRule()

    @After
    fun clean() {
        storageProvider.clean()
    }

    @Test
    fun `test report is written for ignored test`() {
        // given
        val suiteStage = InstrumentedSuiteStage(testBuilder)

        // when
        suiteStage.addReporter(reporter)
        suiteStage.testIgnored(descriptionInterceptorRule.getDescription())

        // then
        assertThat(storageProvider.lastFile).isNotNull()

    }

}
