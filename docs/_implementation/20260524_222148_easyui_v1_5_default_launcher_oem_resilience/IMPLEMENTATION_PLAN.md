# Implementation Plan - EasyUI V1.5 Default Launcher & OEM Resilience

## 1. Default Launcher Logic Improvements
- **Goal:** Improve the reliability of `isDefaultLauncher()` and `openDefaultLauncherSettings()`.
- **Changes:**
    - Update `AndroidDefaultLauncherManager` to use a more robust check for the default home app.
    - Add detection for cases where multiple launchers are resolving and no default is set.
    - Improve `openDefaultLauncherSettings()` to try multiple intent paths (e.g., specific OEM paths if known).

## 2. Intent Hardening & Resilience
- **Goal:** Ensure critical actions (Phone, Messages, Camera, etc.) have robust fallbacks.
- **Changes:**
    - Create a central `IntentHardener` or similar in `core:platform`.
    - Use `queryIntentActivities` to verify resolution before starting.
    - Implement a "Safe Handoff" pattern: if the preferred app fails, try the next best one or show a user-friendly fallback screen.
    - Hardened intents for:
        - `ACTION_DIAL` (Phone)
        - `ACTION_SENDTO` (SMS/Messages)
        - `STILL_IMAGE_CAMERA` (Camera)
        - `ACTION_VIEW` (Photos/Gallery)
        - `ACTION_WIFI_SETTINGS`
        - `ACTION_POWER_USAGE_SUMMARY` (Battery)
        - `ACTION_HOME_SETTINGS`

## 3. Enhanced Assisted Recovery
- **Goal:** Provide better guidance when EasyUI is not the default.
- **Changes:**
    - Update `AssistedRecoveryScreen` logic in `EasyUiNavGraph` to be more resilient to intent failures.
    - Improve wording for "Set Default Launcher" guidance, potentially adding a "How to" helper if settings doesn't open.

## 4. Fallback Screens & Messaging
- **Goal:** Replace snackbars with meaningful "Can't open this" screens for critical failures.
- **Changes:**
    - Add a `FallbackFailureScreen` to `feature:home`.
    - Update `HomeViewModel` to navigate to this screen when a critical launch fails.

## 5. OEM Specific Handling (Initial)
- **Goal:** Address known vivo behavior from discovery.
- **Changes:**
    - Add specific logging for vivo's `ro.product.manufacturer`.
    - Try vivo-specific intent for Home settings if generic one fails.

## Execution Steps
1. **Research OEM-specific intents** for vivo, Samsung, and others.
2. **Refactor `DefaultLauncherManager`** and its implementation.
3. **Implement `IntentHardener`** and integrate it into `PlatformActions`.
4. **Update `HomeViewModel` and `EasyUiNavGraph`** to use hardened intents and fallback UI.
5. **Verify on physical device** (34081500040008N).
