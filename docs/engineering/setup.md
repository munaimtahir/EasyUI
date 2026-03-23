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
3. Build a debug APK with `./gradlew :app:assembleDebug`.
4. Run on at least one physical Android device.
5. Verify launcher intent registration.
6. Verify the app can be selected as the default launcher.
7. Verify onboarding can reach home on the target display size.

## Device test recommendations

- one stock Android device
- one Samsung device
- one Xiaomi, HyperOS, or Poco device if available

## Build variants

- `debug`
- `release`

## Config

No backend secrets are required. Billing test product IDs, when premium work resumes, should be provided through a VCS-excluded local configuration path.

## E2E commands

- `./e2e/scripts/run-static.sh`
- `./e2e/scripts/run-device-smoke.sh <serial>`
- `./e2e/scripts/run-device-full.sh <serial>`
