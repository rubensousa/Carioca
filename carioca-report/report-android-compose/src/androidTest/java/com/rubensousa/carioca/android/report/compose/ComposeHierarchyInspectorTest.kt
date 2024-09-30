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

package com.rubensousa.carioca.android.report.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.report.android.compose.ComposeHierarchyInspector
import org.junit.Rule
import org.junit.Test

class ComposeHierarchyInspectorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun testHierarchyIsEmptyWhenComposableIsNotFound() {
        assertThat(ComposeHierarchyInspector.dump(useUnmergedTree = true)).isEmpty()
    }

    @Test
    fun testHierarchyDumpMatchesContent() {
        // given
        composeRule.setContent {
            Box(modifier = Modifier.size(200.dp)) {
                Button(
                    onClick = {}
                ) {
                    Text("Some text")
                }
            }
        }

        // when
        composeRule.waitForIdle()
        val dump = ComposeHierarchyInspector.dump(useUnmergedTree = true)

        // then
        assertThat(dump).contains(
            "Role = 'Button'"
        )
        assertThat(dump).contains(
            "Text = '[Some text]'"
        )
    }

}
