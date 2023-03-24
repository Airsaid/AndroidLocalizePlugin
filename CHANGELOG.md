<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Android Localize Plugin Changelog

## [Unreleased]

## [3.0.0] (2023-03-24)

### Added
- Supported OpenAI ChatGPT translator. [#118](https://github.com/Airsaid/AndroidLocalizePlugin/pull/118)

### Other
- Delayed error throwing to avoid losing successfully translated text.

## [2.9.0] (2022-11-29)

### Added
- Supported DeepLPro translator. [#92](https://github.com/Airsaid/AndroidLocalizePlugin/issues/92)

### Fixed
- Fix `xliff:g` attribute does not work. [#91](https://github.com/Airsaid/AndroidLocalizePlugin/issues/91)

## [2.8.0] (2022-10-31)

### Added
- Supported DeepL translator.

## [2.7.0] (2022-10-11)

### Changed
- Improve plugin description information.
- Relax translation file name boundaries.

## [2.6.1] (2022-08-27)

### Added
- Added rich text supported.
- Added signature configuration.

### Changed
- Upgrade Gradle Wrapper to `7.5.1`.
- Upgrade Intellij Gradle Plugin to `1.8.1`.
- Plugin description changed to be taken from the `README.md` file.

## [2.6.0] (2022-06-05)

### Added
- Support Ali translator.
- Support new IDE version.

### Fixed
- Fix google translator translation instability.
- Fix single quote character must be escaped in strings.xml. [#54](https://github.com/Airsaid/AndroidLocalizePlugin/issues/54)
- Fix target folder naming. [#61](https://github.com/Airsaid/AndroidLocalizePlugin/issues/61)

## [2.5.0] (2022-02-11)

### Added
- Support for preserving comments, blank lines and other characters.

## [2.4.0] (2022-01-21)

### Added
- Supported custom google api key.
- Supported plurals&string-array tags.
- Added baidu icon of light mode.

### Changed
- Changed maximum number of cacheable items to 1000.

## [2.3.0] (2021-07-09)

### Added
- Add translation interval time setting.

### Changed
- Replace plugin logo.

## [2.2.1] (2021-07-06)

### Fixed
- Fix translation error when source text is in chinese [#33](https://github.com/Airsaid/AndroidLocalizePlugin/issues/33).

## [2.2.0] (2021-06-08)

### Added
- Add power by translator description.

### Fixed
- Fix incomplete Google translation long text [#31](https://github.com/Airsaid/AndroidLocalizePlugin/issues/31).

## [2.1.0] (2021-06-05)

### Added
- Added Microsoft Translator.
- Added "Use google.com" setting.
- Supported more languages.

## [2.0.0] (2021-06-04)

### Added
- Added multiple translator support.
- Added "Open Translated File" option.
- Added translation cache.

### Changed
- Completely refactor the code.
- Optimized the experience.

### Fixed
- Fixed bugs.

## [1.5.0] (2020-03-28)

### Added
- Added "Select All" option.

## [1.4.0] (2020-03-28)

### Added
- Added proxy support.

### Fixed
- Fixed bugs.

## [1.3.0] (2018-10-14)

### Added
- Added "Overwrite Existing String" option.
- Optimize the experience of choice.

## [1.2.0] (2018-09-28)

### Fixed
- Fixed garbled bug.

## [1.1.0] (2018-09-25)

### Added
- Supported for automatic detection of source file language.

## [1.0.0] (2018-09-24)
- Initial release of the plugin.

[Unreleased]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v3.0.0...HEAD
[3.0.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.9.0...v3.0.0
[2.9.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.8.0...v2.9.0
[2.8.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.7.0...v2.8.0
[2.7.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.6.1...v2.7.0
[2.6.1]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.6.0...v2.6.1
[2.6.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.5.0...v2.6.0
[2.5.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.4.0...v2.5.0
[2.4.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.3.0...v2.4.0
[2.3.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.2.1...v2.3.0
[2.2.1]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.2.0...v2.2.1
[2.2.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.5...v2.0.0
[1.5.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.4...v1.5
[1.4.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.3...v1.4
[1.3.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.2...v1.3
[1.2.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.1...v1.2
[1.1.0]: https://github.com/Airsaid/AndroidLocalizePlugin/compare/v1.0...v1.1
[1.0.0]: https://github.com/Airsaid/AndroidLocalizePlugin/commits/v1.0
