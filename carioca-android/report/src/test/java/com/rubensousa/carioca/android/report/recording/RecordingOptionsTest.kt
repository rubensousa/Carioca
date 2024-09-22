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

package com.rubensousa.carioca.android.report.recording

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.junit4.rules.TestDescriptionRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFails

class RecordingOptionsTest {

    @get:Rule
    val testDescriptionRule = TestDescriptionRule()

    @Test
    fun `test default options`() {
        // given
        val options = RecordingOptions()

        // then
        assertThat(options.enabled).isTrue()
        assertThat(options.bitrate).isEqualTo(16_000_000L)
        assertThat(options.scale).isEqualTo(0.75f)
        assertThat(options.keepOnSuccess).isEqualTo(false)
        assertThat(options.startDelay).isEqualTo(1000L)
        assertThat(options.stopDelay).isEqualTo(1000L)
        assertThat(options.continueDelay).isEqualTo(500L)
    }

    @Test
    fun `exception is thrown if scale is out of bounds`() {
        val message = "scale must be greater than 0 and smaller or equal than 1"
        assertFails(message) {
            RecordingOptions(scale = 2f)
        }
        assertFails(message) {
            RecordingOptions(scale = 0f)
        }
        assertFails(message) {
            RecordingOptions(scale = -2f)
        }
    }

    @TestRecording
    @Test
    fun `annotation without values returns default values`() {
        // given
        val description = testDescriptionRule.getDescription()

        // when
        val options = RecordingOptions.from(description)!!

        // then
        assertThat(options).isEqualTo(RecordingOptions())
    }

    @Test
    fun `without annotation, config is null`() {
        // given
        val description = testDescriptionRule.getDescription()

        // then
        assertThat(RecordingOptions.from(description)).isNull()
    }

    @TestRecording(
        enabled = true,
        bitrate = 20_000_000,
        scale = 1.0f,
        keepOnSuccess = true,
        startDelay = 1,
        stopDelay = 2,
        continueDelay = 3
    )
    @Test
    fun `options get assigned from annotation`() {
        // given
        val description = testDescriptionRule.getDescription()

        // when
        val options = RecordingOptions.from(description)!!

        // then
        assertThat(options.enabled).isTrue()
        assertThat(options.bitrate).isEqualTo(20_000_000)
        assertThat(options.scale).isEqualTo(1.0f)
        assertThat(options.keepOnSuccess).isTrue()
        assertThat(options.startDelay).isEqualTo(1)
        assertThat(options.stopDelay).isEqualTo(2)
        assertThat(options.continueDelay).isEqualTo(3)
    }

}