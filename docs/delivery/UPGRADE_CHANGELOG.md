# EasyUI Upgrade Changelog

## Summary

This upgrade focuses on caregiver-first discoverability, anchored home essentials, clearer surface separation (`Home Apps` vs `All Apps`), and a new offline `Health Info` flow while preserving existing architecture and offline-first constraints.

## What changed

## 1) Caregiver entry and protection flow

- Added a practical caregiver entry button on home (`Caregiver`) while keeping the hidden header multi-tap fallback.
- Kept and reused existing PIN/session protection flow (`CaregiverViewModel`, protected routes).
- Updated onboarding caregiver guidance copy to explain how to re-enter caregiver settings and what PIN protects.

## 2) Home essentials and fixed layout model

- Expanded anchored required home actions in `HomeLayoutRules`:
  - `Phone`
  - `All Apps`
  - `Emergency`
  - `Camera`
  - `Health Info`
  - `Flashlight`
- Reserved these positions so app assignment cannot displace essentials.
- Updated starter layout logic to keep essentials fixed and place one caregiver-configurable app in the next available slot (`Page 2, Slot 1`) when available.
- Updated default page count to `2` for new installs/settings defaults to preserve a clear essentials page and a separate Home Apps area.

## 3) Health Info (offline local)

- Added local `HealthInfo` domain model.
- Added persistence via existing DataStore settings repository.
- Added senior-facing `HealthInfoScreen` reachable from the new home `Health Info` tile.
- Added caregiver `HealthInfoEditorScreen` in Caregiver Settings for offline editing.
- Wired health info through backup export/import settings payload.

## 4) Dialer, emergency, camera, and battery surfacing

- Kept dialer/emergency behavior on safe `ACTION_DIAL` paths; emergency opens dialer with configured number.
- Added a camera action handler (`AndroidCameraActionHandler`) and home camera tile behavior with graceful fallback.
- Kept battery home visibility toggle and retained home header battery summary display, now with improved header hierarchy.

## 5) Visual polish and caregiver language

- Refined home header visual hierarchy and card styling.
- Enhanced action tile visual polish (borders/elevation).
- Renamed caregiver wording to better mental model:
  - `Allowed Apps` -> `Home Apps` (in caregiver settings section wording)
  - explicit distinction from `All Apps`
- Added a dedicated `Health Info` section in caregiver tools.

## 6) Build stability fixes discovered during baseline

- Fixed existing compile blockers found during baseline:
  - replaced invalid `hidePackage/unhidePackage` calls with `setHidden(package, bool)`
  - added missing `HiddenAppsScreen` import in nav graph

## 7) Tests updated

- Updated home UI smoke test for visible caregiver entry plus fallback gesture.
- Updated caregiver smoke tests for updated `CaregiverToolsScreen` parameters.
- Updated `HomeLayoutRulesTest` for new required essentials and fixed-slot app assignment constraints.
