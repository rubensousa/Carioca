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

package com.rubensousa.carioca.android.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SampleScreen(
    modifier: Modifier = Modifier,
    viewModel: SampleViewModel = hiltViewModel<SampleViewModel>(),
) {
    SampleScreen(
        modifier = modifier,
        onButtonClick = {
            viewModel.log("Button clicked")
        },
        onFabClick = {
            viewModel.log("Fab clicked")
        }
    )
}

@Composable
internal fun SampleScreen(
    modifier: Modifier,
    onButtonClick: () -> Unit,
    onFabClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onFabClick()
                }
            ) {
                Text("FAB")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ElevatedButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    onButtonClick()
                }
            ) {
                Text("Main button")
            }

        }
    }
}

@Composable
@Preview
private fun PreviewSampleScreen() {
    SampleScreen(
        modifier = Modifier.fillMaxSize(),
        onButtonClick = {},
        onFabClick = {}
    )
}


