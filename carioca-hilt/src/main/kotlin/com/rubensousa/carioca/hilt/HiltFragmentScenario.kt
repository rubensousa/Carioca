/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import androidx.fragment.app.testing.FragmentScenario.Companion.launch
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import java.io.Closeable


/**
 * FragmentScenario provides API to start and drive a Fragment's lifecycle state for testing. It
 * works with arbitrary fragments and works consistently across different versions of the Android
 * framework.
 *
 * FragmentScenario only supports [androidx.fragment.app.Fragment][Fragment]. If you are using
 * a deprecated fragment class such as `android.support.v4.app.Fragment` or
 * [android.app.Fragment], please update your code to
 * [androidx.fragment.app.Fragment][Fragment].
 *
 * If your testing Fragment has a dependency to specific theme such as `Theme.AppCompat`,
 * use the theme ID parameter in [launch] method.
 *
 * @param F The Fragment class being tested
 *
 * @see ActivityScenario a scenario API for Activity
 */
class HiltFragmentScenario<F : Fragment> private constructor(
    @Suppress("MemberVisibilityCanBePrivate") /* synthetic access */
    internal val fragmentClass: Class<F>,
    private val activityScenario: ActivityScenario<EmptyHiltActivity>,
) : Closeable {

    /**
     * Moves Fragment state to a new state.
     *
     *  If a new state and current state are the same, this method does nothing. It accepts
     * all [Lifecycle.State]s. [DESTROYED][Lifecycle.State.DESTROYED] is a terminal state.
     * You cannot move to any other state after the Fragment reaches that state.
     *
     * This method cannot be called from the main thread.
     */
    fun moveToState(newState: Lifecycle.State): HiltFragmentScenario<F> {
        if (newState == Lifecycle.State.DESTROYED) {
            activityScenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager
                    .findFragmentByTag(FRAGMENT_TAG)
                // Null means the fragment has been destroyed already.
                if (fragment != null) {
                    activity.supportFragmentManager.commitNow {
                        remove(fragment)
                    }
                }
            }
        } else {
            activityScenario.onActivity { activity ->
                val fragment = requireNotNull(
                    activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                ) {
                    "The fragment has been removed from the FragmentManager already."
                }
                activity.supportFragmentManager.commitNow {
                    setMaxLifecycle(fragment, newState)
                }
            }
        }
        return this
    }

    /**
     * Recreates the host Activity.
     *
     * After this method call, it is ensured that the Fragment state goes back to the same state
     * as its previous state.
     *
     * This method cannot be called from the main thread.
     */
    fun recreate(): HiltFragmentScenario<F> {
        activityScenario.recreate()
        return this
    }

    /**
     * FragmentAction interface should be implemented by any class whose instances are intended to
     * be executed by the main thread. A Fragment that is instrumented by the FragmentScenario is
     * passed to [FragmentAction.perform] method.
     *
     * You should never keep the Fragment reference as it will lead to unpredictable behaviour.
     * It should only be accessed in [FragmentAction.perform] scope.
     */
    fun interface FragmentAction<F : Fragment> {
        /**
         * This method is invoked on the main thread with the reference to the Fragment.
         *
         * @param fragment a Fragment instrumented by the FragmentScenario.
         */
        fun perform(fragment: F)
    }

    /**
     * Runs a given [action] on the current Activity's main thread.
     *
     * Note that you should never keep Fragment reference passed into your [action]
     * because it can be recreated at anytime during state transitions.
     *
     * Throwing an exception from [action] makes the host Activity crash. You can
     * inspect the exception in logcat outputs.
     *
     * This method cannot be called from the main thread.
     */
    fun onFragment(action: FragmentAction<F>): HiltFragmentScenario<F> {
        activityScenario.onActivity { activity ->
            val fragment = requireNotNull(
                activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            ) {
                "The fragment has been removed from the FragmentManager already."
            }
            check(fragmentClass.isInstance(fragment))
            action.perform(requireNotNull(fragmentClass.cast(fragment)))
        }
        return this
    }

    /**
     * Finishes the managed fragments and cleans up device's state. This method blocks execution
     * until the host activity becomes [Lifecycle.State.DESTROYED].
     */
    override fun close() {
        activityScenario.close()
    }

    companion object {

        private const val FRAGMENT_TAG = "HiltFragmentScenario_Fragment_Tag"


        /**
         * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
         * given arguments hosted by an empty [FragmentActivity] themed by [themeResId],
         * and waits for it to reach [initialState].
         *
         * This method cannot be called from the main thread.
         *
         * @param fragmentClass a fragment class to instantiate
         * @param fragmentArgs a bundle to passed into fragment
         * @param themeResId a style resource id to be set to the host activity's theme
         * @param initialState The initial [Lifecycle.State]. Passing in
         * [DESTROYED][Lifecycle.State.DESTROYED] will result in an [IllegalArgumentException].
         */
        @JvmOverloads
        @JvmStatic
        fun <F : Fragment> launchInContainer(
            fragmentClass: Class<F>,
            fragmentArgs: Bundle? = null,
            @StyleRes themeResId: Int = EmptyHiltActivity.DEFAULT_THEME,
            initialState: Lifecycle.State = Lifecycle.State.RESUMED,
        ): HiltFragmentScenario<F> {
            return launchInContainer(
                fragmentClass = fragmentClass,
                activityClass = EmptyHiltActivity::class.java,
                fragmentArgs = fragmentArgs,
                themeResId = themeResId,
                initialState = initialState
            )
        }

        /**
         * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
         * given arguments hosted by an empty [EmptyHiltActivity] themed by [themeResId],
         * and waits for it to reach [initialState].
         *
         * This method cannot be called from the main thread.
         *
         * @param fragmentClass a fragment class to instantiate
         * @param activityClass the activity that will host this fragment
         * @param fragmentArgs a bundle to passed into fragment
         * @param themeResId a style resource id to be set to the host activity's theme
         * @param initialState The initial [Lifecycle.State]. Passing in
         * [DESTROYED][Lifecycle.State.DESTROYED] will result in an [IllegalArgumentException].
         */
        @JvmOverloads
        @JvmStatic
        fun <F : Fragment, A : EmptyHiltActivity> launchInContainer(
            fragmentClass: Class<F>,
            activityClass: Class<A>,
            fragmentArgs: Bundle? = null,
            @StyleRes themeResId: Int = EmptyHiltActivity.DEFAULT_THEME,
            initialState: Lifecycle.State = Lifecycle.State.RESUMED,
        ): HiltFragmentScenario<F> {
            require(initialState != Lifecycle.State.DESTROYED) {
                "Cannot set initial Lifecycle state to $initialState for FragmentScenario"
            }
            val componentName = ComponentName(
                ApplicationProvider.getApplicationContext(),
                activityClass
            )
            val startActivityIntent = Intent.makeMainActivity(componentName)
                .putExtra(EmptyHiltActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)
            val scenario = HiltFragmentScenario(
                fragmentClass,
                ActivityScenario.launch(
                    startActivityIntent
                )
            )
            scenario.activityScenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager.fragmentFactory
                    .instantiate(requireNotNull(fragmentClass.classLoader), fragmentClass.name)
                fragment.arguments = fragmentArgs
                activity.supportFragmentManager.commitNow {
                    add(android.R.id.content, fragment, FRAGMENT_TAG)
                    setMaxLifecycle(fragment, initialState)
                }
            }
            return scenario
        }

    }
}
