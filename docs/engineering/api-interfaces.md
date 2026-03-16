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

### Direct dial or call actions

Use standard intents. Request only the minimum permissions required by the chosen implementation path.

### Flashlight

Use CameraManager torch APIs when available. If unsupported, hide or disable safely.

### Contacts

If direct device-contact integration is used, handle permissions carefully and provide a local-entry fallback.

### Billing

Google Play Billing Library:

- one-time non-consumable premium product
- local entitlement cache
- testable abstraction

## Internal interfaces

### AppCatalogRepository

- load installed launchable apps
- search and sort apps
- expose filtered visible apps

### HomeLayoutRepository

- read and write home tiles
- reorder tiles
- validate tile positions
- restore defaults

### CaregiverSettingsRepository

- lock state
- PIN state
- theme and visibility preferences
- premium state cache

### ContactsRepository

- CRUD photo contacts
- local photo reference handling

### BackupService

- export config payload
- import config payload
- validate version and integrity

### PremiumService

- initiate purchase
- observe entitlement
- expose feature flags

## Error handling contract

- never hard-crash because of a missing app or package
- use fallback UI for missing target actions
- validate import payloads before applying
- billing failures must degrade gracefully to free mode
