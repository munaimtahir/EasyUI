## Guided Setup Flow — New Steps Connected (2026-05-02)

### Goal
Ensure the *active* guided setup route includes:
1) Protection Options
2) Theme Picker
3) Permissions Explanation
…before the existing setup flow continues.

### Active flow implementation
- `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
  - Route: `Routes.GuidedSetup`
  - Switch: `when (guidedSetupState.guidedSetupStep)`

### Updated step order (active)
The active flow is now:
1. Welcome (`WelcomeScreen`)
2. Protection Options (`ProtectionOptionsScreen`)
3. Theme Picker (`ThemePickerScreen`)
4. Permissions Explanation (`PermissionsExplanationScreen`)
5. Default launcher selection (`LauncherActivationScreen`)
6. Readability preset (`ReadabilityPresetScreen`)
7. Home page count (`HomeLayoutSetupScreen`)
8. Allowed apps placement (`AllowedAppsSetupScreen`)
9. Call shortcuts & emergency mode (`ContactsSetupScreen`)
10. Security & layout lock (`SecuritySetupScreen`)
11. Home details (`DeviceSupportScreen`)
12. Review (`ReviewConfirmScreen`)
13. Completion (`CompletionScreen`)

### Protection Options (persisted)
- Model: `core/domain/src/main/java/com/easyui/core/domain/model/SetupProtectionLevel.kt`
- Stored in settings:
  - `LauncherSettings.setupProtectionLevel`
  - DataStore key: `setup_protection_level`
- Screen: `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` → `ProtectionOptionsScreen`

Notes:
- Recommended path sets `layoutLocked = true` immediately as the safe default (the PIN step still follows later).
- Copy is explicit: EasyUI does not “lock Android”; it protects EasyUI layout and caregiver settings.

### Theme Picker (persisted + applied)
- Uses existing `SkinConfig` persistence:
  - Visual theme: `SkinConfig.visualTheme`
  - Accessibility: `SkinConfig.accessibilityMode`
- Theme application is live because:
  - `EasyUiTheme(skinConfig = appState.settings.skinConfig)` wraps the app UI.
- Screen: `feature/onboarding/.../GuidedSetupScreens.kt` → `ThemePickerScreen`

Mapping used for the requested labels:
- Midnight Indigo → `VisualTheme.DARK_COMFORT` (default)
- Calm Teal → `VisualTheme.SOFT_CALM`
- Soft Blue → `VisualTheme.CLINICAL_PROFESSIONAL`
- Warm Light → `VisualTheme.LIGHT_PREMIUM`
- High Contrast → `AccessibilityMode.HIGH_CONTRAST`

### Permissions Explanation (persisted preferences)
These are caregiver intent toggles (not Android grant state).
- Model: `core/domain/src/main/java/com/easyui/core/domain/model/OptionalPermission.kt`
- Stored in settings:
  - `LauncherSettings.setupOptionalPermissions` (set of enum names)
  - DataStore key: `setup_optional_permissions`
- Screen: `feature/onboarding/.../GuidedSetupScreens.kt` → `PermissionsExplanationScreen`

### NOT TESTED
- `NOT RUN — requires local ADB` (device verification pending).

