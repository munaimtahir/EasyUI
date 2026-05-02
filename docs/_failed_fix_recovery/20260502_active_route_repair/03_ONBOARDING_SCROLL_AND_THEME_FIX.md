## Onboarding Scroll + Theme Fix (2026-05-02)

### Problem (as observed on device, per prompt)
- First-launch onboarding showed `Welcome to EasyUI` with light/cream styling.
- Bottom explanatory copy was clipped/hidden behind the fixed bottom CTA area.

### Active source (confirmed)
- `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` → `Routes.GuidedSetup` step `1`
- `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` → `WelcomeScreen`
- `core/ui/src/main/java/com/easyui/core/ui/components/WizardShell.kt` provided the fixed bottom CTA shell.

### Fixes applied
1) **Scroll + clipping**
   - Updated `core/ui/src/main/java/com/easyui/core/ui/components/WizardShell.kt` to render content in a `LazyColumn`.
   - This ensures long onboarding copy can be scrolled and never becomes unreachable behind the bottom CTA.

2) **Theme mismatch**
   - Made the app theme respond to persisted `SkinConfig` instead of the device system light/dark mode:
     - `app/src/main/java/com/easyui/launcher/MainActivity.kt` no longer wraps content in `EasyUiTheme`.
     - `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` now wraps the app UI in `EasyUiTheme(skinConfig = appState.settings.skinConfig)`.
   - Updated theme defaults to remove “warm light” as the forced first-run look:
     - `core/domain/src/main/java/com/easyui/core/domain/model/SkinConfig.kt` default `visualTheme` is now `DARK_COMFORT`.
     - `core/data/src/main/java/com/easyui/core/data/datastore/LauncherSettingsDataStore.kt` default visual theme fallback is now `DARK_COMFORT`.
     - `core/data/src/main/java/com/easyui/core/data/backup/BackupSerializer.kt` default visual theme fallback is now `DARK_COMFORT`.

### Acceptance check (local code-level)
- Old first screen is still the same route, but it now scrolls.
- Default first-run style is no longer forced to warm light; it follows persisted `SkinConfig` which defaults to `DARK_COMFORT`.

### NOT TESTED
- `NOT RUN — requires local ADB` (this environment does not have the target device/serial attached).

