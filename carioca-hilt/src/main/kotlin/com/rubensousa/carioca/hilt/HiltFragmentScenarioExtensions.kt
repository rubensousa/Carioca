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

package com.rubensousa.carioca.hilt

import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle

/**
 * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
 * given arguments hosted by an empty [FragmentActivity] and waits for it to reach [initialState].
 *
 * This method cannot be called from the main thread.
 *
 * @param fragmentArgs a bundle to passed into fragment
 * @param themeResId a style resource id to be set to the host activity's theme
 * @param initialState the initial [Lifecycle.State]. Passing in
 * [DESTROYED][Lifecycle.State.DESTROYED] will result in an [IllegalArgumentException].
 */
inline fun <reified F : Fragment> launchHiltFragmentInContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = HiltFragmentScenario.DEFAULT_THEME,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
): HiltFragmentScenario<F> = HiltFragmentScenario.launchInContainer(
    F::class.java, fragmentArgs, themeResId, initialState
)

/**
 * Run [block] using [HiltFragmentScenario.onFragment], returning the result of the [block].
 *
 * If any exceptions are raised while running [block], they are rethrown.
 */
@SuppressWarnings("DocumentExceptions")
inline fun <reified F : Fragment, T : Any> HiltFragmentScenario<F>.withFragment(
    crossinline block: F.() -> T,
): T {
    lateinit var value: T
    var err: Throwable? = null
    onFragment { fragment ->
        try {
            value = block(fragment)
        } catch (t: Throwable) {
            err = t
        }
    }
    err?.let { throw it }
    return value
}