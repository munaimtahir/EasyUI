# SETTINGS CONTRACT: EasyUI Canonical State

This contract defines the single source of truth for the launcher after the stabilization sprint.

## 1. Canonical Models

- `LauncherSettings` is the canonical DataStore-backed settings model.
- `SkinConfig` is the canonical visual configuration model exposed through `LauncherSettingsRepository`.
- `HomeTile` lists in `RoomHomeLayoutRepository` are the canonical home layout and shortcut model.

No screen may invent a parallel settings source for the same behavior.

## 2. Canonical Keys

The key names below are the canonical persistence fields used by the repository layer:

- `ONBOARDING_COMPLETE`
- `LAYOUT_LOCKED`
- `PIN_HASH_HEX`
- `PIN_SALT_HEX`
- `CAREGIVER_PROTECTION_ENABLED`
- `EMERGENCY_PHONE_NUMBER`
- `EMERGENCY_NUMBERS`
- `SKIN_VISUAL_THEME`
- `SKIN_LAYOUT_MODE`
- `SKIN_READABILITY_PRESET`
- `SKIN_FONT_SCALE_FACTOR`
- `SKIN_FONT_SIZE_MODE`
- `USE_24_HOUR_CLOCK`

## 3. Allowed Values

| Setting | Allowed Values | Fresh Install Default |
| --- | --- | --- |
| `visualTheme` | `VisualTheme` enum | `VisualTheme.DARK_COMFORT` |
| `layoutMode` | Supported launcher `LayoutMode` values only | Supported fixed V1 layout |
| `readabilityPreset` | `HomeReadabilityPreset` enum | `HomeReadabilityPreset.STANDARD` |
| `layoutLocked` | `Boolean` | `false` |
| `caregiverPin` | `null` or a user-created PIN hash | `null` |
| `emergencyNumber` | Valid dialable string or `null` | `911` |
| `homePageCount` | `Int` supported by the home tile model | `1` |

Unsupported options must not be shown as fake controls. If the current V1 layout does not support a choice, the UI must remove it or disable it with a plain explanation.

## 4. Write Authority

| Screen / ViewModel | May Write | May Read |
| --- | --- | --- |
| `GuidedSetupViewModel` | Initial onboarding settings only | Yes |
| `CaregiverViewModel` | All caregiver-managed settings after onboarding | Yes |
| `HomeViewModel` | No | Yes |
| `AppViewModel` | No | Yes |
| `AppListViewModel` | No | Yes |

The same repository methods must be used for overlapping values in onboarding and caregiver settings.

## 5. Behavior Rules

### Onboarding

- Onboarding must stay minimal.
- It can configure the values necessary for first launch: theme, readability, initial apps/contacts, and any required safety defaults.
- It must write to the same canonical repository APIs that caregiver settings use later.

### Caregiver Access

- The 5-tap clock gesture must always open caregiver access.
- If a PIN exists, show PIN verification.
- If no PIN exists, show PIN creation.
- The user must never be locked out because a PIN was never created.

### Home Rendering

- `HomeViewModel` observes the canonical repositories and produces the home UI state.
- It must render all configured pages and slots that are supported by the current design.
- If a layout is locked, editing is disabled, but the UI should not decorate every tile with a lock icon.

### Theme / Readability / App Placement

- Theme changes persist through navigation, recomposition, and resume.
- Readability choices affect senior-facing UI text and spacing.
- App placement uses the shared grid component and the Room tile model.

### Unsupported Choices

- Do not expose non-functional layout modes or dead onboarding controls.
- If a control cannot affect the actual launcher state, remove it or disable it with a clear explanation.

## 6. Summary

The contract is intentionally narrow:

- one repository path for settings
- one repository path for home tiles
- shared UI for overlapping configuration surfaces
- read-only home rendering

That is the stabilized model the app now uses.
