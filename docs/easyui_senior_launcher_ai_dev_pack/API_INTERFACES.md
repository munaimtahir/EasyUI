# API and Interfaces

## External APIs / platform integrations

### Android launcher integration
- Intent filters for `ACTION_MAIN`, `CATEGORY_HOME`, `CATEGORY_DEFAULT`
- Clear onboarding to set as default launcher

### Package manager
Used to:
- enumerate launchable apps
- fetch labels and icons
- detect uninstall/install changes

### Direct dial / call actions
Use standard intents. Respect platform restrictions and request only necessary permissions for chosen implementation path.

### Flashlight / torch
Use camera manager torch APIs when available. If unsupported, hide or disable safely.

### Contacts
If photo contacts are implemented via direct entry only, keep permissions minimal.
If integrating device contacts directly, handle permission flow carefully and keep fallback manual entry path.

### Billing
Google Play Billing Library:
- one-time non-consumable product for premium unlock
- local entitlement cache
- testable abstraction

## Internal interfaces

### AppCatalogRepository
Responsibilities:
- load installed launchable apps
- search/sort apps
- expose filtered visible apps

### HomeLayoutRepository
Responsibilities:
- read/write home tiles
- reorder tiles
- validate tile positions
- restore defaults

### CaregiverSettingsRepository
Responsibilities:
- lock state
- PIN state
- theme and visibility preferences
- premium state cache

### ContactsRepository
Responsibilities:
- CRUD photo contacts
- local photo reference handling

### BackupService
Responsibilities:
- export config payload
- import config payload
- validate version and integrity

### PremiumService
Responsibilities:
- initiate purchase
- observe entitlement
- expose feature flags

## Error handling contract
- never hard crash because of missing package/app
- use fallback UI for missing target actions
- import must validate payload before applying
- billing failures must degrade gracefully to free mode
