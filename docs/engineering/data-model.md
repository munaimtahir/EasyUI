# Data Model

## Overview

The data model is fully local. Separate durable user configuration from transient installed-app inventory, and keep backup payloads versioned.

## Entities

### InstalledApp

- `packageName: String`
- `activityName: String`
- `label: String`

### HomeTile

- `id: String`
- `position: Int`
- `title: String`
- `type: HomeTileType`
- `packageName: String?`
- `action: HomeTileAction?`
- `phoneNumber: String?`
- `photoUri: String?`

### HomeTileType

- `APP`
- `ACTION`
- `CONTACT`

### HomeTileAction

- `OPEN_DIALER`
- `OPEN_APP_LIST`
- `FLASHLIGHT`
- `EMERGENCY`
- `OPEN_CAMERA`
- `OPEN_HEALTH_INFO`
- `SOS`

### CaregiverContact

- `id: String`
- `name: String`
- `phoneNumber: String`
- `photoUri: String?`

### EmergencyNumber

- `label: String`
- `phoneNumber: String`

### HealthInfo

- `fullName: String`
- `age: String`
- `bloodGroup: String`
- `allergies: String`
- `medicalConditions: String`
- `medicines: String`
- `doctorOrEmergencyContact: String`
- `notes: String`

### LauncherSettings

- `onboardingComplete: Boolean`
- `emergencyPhoneNumber: String`
- `emergencyNumbers: List<EmergencyNumber>`
- `sosNumbers: List<String>`
- `use24HourClock: Boolean`
- `caregiverProtectionEnabled: Boolean`
- `layoutLocked: Boolean`
- `easyUiLockEnabled: Boolean`
- `easyUiLockTimeoutSeconds: Int`
- `pinSaltHex: String?`
- `pinHashHex: String?`
- `appVisibilityPreset: String`
- `homeReadabilityPreset: String`
- `verySimpleModeEnabled: Boolean`
- `showBatteryInfo: Boolean`
- `homePageCount: Int`
- `healthInfo: HealthInfo`
- `skinConfig: SkinConfig`

### SkinConfig

- `layoutMode: LayoutMode`
- `visualTheme: VisualTheme`
- `accessibilityMode: AccessibilityMode`

### BackupData

- `version: Int`
- `settings: LauncherSettings`
- `tiles: List<HomeTile>`
- `hiddenPackages: Set<String>`

### Hidden app state

- persisted as a `Set<String>` of package names
- hidden apps stay hidden inside EasyUI after app inventory refreshes

### UI models

- `TileDisplayModel` and `TileDisplayKind` are presentation-only models used by the home screen.

## Persistence rules

- Use Room tables for structured records.
- Use DataStore for lightweight settings, lock state, and premium flags.
- Use file-based export for backup payloads.
- Refresh installed apps from PackageManager rather than treating them as owned data.
- Home layout must reference package names, not list indexes.
- Home layout positions are fixed caregiver-defined slots, not freeform drag ordering.
- Reserve positions `0..5` for the six essential senior-facing tiles.
- Home Apps slots are caregiver-managed and bounded by page count.
- Hidden-app rules must survive app list refreshes.
- PIN credentials are intentionally excluded from backups.

## Migration rules

- prefer additive schema changes
- version backup payloads
- avoid destructive migration after public release
