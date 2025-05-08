# Changelog Reports

Libraries with the same version:

- `com.rubensousa.carioca:report-android:{{ version.report }}`
- `com.rubensousa.carioca:report-android-compose:{{ version.report }}`
- `com.rubensousa.carioca:report-android-coroutines:{{ version.report }}`
- `com.rubensousa.carioca:report-runtime:{{ version.report }}`
- `com.rubensousa.carioca:report-json:{{ version.report }}`
- `com.rubensousa.carioca:report-junit4:{{ version.report }}`

## Version 1.0.1

2025-05-01

- Allow subclasses of scenario to customize their title and id via `getTitle()` and `getId()`: [#83](https://github.com/rubensousa/Carioca/pull/83)

## Version 1.0.0

### 1.0.0

2025-04-05

- No changes since `1.0.0-rc01`

### 1.0.0-rc01

2025-04-05

- No changes since `1.0.0-beta01`

### 1.0.0-beta01

2024-11-11

No changes since `1.0.0-alpha04`

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
