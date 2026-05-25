# EasyUI Settings Contract (Canonical)

Timestamp: 2026-05-25T23:23:39Z  
Purpose: Define the single, canonical settings model and the allowed writers/readers so onboarding, caregiver settings, and home rendering remain consistent.

## 1. Canonical Settings Model

**Primary model:** `core/domain/model/LauncherSettings.kt`  
**Auxiliary layout model:** `core/domain/model/HomeTile` stored via `HomeLayoutRepository` (Room).

LauncherSettings is the single source of truth for preferences and global configuration. Home layout and contact/allowed-app shortcuts are **not** stored inside LauncherSettings; they are stored in Room (`home_tiles`) and referenced by HomeLayoutRules.

## 2. Canonical DataStore Keys

Stored in `core/data/datastore/LauncherSettingsDataStore.kt`:

- `onboarding_complete`
- `guided_setup_step`
- `guided_setup_completed`
- `emergency_phone_number`
- `emergency_numbers`
- `sos_numbers`
- `emergency_mode`
- `use_24_hour_clock`
- `caregiver_protection_enabled`
- `layout_locked`
- `pin_salt_hex`
- `pin_hash_hex`
- `home_readability_preset`
- `skin_layout_mode`
- `skin_visual_theme`
- `skin_accessibility_mode`
- `home_page_count`
- `all_apps_visible`
_plus guardian health check toggles/thresholds and health info fields_

## 3. Allowed Values

**Theme (`SkinConfig.visualTheme`)**
- `LIGHT_PREMIUM` (label: Light)
- `DARK_COMFORT` (label: Dark)
- `AUTO` (label: Auto / Follow system)
- `CLINICAL_PROFESSIONAL` (caregiver-only)
- `SOFT_CALM` (caregiver-only)

**Accessibility (`SkinConfig.accessibilityMode`)**
- `NONE`
- `HIGH_CONTRAST`
- `BOLD_ACCESSIBILITY` (caregiver-only)

**Font / Readability (`home_readability_preset`, `SkinConfig.readabilityPreset`)**
- `STANDARD`
- `LARGER_TEXT`
- `LARGER_TILES`
- `EXTRA_SIMPLE_SPACING`

**Layout mode (`SkinConfig.layoutMode`)**
- **Supported:** `SIMPLE_CLASSIC`, `VERY_SIMPLE`
- **Unsupported (hide/disable):** `CARE_MODE`, `COMMUNICATION_MODE`

**App visibility**
- Home tiles are stored in Room. Page count lives in settings (`home_page_count`).
- Hidden apps stored in `hidden_packages` (DataStoreHiddenAppRepository).

**Layout lock**
- `layout_locked` boolean. Locks editing only; no per-tile lock icon.

**Contact shortcut mode / Emergency mode**
- `emergency_mode` = `"MENU"` or `"SOS"`
- `emergency_phone_number` used by SOS
- Contact tiles stored as `HomeTileType.CONTACT` in Room

## 4. Writers vs Readers

**Onboarding (GuidedSetupViewModel + GuidedSetupScreens)**
- Writes: theme, accessibility, readability, page count, emergency mode/number, layout lock, PIN (optional), onboarding completion
- Reads: settings via `LauncherSettingsRepository.settings`

**Caregiver Settings (CaregiverViewModel + CaregiverScreens)**
- Writes: theme, accessibility, readability, page count, allowed apps, contacts, emergency settings, layout lock, PIN
- Reads: settings + layout tiles + hidden apps

**Senior Home (HomeViewModel + HomeScreen)**
- Reads only: settings + layout tiles; never writes

## 5. Defaults (Fresh Install)

From `LauncherSettings` and DataStore defaults:
- `onboarding_complete = false`
- `guided_setup_completed = false`
- `home_page_count = 2`
- `layout_locked = false`
- `caregiver_protection_enabled = false`
- `pin_hash_hex = null`, `pin_salt_hex = null`
- `home_readability_preset = STANDARD`
- `skin_config = SkinConfig()` (Dark theme, standard readability)
- `emergency_phone_number = "911"`
- `emergency_mode = "MENU"`

Room `home_tiles` seeded via `HomeLayoutRules.starterLayout` with fixed essentials.

## 6. PIN Not Configured

When `pin_hash_hex` or `pin_salt_hex` is null:
- Caregiver access flows **must** route to **create PIN** (PinSetup screen).
- After successful PIN creation, caregiver session becomes active and caregiver tools are opened.
- User must never be locked out of caregiver tools due to missing PIN.

## 7. Onboarding Skipped / Incomplete

If `onboarding_complete = false`, app starts in Guided Setup.  
If Guided Setup is partially complete, it resumes at `guided_setup_step`.

## 8. Home Screen Page/Tile Construction

Canonical logic:

1. Read `home_page_count` from settings.
2. Read `home_tiles` from Room.
3. Apply `HomeLayoutRules.ensureRequiredActions`.
4. Compute pages via `HomeLayoutRules.pages`.
5. Render pages directly; do **not** ignore non-primary tiles.

## 9. Unsupported Options

- **Care Mode** and **Communication Mode** layouts are not supported by the fixed 2x3 V1 home. These options must be removed or disabled with an explanation in caregiver settings.
- Onboarding should not expose options that do not map to the canonical model or are not rendered on the home screen.
