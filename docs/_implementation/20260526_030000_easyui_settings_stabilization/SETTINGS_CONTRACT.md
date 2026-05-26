# SETTINGS CONTRACT: EasyUI Canonical State

This document defines the single source of truth for all user-configurable settings in EasyUI. All new and existing code must adhere to this contract to ensure state consistency.

## 1. Canonical Settings Models

- **Primary Settings:** `com.easyui.core.domain.model.LauncherSettings` (persisted via `DataStoreLauncherSettingsRepository`). This holds most configuration values.
- **Skin & Theme:** `com.easyui.core.domain.model.SkinConfig` (also in `LauncherSettingsRepository`). This is for visual customization.
- **Home Screen Layout:** `List<com.easyui.core.domain.model.HomeTile>` (persisted via `RoomHomeLayoutRepository`). This defines the grid, pages, and all tiles (apps, contacts, actions).

There is **NO OTHER** source of truth. Caching in ViewModels is allowed, but `DataStore` and `Room` are the canonical stores.

## 2. Canonical DataStore Keys

All keys are defined in `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt`.

- `ONBOARDING_COMPLETE`: `Boolean`
- `LAYOUT_LOCKED`: `Boolean`
- `PIN_HASH_HEX`: `String?`
- `PIN_SALT_HEX`: `String?`
- `CAREGIVER_PROTECTION_ENABLED`: `Boolean`
- `EMERGENCY_PHONE_NUMBER`: `String`
- `EMERGENCY_NUMBERS`: `Set<String>`
- `SKIN_VISUAL_THEME`: `String` (enum name of `VisualTheme`)
- `SKIN_LAYOUT_MODE`: `String` (enum name of `LayoutMode`)
- `SKIN_READABILITY_PRESET`: `String` (enum name of `HomeReadabilityPreset`)
- `USE_24_HOUR_CLOCK`: `Boolean`
- ... and others as defined in the file.

## 3. Allowed Values & Defaults

| Setting             | Allowed Values (Enum) / Type      | Default on Fresh Install                        |
| ------------------- | --------------------------------- | ----------------------------------------------- |
| `visualTheme`       | `VisualTheme` enum                | `VisualTheme.DARK_COMFORT`                      |
| `layoutMode`        | `LayoutMode` enum                 | `LayoutMode.SIMPLE_CLASSIC`                     |
| `readabilityPreset` | `HomeReadabilityPreset` enum      | `HomeReadabilityPreset.STANDARD`                |
| `layoutLocked`      | `Boolean`                         | `false`                                         |
| `caregiverPin`      | `String` (4+ digits)              | `null` (no PIN)                                 |
| `emergencyNumber`   | `String`                          | `"911"`                                         |
| `homePageCount`     | `Int` (1-4)                       | `1`                                             |

**Unsupported Options:** Any UI controls for settings not listed here or marked as deprecated (e.g., layouts other than `SIMPLE_CLASSIC` if V1 is fixed) **MUST be removed or disabled**. Do not show non-functional options.

## 4. Read/Write Authority

To eliminate state conflicts, we define a strict separation of concerns:

| ViewModel                  | Write Authority                                                                                                                              | Read Authority |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| `GuidedSetupViewModel`     | **ONCE** during onboarding. May write to `LauncherSettings`, `SkinConfig`, and initial `HomeTile` layout. Sets `onboardingComplete` to `true`. | Limited (own state) |
| `CaregiverViewModel`       | **EXCLUSIVE** writer for all settings *after* onboarding. All user-driven changes from any screen must be funneled through this ViewModel.      | Full (all settings) |
| `HomeViewModel`, `AppViewModel`, `AppListViewModel` | **NO WRITE AUTHORITY**. These are read-only consumers of the state exposed by the repositories. | Full (all settings) |

## 5. Critical Scenarios & Behavior

### Onboarding
- Onboarding is the ONLY flow that uses `GuidedSetupViewModel`.
- It collects initial preferences for theme, font, and emergency contact.
- It MUST save these to the canonical `DataStore` and `Room` repositories.
- Upon completion, it sets `onboardingComplete = true`. The app will then navigate to the `HomeScreen`, and `CaregiverViewModel` becomes the settings authority.

### Caregiver Access (Clock 5-Tap)
- The gesture listener is in `HomeViewModel`.
- **`HomeViewModel` MUST NOT contain verification logic.**
- On 5 taps, it will call a method on `CaregiverViewModel` (e.g., `requestCaregiverAccess()`).
- `CaregiverViewModel` checks if a PIN is configured (`pinHashHex != null`).
  - **If PIN exists:** Navigate to `PinEntryScreen` for verification.
  - **If PIN does NOT exist:** Navigate to `PinEntryScreen` in "Create PIN" mode.
- The user must **NEVER** be locked out because a PIN was never set.

### Home Screen Rendering
- `HomeViewModel` is responsible for observing `LauncherSettingsRepository` and `HomeLayoutRepository`.
- It transforms the canonical `List<HomeTile>` and `LauncherSettings` into a `HomeUiState` for the `HomeScreen`.
- **Pagination:** The number of pages is determined by `HomeLayoutRules.effectivePageCount(tiles)`. The UI must render this many pages. If a tile's `page` property is outside this range, it's a data integrity issue that `CaregiverViewModel` must prevent during edits.
- **Layout Lock:** When `layoutLocked` is `true`, the `HomeScreen` UI MUST disable all drag-and-drop or reordering gestures. It MUST NOT show a lock icon on every individual tile. A single, global indicator (e.g., in the top bar or a toast on attempted edit) is acceptable.

### Settings Consistency (Onboarding vs. Caregiver)
- **Problem:** Onboarding and Caregiver settings are inconsistent (Bug #5).
- **Solution:**
  1.  **Single Source of UI:** Any overlapping settings UI (like Theme selection) should be extracted into a common, reusable Composable if possible.
  2.  **Single Source of Logic:** Both `GuidedSetupViewModel` and `CaregiverViewModel` must call the **exact same** repository methods. E.g., `updateVisualTheme(theme)`.
  3.  **Audit & Prune:** Review all settings in the onboarding flow. If a setting is not essential for the first-run experience, remove it from onboarding and leave it for the full `CaregiverToolsScreen`. Keep onboarding simple.

This contract is the source of truth for the stabilization sprint. All fixes in Phase 3 must align with these rules.
