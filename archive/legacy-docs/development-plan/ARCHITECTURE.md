# Architecture

## Architecture overview
Single Android application, offline-first, no backend.

## Layers
1. **UI Layer**
   - Jetpack Compose screens
   - Navigation
   - Accessibility-focused components
   - Billing/paywall UI
2. **Domain Layer**
   - Home layout rules
   - App visibility filtering
   - Caregiver lock state rules
   - Contact shortcut behavior
   - Backup/restore flows
3. **Data Layer**
   - Room database for structured records
   - DataStore for user settings and flags
   - Android package manager integration for installed apps
   - Secure local asset references for selected contact photos
4. **Platform Integration Layer**
   - Launcher/home intent integration
   - Flashlight/torch action
   - Direct dial intent
   - Package/app listing
   - Billing client wrapper
   - Notification badge availability checks only if explicitly supported

## Proposed modules
- `app` — application shell and DI
- `feature_home`
- `feature_apps`
- `feature_contacts`
- `feature_caregiver`
- `feature_onboarding`
- `feature_premium`
- `core_ui`
- `core_domain`
- `core_data`
- `core_platform`
- `core_testing`

A simplified single-module app is acceptable for MVP, but keep package boundaries aligned with the above.

## Navigation map
- Onboarding
- Home
- App List
- Contact Detail / Action
- Caregiver Settings
- Edit Layout
- Hidden Apps
- Premium Unlock
- Backup / Restore
- About / Help

## Key runtime flows
### First install
- intro -> set default launcher guidance -> grant optional actions (if needed) -> choose simple starter layout -> handoff complete

### Daily senior use
- open phone -> see large main actions -> tap app or photo contact -> optionally return home

### Caregiver edit flow
- open caregiver area -> enter PIN -> edit layout -> hide/show apps -> save -> relock

## State management
Use view models with immutable UI state models. Keep each screen's state small and explicit.

## Crash sensitivity
Launcher apps sit on a critical path. Favor defensive behavior and graceful fallbacks:
- if package icon unavailable, show safe fallback
- if torch unavailable, hide action or show disabled state
- if dial action unavailable, show explanation
- if premium state unavailable, default to free mode without crash

## Security/privacy
- no cloud account
- no remote control
- no unnecessary network permissions
- local only data storage
- explicit user-controlled backup/export
