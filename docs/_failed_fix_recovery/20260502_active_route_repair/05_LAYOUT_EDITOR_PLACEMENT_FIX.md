## Layout Editor App Placement (2026-05-02)

### User-reported issue
“Selecting a slot and then placing an app does not place the app.”

### Active editor surfaces
There are two relevant active UI surfaces:
1) Guided setup allowed apps placement:
   - `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` → `AllowedAppsSetupScreen`
   - Routed from `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` step `8`

2) Caregiver “Allowed Apps” placement:
   - `feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt` → `AllowedAppsScreen`
   - Routed from `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` route `Routes.AllowedApps`

Both flows use the same placement operation:
- `app/src/main/java/com/easyui/launcher/app/caregiver/CaregiverViewModel.kt` → `assignAllowedApp(packageName, position)`
- Rules: `core/domain/src/main/java/com/easyui/core/domain/rules/HomeLayoutRules.kt` → `assignAppToPosition(...)`

### What changed in this sprint
- Added an instrumentation regression test that selects a known empty slot and confirms the “Place Here” action calls `onAssignApp` with the expected absolute position:
  - `app/src/androidTest/java/com/easyui/launcher/caregiver/CaregiverQolSmokeTest.kt` → `allowedAppsScreenPlacesIntoSelectedSlot`

### Status
- Code path is confirmed (active routes identified).
- Placement behavior is covered by an automated UI regression test.

### NOT TESTED
- `NOT RUN — requires local ADB` for end-to-end device validation (install → place app → return home → verify tile).

