# Data Model

## Overview

The data model is fully local. Separate durable user configuration from transient installed-app inventory.

## Entities

### AppEntry

- `packageName: String`
- `label: String`
- `activityName: String?`
- `iconCacheKey: String?`
- `isLaunchable: Boolean`
- `isSystemApp: Boolean`
- `lastSeenAt: Long`

### HomeTile

- `id: String`
- `type: TileType`
- `title: String`
- `subtitle: String?`
- `positionIndex: Int`
- position is a fixed global home slot index
- page = `positionIndex / 6`
- slot = `positionIndex % 6`
- `targetPackageName: String?`
- `contactId: String?`
- `actionId: String?`
- `iconStyle: String?`
- `isVisible: Boolean`

### ContactShortcut

- `id: String`
- `displayName: String`
- `phoneNumber: String`
- `photoUri: String?`
- `relationshipLabel: String?`
- `priorityOrder: Int`

### HiddenAppRule

- `packageName: String`
- `isHidden: Boolean`

### CaregiverSettings

- `pinHash: String?`
- `isEditLocked: Boolean`
- `isLongPressDisabled: Boolean`
- `themeMode: String`
- `showBatteryPercent: Boolean`
- `showBatteryInfo: Boolean`
- `homePageCount: Int`
- `showAttentionIndicators: Boolean`
- `isPremiumUnlocked: Boolean`
- `lastBackupAt: Long?`

### EmergencyActionConfig

- `contactName: String?`
- `phoneNumber: String?`
- `requiresConfirmation: Boolean`

### BackupPayload

- metadata version
- home tiles
- contact shortcuts
- hidden app rules
- caregiver settings subset
- emergency action config

## Persistence rules

- Use Room tables for structured records.
- Use DataStore for lightweight settings and premium flags.
- Use file-based export for backup payloads.
- Refresh installed apps from PackageManager rather than treating them as owned data.
- Home layout must reference package names, not list indexes.
- Home layout positions are fixed caregiver-defined slots, not freeform drag ordering.
- Reserve stable daily-use slots for launcher actions such as `Phone` and `All Apps`.
- Hidden-app rules must survive app list refreshes.

## Migration rules

- prefer additive schema changes
- version backup payloads
- avoid destructive migration after public release
