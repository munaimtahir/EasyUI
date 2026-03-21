# HOME REDESIGN IMPLEMENTATION

## What changed

- Home back behavior is now inert at root (`BackHandler` on `Routes.Home`) so pressing back on Home does nothing.
- Home UI was redesigned to the locked senior-facing visual spec with a fixed, non-scrolling 2x3 grid:
  - Phone
  - Messages
  - Contacts
  - Photos
  - Camera
  - Emergency
- The new first page uses a hard-coded visual shell:
  - full-screen vertical indigo gradient background
  - large blue time/date header card
  - equal-size solid color tiles with white filled icons and short white labels
  - no visible settings tile, app-list tile, placeholders, or technical device-status text
- Removed visible caregiver button from Home and finalized hidden caregiver entry model:
  - 3-second long-press anywhere on top status bar.
  - 5 quick taps on the clock within 3 seconds (fallback).
  - Both routes open the same caregiver access pipeline.
- Added emergency call screen with configurable emergency numbers.
- Added phone contacts screen with up to 10 caregiver-managed contacts (using existing contact tile storage).
- Added safe app-resolution behavior for the fixed `Messages` and `Photos` tiles, with fallback messaging when the device does not expose a suitable target app.
- Added EasyUI app-level lock overlay:
  - Can be enabled in caregiver emergency/safety settings.
  - Locks on inactivity timeout and on app resume.
  - Requires caregiver PIN to unlock.

## Tokens and components

- Locked home tokens and icon mapping live in:
  - `feature/home/src/main/java/com/easyui/feature/home/SeniorHomeTokens.kt`
- Home rendering now uses:
  - `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
- Home action mapping now uses:
  - `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
  - `core/domain/src/main/java/com/easyui/core/domain/rules/PrimaryHomeAppRules.kt`
- Preview evidence is included directly in `HomeScreen.kt`:
  - default senior-home preview
  - large-text preview

## Where implemented

- Navigation and lock overlay:
  - `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
  - `app/src/main/java/com/easyui/launcher/navigation/Routes.kt`
- Home logic/state:
  - `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
  - `app/src/main/java/com/easyui/launcher/app/HomeUiState.kt`
- Home UI:
  - `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
  - `feature/home/src/main/java/com/easyui/feature/home/SeniorHomeTokens.kt`
  - `feature/home/src/main/java/com/easyui/feature/home/HomeActionScreens.kt`
- Caregiver safety configuration:
  - `feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt`
  - `app/src/main/java/com/easyui/launcher/app/caregiver/CaregiverViewModel.kt`
- Settings/data model persistence:
  - `core/domain/src/main/java/com/easyui/core/domain/model/LauncherSettings.kt`
  - `core/domain/src/main/java/com/easyui/core/domain/model/SafetyModels.kt`
  - `core/domain/src/main/java/com/easyui/core/domain/repository/LauncherSettingsRepository.kt`
  - `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt`
  - `core/data/src/main/java/com/easyui/core/data/backup/BackupSerializer.kt`
  - `core/data/src/main/java/com/easyui/core/data/repository/LocalBackupRepository.kt`
- Platform integrations:
  - `core/platform/src/main/java/com/easyui/core/platform/actions/AndroidEmergencyActionHandler.kt`
  - `core/platform/src/main/java/com/easyui/core/platform/actions/AndroidDeviceStatusRepository.kt`
  - `app/src/main/AndroidManifest.xml`

## Known limitations

- If call permission is unavailable, emergency call falls back to dialer behavior where possible.
- `Messages` and `Photos` depend on the device exposing a launchable app that matches the preferred package or label heuristics.
- Contact management still reuses existing home contact storage model; this preserves compatibility but is not yet a dedicated contacts table.
- Device/ADB validation scenarios are not executed in this static run and should be completed on physical hardware.

## Caregiver Access (Final Model)
- Long press top bar (3s)
- Clock 5-tap fallback
- PIN-gated access
