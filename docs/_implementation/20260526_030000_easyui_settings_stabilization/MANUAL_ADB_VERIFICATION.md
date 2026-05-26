# Manual ADB Verification

## Scope

Use this script to verify the stabilization sprint on a connected Android device after installing the debug build.

## Prerequisites

- A device is connected and visible in `adb devices`
- EasyUI debug APK has been built at `app/build/outputs/apk/debug/app-debug.apk`

## Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Launch

```bash
adb shell am start -n com.easyui.launcher.debug/.MainActivity
```

If the debug launcher package is not active, open the app from the device launcher and set EasyUI as the default home app when prompted.

## Verify These Behaviors

1. Fresh install and no PIN:
   - Tap the clock 5 times on the home screen.
   - Confirm caregiver access flows to PIN setup instead of failing.

2. Existing PIN:
   - Tap the clock 5 times.
   - Confirm the PIN entry screen opens.
   - Enter the correct PIN and confirm caregiver settings open.

3. Theme selection:
   - Open Caregiver Settings.
   - Open Visual Theme.
   - Change the theme.
   - Return to caregiver settings and confirm the selected theme is preserved.

4. Home pages:
   - Open the layout/pages flow.
   - Confirm the theme selection entry is available from caregiver settings and layout pages.
   - Confirm page count changes remain visible.

5. Layout lock:
   - Toggle layout lock on and off.
   - Confirm the home UI does not show per-tile lock icons.

6. Allowed apps and home tiles:
   - Assign an app to an empty slot.
   - Confirm it appears on the home screen after returning.

7. Emergency/contact shortcuts:
   - Configure a contact shortcut and emergency number.
   - Confirm the shortcut renders on home and launches the configured action.

## Evidence To Capture

- Screenshot of caregiver settings showing Visual Theme and Theme & Pages actions
- Screenshot of the dedicated theme selection screen
- Screenshot of home screen after assigning an app slot
- Screenshot or logcat snippet showing clock 5-tap caregiver entry
- Screenshot of emergency/contact shortcut rendering on home
- Logcat if any launcher route or intent fails

Store artifacts under:

```text
docs/_implementation/20260526_030000_easyui_settings_stabilization/evidence/
```
