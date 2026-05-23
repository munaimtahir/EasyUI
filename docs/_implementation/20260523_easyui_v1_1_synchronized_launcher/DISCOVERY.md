# EasyUI V1.1 Discovery Report

## Overview
The current MVP (V1.0) provides a basic launcher experience with a home screen, app list, and caregiver settings. The structure is modular, using Jetpack Compose and a clean architecture approach.

## Current Screen Map & Navigation
- **Senior Screens:**
  - `Home`: Grid of tiles (Phone, Messages, Contacts, Photos, Camera, Emergency).
  - `PhoneContacts`: List of favorite contacts.
  - `EmergencyCall`: List of emergency numbers.
  - `HealthInfo`: Display of senior's health information.
  - `AppList`: Searchable list of allowed apps.
- **Caregiver Screens:**
  - `CaregiverTools`: Main dashboard for settings.
  - `LayoutPages`: Configure home page count and themes.
  - `AllowedApps`: Select apps visible to the senior.
  - `ManageContacts`: Manage favorite contacts.
  - `PinSetup`/`PinVerify`: Caregiver PIN management.
  - `EmergencySettings`: Configure emergency numbers.
  - `HealthInfoEditor`: Edit senior's health info.
  - `BackupRestore`: Backup/restore settings via JSON.
  - `ManageHiddenApps`: Hide system apps.

## Current Navigation Routes
Defined in `Routes.kt` and `EasyUiNavGraph.kt`.

## Existing State/Data Sources
- `LauncherSettingsRepository`: Persistence using DataStore.
- `HomeLayoutRepository`: Manages tile layout (Room DB).
- `HiddenAppRepository`: Manages hidden app packages.

## Architecture
- **Data Layer:** DataStore for settings, Room for tiles.
- **Domain Layer:** Models (LauncherSettings, HomeTile, etc.), Rules, and Repository interfaces.
- **UI Layer:** Feature modules (home, apps, caregiver, onboarding) using ViewModels and Compose.

## Current Test Coverage
- Unit tests for logic (rules, ViewModels).
- Instrumentation tests for UI (E2E, but ADB tests are deferred in this sprint).

## Limitations
- Many home tiles currently open external apps directly or route to simple lists.
- Lack of a "synchronized" feel where EasyUI owns the first layer of interaction for all major tasks.
- No local monitoring of phone health (battery, internet).
- Caregiver setup status is not easily visible.

## Implementation Plan
1. **Foundation:**
   - Update `LauncherSettings` and `LauncherSettingsRepository` for Guardian Checks and Setup status.
   - Implement `GuardianCheckRepository` or service.
2. **Senior UI Enhancement:**
   - New screens: `Messages`, `Photos`, `Camera`, `SafeHandoff`.
   - Update `HomeScreen` with a `PhoneHealthCard`.
   - Ensure all major tiles route to EasyUI screens first.
3. **Caregiver UI Enhancement:**
   - Add "Setup Status" section to `CaregiverTools`.
   - Add "Guardian Checks" (Caregiver Watch) settings screen.
4. **Logic Implementation:**
   - Local Guardian Checks (battery, internet, default launcher).
   - Setup Completeness calculation.
5. **Verification:**
   - Unit tests for new logic.
   - Build validation.
