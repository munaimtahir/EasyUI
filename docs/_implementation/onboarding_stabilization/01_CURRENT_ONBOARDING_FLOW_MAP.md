# Onboarding Flow Map

## Current Flow

1. **Step 1: WelcomeScreen** - Introductory text and trust badges. Does not scroll safely (nested in WizardShell). Next -> Step 2.
2. **Step 2: ProtectionOptionsScreen** - Select protection level (Recommended, Flexible, Simple). Writes SetupProtectionLevel state. Next -> Step 3.
3. **Step 3: ThemePickerScreen** - Select visual theme and accessibility mode. Writes VisualTheme/AccessibilityMode state. Next -> Step 4.
4. **Step 4: PermissionsExplanationScreen** - Enable optional permissions (dialer, contacts, etc). Writes OptionalPermissions state. Next -> Step 5.
5. **Step 5: LauncherActivationScreen** - Set EasyUI as default launcher. Checks system default launcher intent. Next -> Step 6.
6. **Step 6: ReadabilityPresetScreen** - Select readability preset. Uses `LazyColumn` inside WizardShell's `LazyColumn` (nested scrolling issue). Writes HomeReadabilityPreset. Next -> Step 7.
7. **Step 7: HomeLayoutSetupScreen** - Select number of home pages. Uses Grid. Displays page preview but preview is clipped or not scrollable. Writes homePageCount. Next -> Step 8.
8. **Step 8: AllowedAppsSetupScreen** - Assign apps to home slots. Has nested LazyVerticalGrid and LazyColumn inside WizardShell. Clipping/scroll crash. Next -> Step 9.
9. **Step 9: ContactsSetupScreen** - Configure emergency mode and call shortcuts. Nested LazyColumn. Next -> Step 10.
10. **Step 10: SecuritySetupScreen** - Setup PIN and layout lock. Writes pin, layoutLocked. Next -> Step 11.
11. **Step 11: DeviceSupportScreen** - Toggle battery info. Writes showBatteryInfo. Next -> Step 12.
12. **Step 12: ReviewConfirmScreen** - Review selections. Nested LazyColumn. Next -> Step 13.
13. **Step 13: CompletionScreen** - Setup complete. Finish -> Navigate to Home.

## Issues Identified
* **Scrolling**: `WizardShell` currently uses a `LazyColumn` with an `item` that contains a `Column` containing the `content`. Since `content` in several screens (`ReadabilityPresetScreen`, `AllowedAppsSetupScreen`, `ContactsSetupScreen`, `ReviewConfirmScreen`) uses `LazyColumn` with `.weight(1f)`, this causes a layout crash or breaks virtualization because the parent `item` has infinite height constraint.
* **Flow Order**: "Select EasyUI as default launcher" (Step 5) needs to be moved to the front.
* **Security & Protection**: "SecuritySetupScreen" (Step 10) is disconnected from "ProtectionOptionsScreen" (Step 2).
* **Theme**: Calm Teal and Midnight Indigo do not visibly change UI (likely due to missing implementation in ThemePickerScreen or Theme logic).
* **Placement**: Slot placement empty slot selection does not trigger placement.
