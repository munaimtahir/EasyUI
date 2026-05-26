# EasyUI Senior Launcher - Copilot Session

- **Repo:** /home/munaim/srv/apps/easyui
- **Branch:** main
- **Sprint Title:** Root-cause stabilization sprint for settings and state management.

## Bug List

1. Apps added on additional pages are not visible on the home screen.
2. When layout is locked, a weird lock icon appears with every app/tile.
3. If PIN is not set, tapping the clock 5 times cannot enter caregiver settings.
4. Visual theme cannot be changed after first selection.
5. Onboarding menu and caregiver settings menu are completely different and inconsistent.
6. Layout options in caregiver settings do not work.
7. Font options in onboarding do not work.
8. Even after choosing call list shortcut / emergency list numbers in onboarding or caregiver settings, numbers do not show as shortcuts.

## Execution Checklist

- [ ] PHASE 0: Session Handoff File
- [ ] PHASE 1: Discovery / Truth Map
- [ ] PHASE 2: Define Canonical Settings Contract
- [ ] PHASE 3: Fix Implementation
- [ ] PHASE 4: Testing Requirements
- [ ] PHASE 5: Commands to Run
- [ ] PHASE 6: Manual / ADB Verification Script
- [ ] PHASE 7: Final Report

## Commands to Run

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `./gradlew connectedDebugAndroidTest`

## Files Inspected

- TBD

## Files Changed

- TBD

## Tests Added

- TBD

## Verification Results

- TBD

## Remaining Issues

- TBD

## Final GO/NO-GO Verdict

- TBD
