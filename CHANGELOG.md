# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.2] - 2025-11-07

### Fixed
- iOS: Fixed memory leak in observer cleanup by specifying notification name in removeObserver
- iOS: Added Info.plist with NSMicrophoneUsageDescription

### Added
- Documentation: Added permissions section to README for both iOS and Android

## [1.1.1] - 2025-11-07

### Added
- Android: Added AndroidManifest.xml with MODIFY_AUDIO_SETTINGS permission

### Fixed
- Android: Improved error handling with try-catch block
- Android: Added explicit error message for API levels below 23

## [1.1.0] - 2025-11-07

### Added
- Android support with AudioManager integration
- Android audio input route detection
- Android real-time audio route change notifications
- Support for Android devices (API 23+)
- Cross-platform support for iOS and Android

### Changed
- Updated README to reflect Android support
- Added Android-specific port types documentation

## [1.0.2] - 2025-11-07

### Changed
- Translated all code comments from Japanese to English
- Improved code internationalization

## [1.0.1] - 2025-11-07

### Added
- Installation instructions in README
- Expo prebuild and run commands documentation

## [1.0.0] - 2025-11-07

### Added
- Initial release
- iOS audio input route detection
- Support for detecting microphone types (built-in, Bluetooth, wired headset)
- Real-time audio route change notifications
- Expo module implementation with Swift
