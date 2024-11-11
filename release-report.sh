#!/bin/bash

./gradlew carioca-report:report-runtime:publishAndReleaseToMavenCentral \
  && ./gradlew :carioca-report:report-junit4:publishAndReleaseToMavenCentral \
  && ./gradlew :carioca-report:report-json:publishAndReleaseToMavenCentral \
  && ./gradlew :carioca-report:report-android:publishAndReleaseToMavenCentral \
  && ./gradlew :carioca-report:report-android-compose:publishAndReleaseToMavenCentral \
  && ./gradlew :carioca-report:report-android-coroutines:publishAndReleaseToMavenCentral