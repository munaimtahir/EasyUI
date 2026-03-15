# Setup

## Development environment
- Android Studio latest stable
- JDK 17
- Android SDK target latest stable supported version
- Kotlin
- Gradle Kotlin DSL

## Recommended project baseline
- Compose Material 3
- Room
- DataStore
- Hilt or a light DI approach
- Detekt / ktlint
- JUnit + MockK
- Compose UI testing

## Local setup steps
1. Clone repo
2. Open in Android Studio
3. Sync Gradle
4. Create debug build
5. Run on emulator and at least one physical Android device
6. Verify launcher intent filter registration
7. Verify app can be selected as default launcher

## Device test recommendations
Use at least:
- one stock-ish Android device
- one Samsung device
- one Xiaomi / HyperOS / Poco device if available

## Build variants
- `debug`
- `release`
- optional `qa`

## Secrets/config
No backend secrets required for MVP.
Billing test product IDs may be environment-specific and should be defined via Gradle properties or local config that is excluded from VCS if needed.

## Coding conventions
- Keep composables small and readable
- Avoid giant view models
- Put business rules in plain Kotlin classes
- Use explicit naming over clever abstractions
- Comment only where intent is non-obvious
