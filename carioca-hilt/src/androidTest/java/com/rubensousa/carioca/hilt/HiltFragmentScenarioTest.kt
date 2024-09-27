package com.rubensousa.carioca.hilt

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HiltFragmentScenarioTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var providedDependency: TestDependency

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testViewModelOfFragmentReceivesInjectedDependency() {
        // given
        val scenario = launchHiltFragment<TestFragment>()

        // when
        var testDependency: TestDependency? = null
        scenario.onFragment { fragment ->
            testDependency = fragment.getTestDependency()
        }

        // then
        assertThat(testDependency).isSameInstanceAs(providedDependency)
    }

    @Test
    fun testFragmentMovesToDifferentLifecycleStates() {
        // given
        val scenario = launchHiltFragment<TestFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )

        val states = listOf(
            Lifecycle.State.CREATED,
            Lifecycle.State.STARTED,
            Lifecycle.State.RESUMED,
        )

        states.forEach { state ->
            // when
            scenario.moveToState(state)

            // then
            assertThat(scenario.getCurrentState()).isEqualTo(state)
        }
    }

    @Test
    fun testActivityRemovesFragmentWhenItGetsDestroyed() {
        // given
        val scenario = launchHiltFragment<TestFragment>()
        val activity = scenario.withFragment {  requireActivity() }

        // when
        scenario.moveToState(Lifecycle.State.DESTROYED)

        // then
        val fragment = activity.supportFragmentManager.fragments.find { it is TestFragment }
        assertThat(fragment).isNull()
    }

    @Test
    fun testFragmentRecreation() {
        // given
        val scenario = launchHiltFragment<TestFragment>()
        val expectedState = 10
        scenario.onFragment { fragment ->
            fragment.setState(expectedState)
        }

        // when
        scenario.recreate()

        // then
        val newState = scenario.withFragment { getState() }
        assertThat(newState).isEqualTo(expectedState)
    }

    @Test
    fun testFragmentIsLaunchedWithArguments() {
        // given
        val args = Bundle()
        args.putInt("key", 0)

        // when
        val scenario = launchHiltFragment<TestFragment>(
            fragmentArgs = args
        )

        // then
        val arguments = scenario.withFragment { args }

        assertThat(arguments.getInt("key")).isEqualTo(0)
    }

    @Test
    fun testActivityThemeIsSet() {
        // given
        val theme = androidx.test.espresso.core.R.style.WhiteBackgroundTheme

        // when
        val scenario = launchHiltFragment<TestFragment>(
            themeResId = theme
        )

        // then
        val activity = scenario.withFragment { requireActivity() } as EmptyHiltActivity
        assertThat(activity.theme).isEqualTo(theme)
    }

    private fun HiltFragmentScenario<TestFragment>.getCurrentState(): Lifecycle.State? {
        var currentState: Lifecycle.State? = null
        onFragment { fragment ->
            currentState = fragment.lifecycle.currentState
        }
        return currentState
    }

    @AndroidEntryPoint
    class TestFragment : Fragment() {

        private val viewModel by viewModels<TestViewModel>()

        fun getTestDependency() = viewModel.getTestDependency()

        fun setState(state: Int) {
            viewModel.state = state
        }

        fun getState() = viewModel.state

    }

    @HiltViewModel
    class TestViewModel @Inject constructor(
        private val testDependency: TestDependency,
    ) : ViewModel() {

        var state = 0

        fun getTestDependency() = testDependency

    }

}
