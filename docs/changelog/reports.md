# Changelog Reports

Libraries with the same version:

- `com.rubensousa.carioca:report-android:{{ report.version }}`
- `com.rubensousa.carioca:report-android-compose:{{ report.version }}`
- `com.rubensousa.carioca:report-android-coroutines:{{ report.version }}`
- `com.rubensousa.carioca:report-runtime:{{ report.version }}`
- `com.rubensousa.carioca:report-json:{{ report.version }}`
- `com.rubensousa.carioca:report-junit4:{{ report.version }}`

## Version 1.0.0

### 1.0.0-alpha04

2024-10-01

#### New features

- Added `DumpComposeHierarchyInterceptor` in `com.rubensousa.carioca:report-android-compose`: [#71](https://github.com/rubensousa/Carioca/pull/71/)

### 1.0.0-alpha03

2024-09-30

#### API changes

- Removed `dumpOnSuccess` from `DumpViewHierarchyInterceptor`

#### Improvements

- Decreased default delays for screen recordings, which decreases the total test execution time by around 25% [#63](https://github.com/rubensousa/Carioca/pull/63)
- Added `RecordingOrientation` to `RecordingOptions` to allow overriding the default orientation of the video [#65](https://github.com/rubensousa/Carioca/pull/65)

#### Bug fixes

- Fixed screenshot options inside steps or scenarios not using the provided options [#62](https://github.com/rubensousa/Carioca/pull/62)

### 1.0.0-alpha02

2024-09-27

#### Improvements

- Write test report failures before test instrumentation receives failure event: [#51](https://github.com/rubensousa/Carioca/pull/51)

#### Bug fixes

- Fixed title of `When` statement when used with a scenario: [#50](https://github.com/rubensousa/Carioca/pull/50)


### 1.0.0-alpha01

2024-09-22

- Initial alpha release
