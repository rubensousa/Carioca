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

package com.rubensousa.carioca.report.core

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StageStackTest {

    private val stack = StageStack<StageReport>()

    @Test
    fun `push adds stage to the stack`() {
        // given
        val childReport = TestStageReport(1)

        // when
        stack.push(childReport)

        // then
        assertThat(stack.getAll()).isEqualTo(listOf(childReport))
    }

    @Test
    fun `clear resets the stack and its previous reports`() {
        // given
        val childReport = TestStageReport(1)
        stack.push(childReport)

        // when
        stack.clear()

        // then
        assertThat(stack.getAll()).isEmpty()
        assertThat(stack.getActive()).isEmpty()
    }

    @Test
    fun `multiple pushes will keep all entries in the stack`() {
        // given
        val reportCount = 10
        val reports = List(reportCount) {
            TestStageReport(it)
        }

        // when
        reports.forEach { stack.push(it) }

        // then
        assertThat(stack.getAll()).isEqualTo(reports)
        assertThat(stack.getActive()).isEqualTo(reports)
    }

    @Test
    fun `pop removes entries from the stack`() {
        // given
        val reportCount = 10
        val reports = List(reportCount) {
            TestStageReport(it)
        }
        reports.forEach { stack.push(it) }
        val poppedReports = mutableListOf<StageReport>()

        // when
        repeat(reportCount) {
            stack.pop()?.let { poppedReports.add(it) }
        }

        // then
        assertThat(poppedReports).isEqualTo(reports.reversed())
    }

    @Test
    fun `pop returns null if stack is empty`() {
        // when
        val report = stack.pop()

        // then
        assertThat(report).isNull()
    }

    @Test
    fun `all reports are saved even if stack is popped`() {
        // given
        val reportCount = 10
        val reports = List(reportCount) {
            TestStageReport(it)
        }
        reports.forEach { stack.push(it) }

        // when
        repeat(reportCount) {
            stack.pop()
        }

        // then
        assertThat(stack.getActive()).isEmpty()
        assertThat(stack.getAll()).isEqualTo(reports)
    }

}
