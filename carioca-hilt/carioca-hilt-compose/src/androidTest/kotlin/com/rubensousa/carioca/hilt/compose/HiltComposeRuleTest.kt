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

package com.rubensousa.carioca.hilt.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.common.truth.Truth.assertThat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
class HiltComposeRuleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createHiltComposeRule()

    @Inject
    lateinit var injectedDependency: Dependency

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testViewModelIsCreatedWithCorrectDependencyInstance() {
        // given
        var dependencyFromViewModel: Dependency? = null
        composeTestRule.setContent {
            val viewModel = viewModel<TestViewModel>()
            dependencyFromViewModel = viewModel.dependency
        }

        // when
        composeTestRule.waitForIdle()

        // then
        assertThat(dependencyFromViewModel).isSameInstanceAs(injectedDependency)

    }

    @HiltViewModel
    class TestViewModel @Inject constructor(
        val dependency: Dependency,
    ) : ViewModel()

    class Dependency

    @InstallIn(SingletonComponent::class)
    @Module
    class TestModule {

        @Provides
        @Singleton
        fun provideDependency(): Dependency {
            return Dependency()
        }
    }

}
