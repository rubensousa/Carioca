# Carioca

A collection of testing tools for Android that include flexible reporting capabilities

Artifacts available:


```groovy
// Test reports for android tests
implementation "com.rubensousa.carioca:report-android:{{ report.version }}"

// junit4 rules for testing
implementation "com.rubensousa.carioca:junit4-rules:{{ junit4_rules.version }}"

// Optional test reporting for tests with coroutines
implementation "com.rubensousa.carioca:report-android-coroutines:{{ report.version }}"

// Optional: libraries to build your own report formats
implementation "com.rubensousa.carioca:report-json:{{ report.version }}"
implementation "com.rubensousa.carioca:report-runtime:{{ report.version }}"
```

Jump to [this guide](test-reports-android) for how to integrate this library.


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
