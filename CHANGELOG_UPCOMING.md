# Report 1.0.0-alpha03

## API changes

- Removed `dumpOnSuccess` from `DumpViewHierarchyInterceptor`

## Improvements

- Decreased default delays for screen recordings, which decreases the total test execution time by around 25% [#63](https://github.com/rubensousa/Carioca/pull/63)
- Added `RecordingOrientation` to `RecordingOptions` to allow overriding the default orientation of the video [#65](https://github.com/rubensousa/Carioca/pull/65)

## Bug fixes

- Fixed screenshot options inside steps or scenarios not using the provided options [#62](https://github.com/rubensousa/Carioca/pull/62)


# Rules 1.0.0-alpha02

## New features

- Added `MainDispatcherRule` to replace `Dispatcher.Main` in unit tests