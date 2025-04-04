# JUnit4 Rules

A collection of useful test rules for multiple use cases

## RetryTestRule

This rule can be useful for end-to-end tests that have some degree of tolerable flakiness.

Avoid using it for all sorts of tests!

Total executions = `[1, 1 + times]`, depends on which execution the test actually passes

```kotlin linenums="1"
@RetryTest(times = 9)
class SampleRetryTest {

    // Make sure that this rule starts after all rules that shouldn't be repeated
    // E.g: compose test rule can only be applied once
    @get:Rule(order = 100)
    val retryRule = RetryTestRule()

    @RetryTest(times = 2)
    @Test
    fun testExampleMethod() {
        // This test will be executed 3 times in the worst case
    }

    @Test
    fun testExampleClass() {
        // This test will be executed 10 times in the worst case
    }
}
```

## RepeatTestRule

This rule is useful to check for test flakiness. 
Use it carefully and do not commit code with these.

Total executions = `1 + times`

```kotlin linenums="1"
@RepeatTest(times = 2)
class SampleRepeatTest {

    // Make sure that this rule starts before any other rule
    @get:Rule(order = 0)
    val repeatRule = RepeatTestRule()
    
    @RepeatTest(times = 99)
    @Test
    fun `this test will execute 100 times`() {
        // Number of times is defined by the method annotation
    }
    
    @Test
    fun `this test will execute 3 times`() {
        // Number of times is defined by the class annotation
    }

}
```

## MainDispatcherRule

As seen [here](https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher):

```kotlin linenums="1"
class ViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `this test replaces Dispatchers.Main for ViewModels`() = runTest {
        // given
        val viewModel = TestViewModel()
        
        // when
        viewModel.load()
        
        // then
        assertThat(viewModel.uiState).isNotNull()
    }
}
```

## TestDescriptionRule

A rule for extracting metadata for the current test being executed.

```kotlin
class SampleTest {

    @get:Rule
    val testDescriptionRule = TestDescriptionRule()

    @Test
    fun `test example`() {
        val description = testDescriptionRule.getDescription()
        assertThat(description.methodName).isEqualTo("test example")
    }

}

```
