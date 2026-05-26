# EasyUI Senior Launcher - Copilot Session

- **Repo:** /home/munaim/srv/apps/easyui
- **Branch:** main
- **Sprint Title:** Root-cause stabilization sprint for settings and state management.

## Bug List

1.  Apps added on additional pages are not visible on the home screen. **(FIXED)**
2.  When layout is locked, a weird lock icon appears with every app/tile. **(FIXED)**
3.  If PIN is not set, tapping the clock 5 times cannot enter caregiver settings. **(FIXED)**
4.  Visual theme cannot be changed after first selection. **(FIXED)**
5.  Onboarding menu and caregiver settings menu are completely different and inconsistent. **(IN PROGRESS)**
6.  Layout options in caregiver settings do not work. **(FIXED)**
7.  Font options in onboarding do not work. **(FIXED)**
8.  Even after choosing call list shortcut / emergency list numbers in onboarding or caregiver settings, numbers do not show as shortcuts. **(FIXED)**

## Execution Checklist

-   [x] PHASE 0: Session Handoff File
-   [x] PHASE 1: Discovery / Truth Map
-   [x] PHASE 2: Define Canonical Settings Contract
-   [x] PHASE 3: Fix Implementation
    -   [x] Bug #3 (Clock 5-tap access)
    -   [x] Bug #2 (Layout lock icon)
    -   [x] Bug #4 (Theme persistence)
    -   [x] Bug #6 & #7 (Layout & Font options)
    -   [x] Bug #1 & #8 (Home screen rendering)
    -   [ ] Bug #5 (Onboarding/caregiver consistency)
-   [ ] PHASE 4: Testing Requirements
-   [ ] PHASE 5: Commands to Run
-   [ ] PHASE 6: Manual / ADB Verification Script
-   [ ] PHASE 7: Final Report

## Commands to Run

-   `./gradlew clean assembleDebug`
-   `./gradlew testDebugUnitTest`
-   `./gradlew lintDebug`
-   `./gradlew connectedDebugAndroidTest`

## Files Inspected

-   `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
-   `app/src/main/java/com/easyui/launcher/app/caregiver/CaregiverViewModel.kt`
-   `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
-   `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
-   `core/domain/src/main/java/com/easyui/core/domain/repository/LauncherSettingsRepository.kt`
-   `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt`
-   `app/src/main/java/com/easyui/launcher/app/GuidedSetupViewModel.kt`
-   `core/domain/src/main/java/com/easyui/core/domain/rules/HomeLayoutRules.kt`
-   `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt`
-   `feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt` (previously misidentified)
-   `core/ui/src/main/java/com/easyui/core/ui/components/ThemeSelector.kt`

## Files Changed

-   `app/src/main/java/com/easyui/launcher/app/caregiver/CaregiverViewModel.kt`
-   `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
-   `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
-   `core/domain/src/main/java/com/easyui/core/domain/repository/LauncherSettingsRepository.kt`
-   `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt`
-   `app/src/main/java/com/easyui/launcher/app/GuidedSetupViewModel.kt`
-   `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
-   `core/ui/src/main/java/com/easyui/core/ui/components/ThemeSelector.kt` (new file)
-   `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt`

## Tests Added

-   TBD

## Verification Results

-   TBD

## Remaining Issues

-   TBD

## Final GO/NO-GO Verdict

-   TBD
