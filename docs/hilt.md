# Testing with Hilt

The libraries below assume you have knowledge about how to write instrumented tests with Hilt.

The [official guide](https://developer.android.com/training/dependency-injection/hilt-testing) 
is a good starting point.

## Setup

Add the following dependency:

```groovy
debugImplementation "com.rubensousa.carioca:hilt-manifest:{{ hilt.version }}"
```

Then pick one or both of these:

```groovy
androidTestImplementation "com.rubensousa.carioca:hilt-fragment:{{ hilt.version }}"
androidTestImplementation "com.rubensousa.carioca:hilt-compose:{{ hilt.version }}"
```

If you don't have a default `HiltTestRunner`, you can also use this one, as recommended by the [official docs](https://developer.android.com/training/dependency-injection/hilt-testing#instrumented-tests).

```groovy

android {
    defaultConfig {
        testInstrumentationRunner = "com.rubensousa.carioca.hilt.runner.HiltTestRunner"
    }
}

dependencies {
    androidTestImplementation "com.rubensousa.carioca:hilt-runner:{{ hilt.version }}"
}
```

## Fragment

Before continuing, please check the [official guide](https://developer.android.com/guide/fragments/test) for testing fragments.

`com.rubensousa.carioca:hilt-fragment` contains `HiltFragmentScenario`, similar to `FragmentScenario` from the fragment-testing library.

The API is similar to `FragmentScenario`: `launchHiltFragment`

```kotlin linenums="1"

@HiltAndroidTest
class ExampleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var injectedDependency: TestDependency

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testFragmentReceivesDependencyFromHilt() {
        // given
        val scenario = launchHiltFragment<TestFragment>()

        // when
        var fragmentDependency: TestDependency? = null
        scenario.onFragment { fragment ->
            fragmentDependency = fragment.getInjectedDependency()
        }

        // then
        assertThat(fragmentDependency).isSameInstanceAs(injectedDependency)
    }
    
}
```

## Compose

Before continuing, please check the [official guide](https://developer.android.com/develop/ui/compose/testing) for testing Compose layouts.

`com.rubensousa.carioca:hilt-compose` contains `createHiltComposeRule`, which is similar to `createAndroidComposeRule`:


```kotlin linenums="1"
@HiltAndroidTest
class HiltComposeRuleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createHiltComposeRule()

    @Inject
    lateinit var injectedDependency: TestDependency

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testComposableReceivesDependencyFromHilt() {
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
        val dependency: TestDependency,
    ) : ViewModel()
}
```
