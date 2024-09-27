# Release guide

1. Change version of one of the libraries consistently
   - Report in `carioca-report/gradle.properties`
   - Allure plugin in `carioca-report/report-android-allure-gradle-plugin/gradle.properties`
   - junit4 rules `carioca-junit4-rules/gradle.properties`
2. Tag the release based on the modules pushed:
   - `git tag -a report-1.0.1 abcdef -m "1.0.1"`
   - `git tag -a junit4-rules-1.0.1 abcdef "1.0.1"`
   - `git tag -a allure-plugin-1.0.1 abcdef "1.0.1"`
   - `git tag -a hilt-1.0.1 abcdef "1.0.1"`
3. Push the tag: `git push origin --tags`
