# EasyUI Upgrade Findings

## Scope of inspection

Reviewed canonical product and engineering docs, launcher navigation, home UI, caregiver flow, persistence layers (Room/DataStore), platform action wrappers, and current smoke/unit tests.

Primary files reviewed include:

- `README.md`
- `docs/product/*` and `docs/engineering/*` (canonical docs set)
- `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
- `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt`
- `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
- `feature/caregiver/src/main/java/com/easyui/feature/caregiver/CaregiverScreens.kt`
- `feature/onboarding/src/main/java/com/easyui/feature/onboarding/OnboardingScreens.kt`
- `core/domain/src/main/java/com/easyui/core/domain/rules/HomeLayoutRules.kt`
- `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt`
- `core/platform/src/main/java/com/easyui/core/platform/actions/*`

## Existing features found

- Launcher replacement onboarding flow exists: intro, default launcher guidance, caregiver help.
- Home supports large tiles, paging (`1..3`), clock/date header, and optional battery summary.
- Hidden caregiver access exists via repeated taps on the home header.
- Caregiver tools hub exists with:
  - PIN setup/verification and protection toggle
  - layout/page controls
  - allowed apps placement by fixed slots
  - hidden apps management
  - emergency number settings
  - backup/restore and reset
- Core actions implemented:
  - dialer launch via safe `ACTION_DIAL`
  - emergency tile opens dialer with configured number
  - flashlight toggle with graceful fallback
- Home layout is persisted with Room and normalized with `HomeLayoutRules`.
- Launcher settings and hidden apps are persisted via DataStore.
- Starter layout and required actions are enforced (`Phone`, `All Apps`, `Flashlight`).

## Partially complete features

- Caregiver access protection is complete technically, but discoverability is weak (primary path still hidden repeated-tap gesture).
- Battery visibility logic exists and is toggleable in caregiver settings, but home surfacing is minimal and visually low-priority.
- Allowed-apps model exists and fixed-slot placement works, but terminology and separation from `All Apps` can be clearer for caregivers.
- Multi-page support exists and is bounded, but default behavior does not strongly present predictable caregiver-first essentials.
- Emergency flow is safe (prefilled dialer), but copy currently says “dials this number” in some places which can imply direct-call behavior.

## Missing features

- No Health Info data model, caregiver editor, or senior-facing Health Info screen.
- No anchored Camera or anchored Emergency essential tile in required home action set.
- No practical, visible, discreet caregiver entry affordance on home (only hidden tap sequence).
- No explicit caregiver re-entry explanation in onboarding/help beyond generic copy.
- No clear “Home Apps vs All Apps vs Hidden Apps” language consistency across onboarding/help/surfaces.

## UX and discoverability issues

- Home caregiver entry is intentionally hidden but not practical for real caregiver re-entry.
- Home header and tile styling are still MVP-basic; visual hierarchy can be calmer and clearer.
- Essential home actions are not fully anchored to the product-default mental model (Phone, Emergency, Camera, Flashlight, Health Info, All Apps).
- Caregiver copy is mostly good, but several labels are still implementation-centric and not fully caregiver-guided.
- `All Apps` is present, but distinction from promoted home apps is not consistently reinforced.

## Technical risks

- Any extension of `HomeTileAction` or required action set can affect:
  - starter layout generation
  - `ensureRequiredActions` behavior
  - tile click handling in `HomeViewModel`
  - backup serialization/deserialization of tile actions
- Adding persisted Health Info requires careful local storage choice and backup inclusion rules.
- If home default layout semantics change, tests around required tiles and caregiver screens will need synchronized updates.
- Instrumentation smoke tests appear partially stale versus current caregiver screen function signatures and may fail baseline in CI/local verification.

## Reuse recommendations

- Reuse existing protection/session flow in `CaregiverViewModel` (do not rewrite PIN logic).
- Extend `HomeLayoutRules.requiredActionTiles` instead of introducing a second layout system.
- Reuse existing `EmergencyActionHandler` dialer-safe path for phone and emergency behavior.
- Reuse `LauncherSettingsDataStore` pattern for any lightweight new settings state.
- Reuse `LargeActionTile`, `SectionCard`, and existing fallback messaging strategy while improving styling.
- Reuse navigation structure in `EasyUiNavGraph` and add minimal new routes for health and caregiver entry clarity.
- Reuse backup infrastructure (`BackupSerializer`, `LocalBackupRepository`) if health data is persisted and should be portable.
