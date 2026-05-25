# Intent Resilience Review - EasyUI V1.5

## Current Status
- **Resolution:** Uses `resolveActivity` before launching.
- **Fallbacks:** Simple snackbars ("Phone is not available").
- **Intent Definitions:** Distributed across `EasyUiNavGraph`, `AndroidAppLauncher`, `AndroidEmergencyActionHandler`, and `AndroidCameraActionHandler`.

## Risk Assessment
- **Missing Apps:** If a device doesn't have a default dialer or gallery app (common in some custom ROMs), the current `resolveActivity` check will fail and show a snackbar.
- **ActivityNotFoundException:** Some apps might claim to handle an intent but crash when launched.
- **OEM Variations:** Intent actions like `ACTION_POWER_USAGE_SUMMARY` might not be supported on all devices.

## Proposed Improvements
1. **Centralized Intent Registry:** Move all critical intent definitions to a single `IntentHardener` or `PlatformIntents` utility.
2. **Safe Handoff Pattern:**
    - For Phone: Try `ACTION_DIAL`. If it fails, try searching for any app with "phone" or "dialer" in its name/package and prompt the user.
    - For Photos: Try `ACTION_VIEW` with image MIME type. If it fails, try common gallery package names.
    - For Settings: Provide a list of alternative intent actions for common settings (Wi-Fi, Battery, Home).
3. **Robust Resolution:**
    - Use `PackageManager.queryIntentActivities` to list all available handlers.
    - Avoid `ActivityNotFoundException` by wrapping `startActivity` in try-catch and providing a "Safe Fallback" UI.
4. **Fallback UI:**
    - Instead of just a snackbar, navigate to a `SafeFallbackScreen` which explains the issue and offers manual steps or alternative apps.
