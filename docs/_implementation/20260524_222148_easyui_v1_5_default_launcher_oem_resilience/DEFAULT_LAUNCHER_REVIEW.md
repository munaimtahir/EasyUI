# Default Launcher Review - EasyUI V1.5

## Current Status
- **Detection:** Uses `PackageManager.resolveActivity` for `CATEGORY_HOME`. This works but doesn't distinguish between "this app is the only one", "this app is the default", or "there are multiple and no default".
- **Activation:** Uses `ACTION_HOME_SETTINGS` and `ACTION_MANAGE_DEFAULT_APPS_SETTINGS`. This is standard but lacks OEM-specific fallbacks and doesn't use the modern `RoleManager` (API 29+).

## Research Findings
- **RoleManager (API 29+):** The most reliable way to prompt for the default home app.
- **Fake Launcher Trick:** A robust fallback that forces the system chooser to appear by briefly enabling a dummy launcher activity.
- **OEM Paths:** Manufacturers like Xiaomi, Samsung, and Huawei have specific settings activities that can be targeted if standard intents fail.

## Proposed Improvements
1. **Tiered Activation:**
    - Tier 1: `RoleManager` (Android 10+).
    - Tier 2: `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS` (API 24+).
    - Tier 3: `Settings.ACTION_HOME_SETTINGS` (API 21+).
    - Tier 4: OEM-specific component intents (Xiaomi, Samsung, Huawei).
    - Tier 5: "Fake Launcher" trick to force the system chooser.
2. **Robust Detection:**
    - Check if multiple activities resolve for `CATEGORY_HOME`.
    - If `RoleManager` is available, use `roleManager.isRoleHeld(RoleManager.ROLE_HOME)`.
3. **Caregiver Guidance:**
    - Provide clear instructions if none of the intents work (e.g., "Go to Settings -> Apps -> Default Apps").
