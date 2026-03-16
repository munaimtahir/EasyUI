# Play Release Readiness

## Scope

This release-readiness pass applies to the Android project that actually exists in this repository: `EasyUI Senior Launcher`.

The original task request referenced `Warranty Vault`, but that product is not present in this repo. All audit findings, policy notes, and store materials below are based on the in-repo launcher app only.

## Audit date

March 16, 2026 (UTC)

## Release audit summary

### Build and packaging

- App module: `app`
- Application ID: `com.easyui.launcher`
- Namespace: `com.easyui.launcher`
- `minSdk`: 26
- `targetSdk`: 35
- `compileSdk`: 35
- Versioning after this pass:
  - `versionCode`: 1
  - `versionName`: `1.0.0`
- Release build type exists and now enables R8/resource shrinking.
- App Bundle generation is supported through the standard Gradle `bundleRelease` task.
- No signing credentials are stored in the repo. Final upload signing remains a manual release step.

### Manifest and component posture

- Single exported activity: `com.easyui.launcher.MainActivity`
- Home launcher intent filters:
  - `android.intent.action.MAIN`
  - `android.intent.category.HOME`
  - `android.intent.category.DEFAULT`
- No services
- No broadcast receivers declared in manifest
- No deep links
- No `FileProvider`
- No network security config
- No cleartext traffic override
- Automatic Android backup is now disabled because the app does not yet expose an explicit backup/export flow for release.

### Permissions and feature declarations

After this pass the manifest does not request any runtime permissions.

Optional hardware feature declarations:

- `android.hardware.camera.flash` with `required="false"`
  - Used only for the optional flashlight tile.
  - The feature remains optional so the launcher is installable on devices without flash hardware.

### Data, SDK, and policy-sensitive behavior

Observed from the codebase:

- Local Room database for home tiles
- Local DataStore preferences for onboarding, caregiver settings, hidden apps, and PIN hash/salt
- Local persisted photo URI references for favorite contact tiles
- PackageManager queries for installed launchable apps
- Dialer launch through `ACTION_DIAL`
- Flashlight control through `CameraManager.setTorchMode`

Not observed in the repo:

- analytics SDKs
- crash reporting SDKs
- ad SDKs
- account/login flows
- backend API clients
- external data transmission
- notification scheduling
- reminder channels
- billing integration in the shipped app module
- backup/export implementation in the shipped app module

### Release risks found during audit

- The repo did not include launcher icon assets suitable for release packaging.
- The manifest requested `CAMERA`, which was broader than necessary for the optional torch tile.
- Automatic backup was enabled even though explicit backup/export is not implemented and caregiver PIN data is stored locally.
- Room schema export was disabled, which weakened public-release migration discipline.

## Repo-side hardening completed

- Removed the `CAMERA` permission by refactoring flashlight support to use flash feature detection plus safe torch attempts.
- Disabled automatic backup in the manifest.
- Added launcher icon and round icon resources, including monochrome support for themed icons.
- Enabled release minification and resource shrinking.
- Enabled Room schema export and kapt schema generation for migration tracking.

## Manual release boundary

The following still requires human action outside the repository:

- choose final signing key ownership strategy
- configure Play App Signing in Google Play Console
- create the Play listing
- upload screenshots and feature graphic
- host the privacy policy at a stable public URL
- complete Data Safety and content rating questionnaires
- upload the signed `.aab`
- choose testing and rollout tracks
