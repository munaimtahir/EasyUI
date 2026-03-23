# API Interfaces

## External integrations

### Android launcher integration

- `ACTION_MAIN`
- `CATEGORY_HOME`
- `CATEGORY_DEFAULT`
- first-run guidance for setting default launcher

### PackageManager

Used to:

- enumerate launchable apps
- fetch labels and icons
- detect install and uninstall changes

### Calling and SOS actions

- regular phone and favorite-contact flows should prefer safe dialer behavior
- SOS may use:
  - `ACTION_DIAL` fallback
  - direct call when `CALL_PHONE` is granted
  - SMS sending when `SEND_SMS` is granted
- permission-denied states must degrade safely and clearly

### Flashlight

Use CameraManager torch APIs when available. If unsupported, hide or disable safely.

### Camera

Launch the installed camera app through a standard intent. If unavailable, show a safe fallback message.

### Battery and device status

Observe battery and device status locally for the home header. Treat the values as best-effort device state, not guaranteed telemetry.

### Contacts

Keep the current implementation local-first. Photo contacts use launcher-owned local records and optional local photo URIs.

### Billing

Google Play Billing Library is planned for the premium unlock path, but it is not wired into the current build yet.

- one-time non-consumable premium product
- local entitlement cache
- testable abstraction
- graceful fallback to free mode if billing is unavailable

## Internal interfaces

### AppCatalogRepository

- load installed launchable apps
- search and sort apps
- expose filtered visible apps and app-list scaffold state

### HomeLayoutRepository

- read and write home tiles
- reorder tiles
- validate tile positions
- restore defaults

### LauncherSettingsRepository

- lock state
- PIN state
- theme and visibility preferences
- home readability and accessibility preferences
- emergency and SOS settings
- health info
- premium state cache

### HiddenAppRepository

- persist hidden package names
- hide and unhide packages without affecting installed apps

### BackupRepository

- export config payload
- validate import payloads
- apply validated backups atomically

### Platform action wrappers

- `AppLauncher`
- `DefaultLauncherManager`
- `EmergencyActionHandler`
- `FlashlightController`
- `CameraActionHandler`
- `BatteryStatusRepository`
- `DeviceStatusRepository`
- `AndroidDefaultLauncherManager`
- `AndroidAppLauncher`

### ContactsRepository

- CRUD photo contacts
- local photo reference handling

### PremiumService

Planned only. When it lands, it should initiate purchase, observe entitlement, and expose feature flags behind a free-mode fallback.

## Error handling contract

- never hard-crash because of a missing app or package
- use fallback UI for missing target actions
- validate import payloads before applying
- permission failures for call or SMS must keep the launcher usable
- billing failures must degrade gracefully to free mode
