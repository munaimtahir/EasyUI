# Caregiver Dashboard Discovery

## Scope

This discovery treats the post-setup caregiver dashboard as the canonical settings surface. Onboarding is a partial setup path, not the full runtime control plane.

## Verification

- `./gradlew :app:assembleDebug :app:testDebugUnitTest --no-daemon --stacktrace` passed.
- `./gradlew :app:connectedDebugAndroidTest --no-daemon --stacktrace` passed on the attached device `TECNO CH6i - 13`.
- The first connected run exposed a stale androidTest compile mismatch in `CaregiverQolSmokeTest.kt`; I updated the test to match the current `LayoutPagesScreen` signature and labels, then reran the device suite successfully.

## Canonical Caregiver Dashboard

The dashboard is rendered by `CaregiverDashboardScreen()` and wired from `EasyUiNavGraph` through `Routes.CaregiverTools`.

### Setup Status

- `EasyUI set as default`
- `Caregiver PIN set`
- `Layout locked`
- `Emergency contact set`
- `Important contacts added`
- `Allowed apps selected`
- `Permissions available`

### Appearance & Layout

- `Readability`
- `Visual Theme`
- `Theme & Pages`

### Home Apps

- `Allowed Apps`
- `Hidden Apps`

### Contacts & Emergency

- `Call Shortcuts`
- `Emergency Settings`
- `Health Information`

### Security & Protection

- `Layout Lock`
- `Show 'All Apps'`
- `Caregiver PIN`

### Device & Backup

- `Show Battery Info`
- `Backup & Restore`
- `Guardian Checks`
- `Linked Phones`
- `Share My Status`

### Final actions

- `Reset Launcher to Defaults`
- `Exit to Senior Home`

## Status Summary

### Functional and available

- `Readability`, `Visual Theme`, `Allowed Apps`, `Hidden Apps`, `Call Shortcuts`, `Emergency Settings`, `Health Information`, `Layout Lock`, `Show 'All Apps'`, `Caregiver PIN`, `Show Battery Info`, `Backup & Restore`, `Linked Phones`, `Share My Status`, `Reset Launcher to Defaults`, and `Exit to Senior Home` are wired to real navigation or repository updates.
- `EmergencySettingsScreen`, `HiddenAppsScreen`, `BackupRestoreScreen`, and `LinkedDevicesScreen` all have concrete UI and callbacks.

### Needs debugging or proper configuration

- `Permissions available` is currently stubbed as always true in `CaregiverViewModel` (`hasRequiredPermissions = true`), so the setup status can look complete even when device checks have not been performed.
- `LayoutPagesScreen` exposes `onIncreasePageCount` and `onDecreasePageCount`, but the current UI does not render any page count controls. The caregiver dashboard only shows a summary row and routes into the page/layout screen.
- `GuardianSettingsScreen` exposes `onUpdateNoInternetDelay` and `onUpdatePermissionCheck`, but there is no visible control for either setting in the current UI.

## Onboarding Coverage

Onboarding currently covers these overlapping settings:

- default launcher guidance
- protection level
- caregiver PIN and layout lock
- visual theme and accessibility mode
- readability preset
- home page count
- allowed apps placement
- emergency mode and emergency phone number
- call shortcut setup
- battery visibility

Onboarding does not cover these caregiver dashboard features:

- hidden apps
- backup and restore
- guardian checks
- linked phones
- share my status
- health information editor
- all-apps visibility toggle
- the full emergency settings surface (`SOS` numbers, quick emergency numbers, and EasyUI lock timeout)

## Source References

- [Caregiver dashboard](../../../feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverDashboard.kt)
- [Dashboard wiring and routes](../../../app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt)
- [Caregiver view model](../../../app/src/main/java/com/easyui/launcher/app/caregiver/CaregiverViewModel.kt)
- [Layout / emergency / health screens](../../../feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt)
- [Guardian checks screen](../../../feature/caregiver/src/main/java/com/easyui/feature/caregiver/GuardianSettingsScreen.kt)
- [Onboarding screens](../../../feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt)
