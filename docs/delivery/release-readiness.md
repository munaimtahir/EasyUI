# Play Release Readiness

## Scope

This document reflects the current in-repo application only: `EasyUI Senior Launcher`.

## Repository-backed release summary

### App identity

- Application ID: `com.easyui.launcher`
- Debug package: `com.easyui.launcher.debug`
- `minSdk`: 26
- `targetSdk`: 35
- Launcher activity: `com.easyui.launcher.MainActivity`

### Current product shape

- consumer Android launcher
- offline-first
- no account requirement
- caregiver-assisted setup
- current build includes:
  - onboarding
  - fixed essentials home
  - hidden caregiver access
  - caregiver PIN and layout lock
  - hidden apps
  - favorite contacts
  - Health Info
  - SOS
  - backup export/import
- current build does not yet include:
  - live billing
  - premium entitlement restore
  - a senior-facing home entry to the app-list surface

### Manifest and policy-sensitive behavior

- exported activity: one launcher activity
- launcher categories:
  - `MAIN`
  - `HOME`
  - `DEFAULT`
- declared permissions:
  - `android.permission.SEND_SMS`
  - `android.permission.CALL_PHONE`
- optional hardware feature:
  - `android.hardware.camera.flash` with `required="false"`
- Android automatic backup is disabled in favor of app-owned export/import

### Data posture

Stored locally on device:

- onboarding state
- home layout
- caregiver settings
- hidden app choices
- emergency and SOS numbers
- Health Info
- contact shortcuts and photo URI references
- caregiver PIN hash and salt

Not observed in the current repo:

- analytics SDKs
- ad SDKs
- crash reporting SDKs
- backend APIs
- account login
- remote caregiver control

## Release strengths

- honest scope for a consumer launcher
- no backend dependency
- local backup and restore
- strong caregiver safety tooling
- current device suite coverage across onboarding, home, caregiver, SOS-adjacent flows, and offline behavior
- latest full retained device verification passed on March 20, 2026:
  - `15 passed (5.3m)`
  - artifacts: `/home/munaim/Documents/github/easyui/device_test_runs/20260320_025455`

## Release blockers

- release signing and upload keystore are still manual
- Play listing assets still need final exported artwork and screenshots
- privacy policy must be hosted at a stable public URL
- Play Console Data Safety and content-rating answers still require human submission
- premium docs must remain aspirational only until billing is actually connected

## Release cautions

- Store copy must not imply kiosk mode or OS-level lockdown.
- Reviewer notes should explain why `SEND_SMS` and `CALL_PHONE` exist:
  - SOS can send messages to configured SOS numbers
  - SOS may attempt a direct call to the primary SOS number when permission is granted
- The app-list entry gap must not be marketed as fully available from home.
- Release packaging now succeeds with signing, code shrinking, and resource shrinking after upgrading the Android Gradle Plugin from `8.5.2` to `8.6.1`.

## Recommended release stance

The app is close to Play-ready from a product and QA perspective, but it is not fully submission-ready until the manual release items above are completed.
