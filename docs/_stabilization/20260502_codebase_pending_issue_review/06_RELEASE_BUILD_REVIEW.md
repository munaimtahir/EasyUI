# Release Build Review

Running `./gradlew :app:assembleRelease --stacktrace` and `./gradlew :app:bundleRelease --stacktrace`

Currently checking if release signing secrets are present.
Looking at `app/build.gradle.kts`:
If release signing secrets are missing, `hasReleaseSigning` is `false` and `signingConfig` will not be set for release build. This means unsigned release build is possible but it won't be signed.

Exact missing variables needed for signed Play Store AAB:
`EASYUI_KEYSTORE_PATH`
`EASYUI_KEYSTORE_PASSWORD`
`EASYUI_KEY_ALIAS`
`EASYUI_KEY_PASSWORD`

Next step needed for signed Play Store AAB:
1. Create a keystore using `keytool` or Android Studio.
2. Provide those 4 values via `local.properties` (or `keystore.properties` per project setup), environment variables, or CI secrets.

Builds ran successfully.
- Unsigned release apk found at `app/build/outputs/apk/release/app-release-unsigned.apk` (3.2M)
- Unsigned release bundle found at `app/build/outputs/bundle/release/app-release.aab` (5.9M)
