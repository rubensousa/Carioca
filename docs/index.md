# Carioca

A collection of testing tools for Android that include flexible reporting capabilities

Artifacts available for test reports:

```groovy
// Test reports for android tests
androidTestImplementation "com.rubensousa.carioca:report-android:{{ report.version }}"

// Optional: test reporting for tests with coroutines
androidTestImplementation "com.rubensousa.carioca:report-android-coroutines:{{ report.version }}"

// Optional: libraries to build your own report formats
androidTestImplementation "com.rubensousa.carioca:report-json:{{ report.version }}"
androidTestImplementation "com.rubensousa.carioca:report-runtime:{{ report.version }}"
```

Artifacts available for hilt tests:

```groovy
// Contains HiltFragmentScenario
androidTestImplementation "com.rubensousa.carioca:hilt-fragment:{{ hilt.version }}"

// Contains createHiltComposeRule()
androidTestImplementation "com.rubensousa.carioca:hilt-compose:{{ hilt.version }}"

// Contains an empty hilt activity that is required 
// by both of the dependencies above
debugImplementation "com.rubensousa.carioca:hilt-manifest:{{ hilt.version }}"

// Optional: default HiltTestRunner if you don't have your own
androidTestImplementation "com.rubensousa.carioca:hilt-runner:{{ hilt.version }}"
```

Other artifacts:

```groovy
// Contains RepeatTest and RetryTest
implementation "com.rubensousa.carioca:junit4-rules:{{ junit4_rules.version }}"
```

Jump to [this guide](test-reports-android.md) for how to integrate this library.


Motivation for this library:

1. I kept copying some of these classes around in multiple projects
2. Analysing the standard junit test reports when you have long UI tests gets frustrating
3. Consistent screen recording and screenshots across different screen resolutions
4. Flexible APIs for any test report format
5. No enforced inheritance in test classes unlike other testing libraries


## License

    Copyright 2024 Rúben Sousa
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
