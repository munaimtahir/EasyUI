# HOME REDESIGN IMPLEMENTATION

## What changed

- Home back behavior is now inert at root (`BackHandler` on `Routes.Home`) so pressing back on Home does nothing.
- Home UI was redesigned to a fixed, non-scrolling 2x3 grid with six fixed tiles:
  - Phone
  - Flashlight
  - Camera
  - Emergency
  - Health Info
  - SOS
- Removed visible caregiver button from Home and finalized hidden caregiver entry model:
  - 3-second long-press anywhere on top status bar.
  - 5 quick taps on the clock within 3 seconds (fallback).
  - Both routes open the same caregiver access pipeline.
- Added SOS workflow:
  - SOS tile requires 3 quick taps.
  - Sends SMS to up to 3 configured SOS numbers.
  - Attempts immediate call to primary SOS number.
  - Includes visual progress and cooldown/debounce.
- Added emergency call screen with configurable emergency numbers.
- Added phone contacts screen with up to 10 caregiver-managed contacts (using existing contact tile storage).
- Added large top status bar (time, battery %, charging, signal label, SIM label, Wi-Fi label).
- Added EasyUI app-level lock overlay:
  - Can be enabled in caregiver emergency/safety settings.
  - Locks on inactivity timeout and on app resume.
  - Requires caregiver PIN to unlock.

## Where implemented

- Navigation and lock overlay:
  - `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
  - `app/src/main/java/com/easyui/launcher/navigation/Routes.kt`
- Home logic/state:
  - `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
  - `app/src/main/java/com/easyui/launcher/app/HomeUiState.kt`
- Home UI:
  - `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
  - `feature/home/src/main/java/com/easyui/feature/home/HomeActionScreens.kt`
  - `core/ui/src/main/java/com/easyui/core/ui/components/LargeActionTile.kt`
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

- SMS auto-send and direct call depend on Android runtime permissions (`SEND_SMS`, `CALL_PHONE`) and OEM behavior.
- If call permission is unavailable, emergency call falls back to dialer behavior where possible.
- Signal/SIM/Wi-Fi status labels are best-effort and may be limited on some Android versions/devices.
- Contact management still reuses existing home contact storage model; this preserves compatibility but is not yet a dedicated contacts table.
- Device/ADB validation scenarios are not executed in this static run and should be completed on physical hardware.

## Caregiver Access (Final Model)
- Long press top bar (3s)
- Clock 5-tap fallback
- PIN-gated access
