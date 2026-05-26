# EasyUI Senior Launcher - Sprint Work Plan

## Overview
This document outlines the complete plan for the EasyUI Senior Launcher stabilization sprint, tracking progress on critical user-reported bugs and architectural improvements.

**Last Updated:** May 26, 2026

## Critical User-Reported Bugs to Fix and Verify

1.  Apps added on additional pages are not visible on the home screen.
2.  When layout is locked, a weird lock icon appears with every app/tile.
3.  If PIN is not set, tapping the clock 5 times cannot enter caregiver settings.
4.  Visual theme cannot be changed after first selection.
5.  Onboarding menu and caregiver settings menu are completely different and inconsistent.
6.  Layout options in caregiver settings do not work.
7.  Font options in onboarding do not work.
8.  Even after choosing call list shortcut / emergency list numbers in onboarding or caregiver settings, numbers do not show as shortcuts.

---

## Work Plan by Phase

### PHASE 0 — SESSION HANDOFF FILE
- [x] Create or overwrite `copilot_session.md` at root.
  - *Status:* Completed. File created with initial project context, bug list, and execution checklist.

### PHASE 1 — DISCOVERY / TRUTH MAP
- [x] Inspect repository to find files related to: onboarding, caregiver settings, clock tap access, PIN, theme, font size, layout, layout lock, app selection, home screen renderer, home paging, contact/emergency shortcuts, settings repository/datastore, ViewModels.
- [x] Produce `docs/_implementation/20260526_030000_easyui_settings_stabilization/TRUTH_MAP.md`.
  - *Status:* Completed. Comprehensive truth map created, identifying key files and potential bug risks.

### PHASE 2 — DEFINE CANONICAL SETTINGS CONTRACT
- [x] Create or update `docs/_implementation/20260526_030000_easyui_settings_stabilization/SETTINGS_CONTRACT.md`.
  - *Status:* Completed. Contract defines canonical models, keys, allowed values, read/write authority, and behavior for critical scenarios.

### PHASE 3 — FIX IMPLEMENTATION

#### Bug Fixes:

-   **Bug #3: Clock 5-tap caregiver entry with no PIN**
    -   [x] Modify `CaregiverViewModel.kt`: Centralize PIN logic, route to `PinSetup.route` if no PIN.
    -   [x] Modify `EasyUiNavGraph.kt`: Update `PinEntryScreen` `onSubmit` handler to navigate correctly after PIN setup.
    -   *Status:* Completed.

-   **Bug #2: Layout lock icon pollutes every tile**
    -   [x] Modify `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`: Remove per-tile lock icon rendering logic from `HomeActionTile`.
    -   *Status:* Completed.

-   **Bug #4: Visual theme cannot be changed after first selection**
    -   [x] Add `updateVisualTheme(theme: VisualTheme)` to `LauncherSettingsRepository` interface.
    -   [x] Implement `updateVisualTheme` in `DataStoreLauncherSettingsRepository` (atomic read-modify-write).
    -   [x] Modify `CaregiverViewModel.kt` to use `launcherSettingsRepository.updateVisualTheme()`.
    -   [x] Modify `GuidedSetupViewModel.kt` to use `launcherSettingsRepository.updateVisualTheme()`.
    -   *Status:* Completed.

-   **Bug #6: Layout options in caregiver settings do not work** & **Bug #7: Font options in onboarding do not work**
    -   [x] Add `updateLayoutMode(mode: LayoutMode)` and `updateReadabilityPreset(preset: HomeReadabilityPreset)` to `LauncherSettingsRepository` interface.
    -   [x] Implement `updateLayoutMode` and `updateReadabilityPreset` in `DataStoreLauncherSettingsRepository` (atomic read-modify-write).
    -   [x] Modify `CaregiverViewModel.kt`: `updateHomeReadabilityPreset` and `updateSkinLayoutMode` to use new repository methods.
    -   [x] Modify `GuidedSetupViewModel.kt`: `updateReadabilityPreset` to use new repository method.
    -   *Status:* Completed.

-   **Bug #1: Apps added on additional pages are not visible on the home screen** & **Bug #8: Contact/emergency shortcuts do not show as shortcuts**
    -   [x] Modify `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`: Update home screen state construction to use flattened list of all tiles from `renderPages` and `effectivePageCount`.
    -   [x] Remove unused `primaryTiles` function from `HomeViewModel.kt`.
    -   *Status:* Completed.

-   **Bug #5: Onboarding menu and caregiver settings menu are completely different and inconsistent**
    -   [x] **Extract Common UI for Theme Selection:** Create `core/ui/src/main/java/com/easyui/core/ui/components/ThemeSelector.kt`.
    -   [x] **Integrate ThemeSelector into Onboarding:** Modify `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` to use `ThemeSelector`.
    -   [x] **Integrate ThemeSelector into Caregiver Settings (Part 1):**
        -   [x] Modify `feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt`: Remove embedded theme selection from `LayoutPagesScreen`.
        -   [x] Add `onOpenThemeSelection` callback to `LayoutPagesScreen` signature.
        -   [x] Add a button to `LayoutPagesScreen` that invokes `onOpenThemeSelection`.
        -   [x] Remove `visualThemeLabel` and `accessibilityModeLabel` functions from `CaregiverScreens.kt`.
    -   [ ] **Integrate ThemeSelector into Caregiver Settings (Part 2):**
        -   [ ] Add new `composable` for dedicated theme selection screen in `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` that uses `ThemeSelector`.
        -   [ ] Update `CaregiverToolsScreen` to navigate to the new theme selection screen.
    -   [ ] **Extract Common UI for Readability Preset:** Create reusable component for `HomeReadabilityPreset` selection.
    -   [ ] **Consolidate Layout Page Count and Layout Mode:** Simplify `HomeLayoutSetupScreen` in onboarding.
    -   [ ] **Consolidate Allowed Apps UI:** Create a shared `AppSelectionGrid` component.
    -   [ ] **Simplify Onboarding PIN/Security:** Audit `SecuritySetupScreen` and `CaregiverToolsScreen`.
    -   [ ] **Simplify Onboarding Contacts/Emergency:** Reduce scope of `ContactsSetupScreen` in onboarding.
    -   [ ] **Remove/Disable Unwired/Duplicate Onboarding Options:** Final cleanup of onboarding.
    -   *Status:* In progress (Theme Selector integration into Caregiver Settings).

---

### PHASE 4 — TESTING REQUIREMENTS
- [ ] Add or repair tests for all fixed bugs, covering:
    - [ ] Fresh install / no PIN: clock 5 taps opens create PIN flow.
    - [ ] PIN configured: clock 5 taps opens PIN entry; correct PIN opens caregiver settings.
    - [ ] Theme change: persists after navigation/resume/recomposition.
    - [ ] Font change: updates senior UI state/rendering.
    - [ ] Layout lock: prevents editing/reordering; no per-tile lock icon pollution.
    - [ ] Home app pages: apps assigned to visible slots/pages appear; no invisible configured apps.
    - [ ] Contacts/direct calls: selected emergency/contact shortcuts render; configured number used for action.
    - [ ] Onboarding/caregiver consistency: overlapping controls write same canonical setting; no dead onboarding controls.
- *Status:* Not started.

### PHASE 5 — COMMANDS TO RUN
- [ ] Execute build and test commands:
    - [ ] `./gradlew clean assembleDebug`
    - [ ] `./gradlew testDebugUnitTest`
    - [ ] `./gradlew lintDebug`
    - [ ] `./gradlew connectedDebugAndroidTest`
- [ ] Inspect GitHub Actions workflows if present.
- *Status:* Not started.

### PHASE 6 — MANUAL / ADB VERIFICATION SCRIPT
- [ ] Create `docs/_implementation/20260526_030000_easyui_settings_stabilization/MANUAL_ADB_VERIFICATION.md`.
- [ ] Detail exact steps and ADB commands for verifying all fixes.
- [ ] Specify evidence collection (screenshots/logs) in `docs/_implementation/20260526_030000_easyui_settings_stabilization/evidence/`.
- *Status:* Not started.

### PHASE 7 — FINAL REPORT
- [ ] Create `docs/_implementation/20260526_030000_easyui_settings_stabilization/FINAL_REPORT.md`.
- [ ] Include executive verdict, root cause summary, bugs fixed, files changed, tests, verification results, remaining risks, and rebuild recommendation.
- [ ] Ensure all 8 listed bugs are fixed or honestly classified with evidence.
- [ ] Ensure onboarding and caregiver settings no longer conflict.
- [ ] Ensure home screen reflects saved app/theme/font/contact/layout settings.
- [ ] Ensure PIN-not-set flow is fixed.
- [ ] Ensure layout lock no longer pollutes every tile.
- [ ] Ensure dead/non-working controls are removed, disabled, or wired.
- [ ] Ensure tests pass.
- [ ] Ensure evidence report is produced.
- [ ] Ensure `copilot_session.md` is updated.
- *Status:* Not started.
