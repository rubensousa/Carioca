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
import com.rubensousa.carioca.junit4.rules.TestDescriptionRule
import com.rubensousa.carioca.report.android.DefaultInstrumentedReporter
import com.rubensousa.carioca.report.android.TemporaryStorageRule
import com.rubensousa.carioca.report.android.fake.FakeReportStorageProvider
import com.rubensousa.carioca.report.android.stage.internal.InstrumentedBlockingTestBuilder
import org.junit.Rule
import org.junit.Test

class InstrumentedSuiteStageTest {

    @get:Rule
    val temporaryStorageRule = TemporaryStorageRule()

    @get:Rule
    val testDescriptionRule = TestDescriptionRule()

    private val reporter = DefaultInstrumentedReporter()
    private val storageProvider = FakeReportStorageProvider()
    private val testBuilder = InstrumentedBlockingTestBuilder(
        storageProvider = storageProvider
    )

    @Test
    fun `test report is written for ignored test`() {
        // given
        val suiteStage = InstrumentedSuiteStage(testBuilder)

        // when
        suiteStage.registerReporter(reporter)
        suiteStage.testIgnored(testDescriptionRule.getDescription())

        // then
        assertThat(storageProvider.lastFile).isNotNull()

    }

}
