# TRUTH MAP: EasyUI Settings & State

This map reflects the stabilized architecture after the sprint fixes. The goal is a single canonical settings model with shared UI for overlapping controls and one read path for the senior home screen.

| Setting / Feature | Written By | Stored In | Read By | Rendered In | Bug Risk | Fix Needed |
| --- | --- | --- | --- | --- | --- | --- |
| `selectedTheme` | `GuidedSetupViewModel`, `CaregiverViewModel` | `LauncherSettingsRepository` / DataStore key `SKIN_VISUAL_THEME` | `AppViewModel`, `EasyUiNavGraph` | `EasyUiTheme` across onboarding, caregiver, and home | Low | Shared `ThemeSelector` and `updateVisualTheme(theme)` are wired; keep this as the only theme write path. |
| `fontScale` / `readabilityPreset` | `GuidedSetupViewModel`, `CaregiverViewModel` | DataStore keys `SKIN_READABILITY_PRESET`, `SKIN_FONT_SCALE_FACTOR`, `SKIN_FONT_SIZE_MODE` | `HomeViewModel`, senior-facing UI theme tokens | Home screen and onboarding readability surfaces | Low | Shared `ReadabilityPresetSelector` is wired; keep senior-facing text tied to the canonical preset. |
| `layoutMode` / `gridMode` | `GuidedSetupViewModel`, `CaregiverViewModel` | DataStore key `SKIN_LAYOUT_MODE` | `HomeViewModel` and layout rules | Senior home layout composition | Low | Unsupported layout choices are pruned or routed through the supported fixed design. |
| `home pages / tile slots` | `CaregiverViewModel` | Room `home_tiles` table via `RoomHomeLayoutRepository` | `HomeViewModel` | `HomeScreen` pager and tile grid | Low | `HomeViewModel` now flattens all configured pages into rendered state. |
| `selected / allowed apps` | `CaregiverViewModel` and onboarding setup | Room `home_tiles` table | `HomeViewModel` | Home tiles / app slots | Low | Shared `AppSelectionGrid` is used in onboarding and caregiver settings. |
| `hidden apps` | `CaregiverViewModel` | DataStore / repository-backed hidden-app state | `AppListViewModel` | Secondary app list | Low | Kept as a caregiver-only management feature. |
| `layoutLocked` | `GuidedSetupViewModel`, `CaregiverViewModel` | DataStore key `LAYOUT_LOCKED` | `HomeViewModel`, caregiver UI | Home edit behavior and a global lock state | Low | Lock state blocks editing without polluting every tile. |
| `caregiverPinHash` / `pinConfigured` | `GuidedSetupViewModel`, `CaregiverViewModel` | DataStore keys `PIN_HASH_HEX`, `PIN_SALT_HEX` | `HomeViewModel`, `CaregiverViewModel` | Clock 5-tap access flow and PIN entry/create screen | Low | No-PIN path now routes to create PIN; existing PIN path routes to verification. |
| `onboardingCompleted` | `GuidedSetupViewModel`, bootstrap flow | DataStore key `ONBOARDING_COMPLETE` | `EasyUiNavGraph` | Startup routing | Low | Onboarding completion remains the navigation gate only. |
| `emergencyContact(s)` | `GuidedSetupViewModel`, `CaregiverViewModel` | DataStore keys such as `EMERGENCY_PHONE_NUMBER`, `EMERGENCY_NUMBERS` | `HomeViewModel` | Emergency shortcut area on home | Low | Selected emergency number now renders as a visible home shortcut. |
| `directCallShortcut(s)` | `CaregiverViewModel` | Room `home_tiles` entries of type `CONTACT` | `HomeViewModel` | Contact / direct-call tiles on home | Low | Contact tiles are rendered from the canonical home tile list. |
| `shortcut/contact list mode` | `GuidedSetupViewModel`, `CaregiverViewModel` | Repository-backed settings + Room tile model | `HomeViewModel` | Contact shortcuts area and caregiver screens | Low | Shared canonical behavior is preserved across onboarding and caregiver settings. |

## Summary

- One canonical settings contract now exists.
- Onboarding and caregiver settings write through the same repository APIs for overlapping values.
- The home screen reads from the same canonical state and renders all configured pages, tiles, and shortcut types.
- Shared UI components are used for theme selection, readability selection, and app placement.
