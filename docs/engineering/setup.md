# Setup

## Development environment

- Android Studio latest stable
- JDK 17
- Android SDK target latest stable supported version
- Kotlin
- Gradle Kotlin DSL

## Recommended baseline

- Compose Material 3
- Room
- DataStore
- Hilt or a lightweight DI approach
- ktlint and detekt
- JUnit and MockK
- Compose UI testing

## Local setup steps

1. Open the repository in Android Studio.
2. Sync Gradle.
3. Create a debug build.
4. Run on an emulator and at least one physical Android device.
5. Verify launcher intent registration.
6. Verify the app can be selected as the default launcher.

## Device test recommendations

- one stock Android device
- one Samsung device
- one Xiaomi, HyperOS, or Poco device if available

## Build variants

- `debug`
- `release`
- optional `qa`

## Config

No backend secrets are required. Billing test product IDs should be provided via local Gradle properties or another VCS-excluded configuration path.
