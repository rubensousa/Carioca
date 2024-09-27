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

package com.rubensousa.carioca.report.android

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.junit4.rules.TestDescriptionRule
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.stage.internal.InstrumentedBlockingTestBuilder
import com.rubensousa.carioca.report.android.storage.TestStorageProvider
import com.rubensousa.carioca.report.android.suite.InstrumentedSuiteStage
import com.rubensousa.carioca.report.android.suite.SuiteReportRegistry
import com.rubensousa.carioca.report.runtime.ReportStatus
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InstrumentedReportRuleTest {

    @get:Rule
    val testDescriptionRule = TestDescriptionRule()

    private val reportRule = InstrumentedReportRule(
        recordingOptions = RecordingOptions(enabled = false)
    )

    @Before
    fun setup() {
        // Cleanup the suite before each test to ensure tests
        // do not add to the singleton state
        SuiteReportRegistry.setSuiteStage(
            InstrumentedSuiteStage(
                testBuilder = InstrumentedBlockingTestBuilder(TestStorageProvider)
            )
        )
    }

    @Test
    fun testMetadataIsCreatedCorrectly() {
        // given
        val description = testDescriptionRule.getDescription()

        // when
        reportRule.start(description)

        // then
        val test = SuiteReportRegistry.getSuiteStage().getTests().first()
        with(test.metadata) {
            assertThat(packageName).isEqualTo("com.rubensousa.carioca.report.android")
            assertThat(methodName).isEqualTo("testMetadataIsCreatedCorrectly")
            assertThat(className).isEqualTo("InstrumentedReportRuleTest")
        }
    }

    @Test
    fun testPreviousTestIsReusedIfDescriptionIsTheSame() {
        // given
        val description = testDescriptionRule.getDescription()
        reportRule.start(description)
        reportRule.test {
            step("Some stage")
        }

        // when
        val failure = IllegalStateException("Whoops")
        reportRule.fail(failure, description)
        reportRule.start(description)

        // then
        val startedTests = SuiteReportRegistry.getSuiteStage().getTests()
        assertThat(startedTests).hasSize(1)
        val execution = reportRule.getCurrentReport().getExecutionMetadata()
        assertThat(execution.failureCause).isNull()
        assertThat(execution.status).isEqualTo(ReportStatus.RUNNING)
    }

    @Test
    fun testBeforeStageIsPassedToReport() {
        // given
        val description = testDescriptionRule.getDescription()
        reportRule.start(description)
        val beforeTitle = "Something"
        val beforeStep = "StepBefore"

        // when
        reportRule.before(beforeTitle) {
            step(beforeStep)
        }

        // then
        val report = reportRule.getCurrentReport()
        val beforeStage = report.getStagesBefore().first()
        assertThat(beforeStage.getTitle()).isEqualTo(beforeTitle)
        assertThat(beforeStage.getTestStages().first().getTitle()).isEqualTo(beforeStep)
    }

    @Test
    fun testAfterStageIsPassedToReport() {
        // given
        val description = testDescriptionRule.getDescription()
        reportRule.start(description)
        val afterTitle = "Something"
        val afterStep = "StepAfter"

        // when
        reportRule.after(afterTitle) {
            step(afterStep)
        }

        // then
        val report = reportRule.getCurrentReport()
        val afterStage = report.getStagesAfter().first()
        assertThat(afterStage.getTitle()).isEqualTo(afterTitle)
        assertThat(afterStage.getTestStages().first().getTitle()).isEqualTo(afterStep)
    }

}
