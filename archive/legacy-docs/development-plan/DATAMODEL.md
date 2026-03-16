# Data Model

## Overview
The data model is fully local. Separate durable user configuration from transient device app inventory.

## Entities

### AppEntry
Represents an installed app discovered from PackageManager.
- `packageName: String` (primary identifier)
- `label: String`
- `activityName: String?`
- `iconCacheKey: String?`
- `isLaunchable: Boolean`
- `isSystemApp: Boolean`
- `lastSeenAt: Long`

### HomeTile
Represents an item shown on the home screen.
- `id: String`
- `type: TileType` (`APP`, `CONTACT`, `ACTION`)
- `title: String`
- `subtitle: String?`
- `positionIndex: Int`
- `targetPackageName: String?`
- `contactId: String?`
- `actionId: String?`
- `iconStyle: String?`
- `isVisible: Boolean`

### ContactShortcut
Represents a caregiver-configured contact tile.
- `id: String`
- `displayName: String`
- `phoneNumber: String`
- `photoUri: String?`
- `relationshipLabel: String?`
- `priorityOrder: Int`

### HiddenAppRule
Controls app visibility.
- `packageName: String`
- `isHidden: Boolean`

### CaregiverSettings
Persisted settings for caregiver protection.
- `pinHash: String?`
- `isEditLocked: Boolean`
- `isLongPressDisabled: Boolean`
- `themeMode: String`
- `showBatteryPercent: Boolean`
- `showAttentionIndicators: Boolean`
- `isPremiumUnlocked: Boolean`
- `lastBackupAt: Long?`

### EmergencyActionConfig
- `contactName: String?`
- `phoneNumber: String?`
- `requiresConfirmation: Boolean`

### BackupPayload
Serialized export object for config backup.
- app metadata version
- home tiles
- contact shortcuts
- hidden app rules
- caregiver settings subset
- emergency action config

## Persistence
- Room tables for structured records
- DataStore for lightweight settings and premium flags
- File-based export for backup payload

## Data rules
1. Installed apps are not owned data; refresh from PackageManager.
2. Home layout references package names, not app list indexes.
3. Hidden-app rules survive app list refreshes.
4. Contact photos are referenced by safe local URI or copied private file path if required.
5. Premium state must be re-checkable from billing cache, but UI should tolerate offline mode gracefully.

## Migration rules
- Additive schema changes preferred
- Always version backup payloads
- Avoid destructive migration after public release
