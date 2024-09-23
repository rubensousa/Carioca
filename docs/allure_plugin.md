# Android Allure Plugin

This library comes with an [Allure](https://allurereport.org/) plugin that can be used to generate test reports based on
the metadata collected through each test execution.

To use it, just add the plugin to your project:

```groovy
plugins {
    id 'com.rubensousa.carioca.report.allure' version '{{ allure_plugin.version }}'
}
```

After each test execution, the plugin will generate the allure results
at:

```
build/output/allure-results
```

Then just run `allure serve` or `allure generate` to generate the reports and you should see something like this:

![Allure report](img/allure_example.png)

The following attachments are included out of the box for every failure:

1. Screen recording
2. Screenshot immediately after failure
3. View hierarchy dump
4. Logcat during test execution


Configuration options for the plugin currently available:

```kotlin
allureReport {
    /**
     * The name of the test task that will be invoked
     * to generate the report on `connectedAllureReport`
     */
    testTask = "connectedDebugAndroidTest"
    /**
     * By default, logcat files are not included if the test passes
     */
    keepLogcatOnSuccess = false
    /**
     * By default, the original carioca reports are deleted to save disk space
     */
    deleteOriginalReports = true
}
```