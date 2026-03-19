# Manual QA Checklist - Upgrade Pass

Run on at least one physical device if possible. Emulator-only checks are acceptable for UI flow validation when hardware features are unavailable.

## 1) First launch experience

1. Install and open EasyUI.
2. Confirm Intro screen appears with setup guidance.
3. Continue to launcher guidance and then caregiver help.

Expected:

- Copy is readable and caregiver-friendly.
- Flow reaches home after finishing setup.

## 2) Default launcher selection flow

1. On launcher guidance screen, tap `Open Default App Settings`.
2. Set EasyUI as default launcher.
3. Return and tap `Check Again`.

Expected:

- Status reflects default launcher when selected.
- Home button returns to EasyUI.

## 3) Caregiver entry discovery

1. On home, long-press the top status bar.
2. Use the clock-tap fallback by tapping the clock five times quickly.

Expected:

- Caregiver entry is practical and discoverable.
- The clock fallback still works.

## 4) Caregiver PIN enabled flow

1. In caregiver settings, set caregiver PIN.
2. Ensure protection is enabled.
3. Return home, re-enter caregiver settings.

Expected:

- PIN verify screen appears before caregiver settings.
- Incorrect PIN is rejected with clear error.
- Correct PIN opens caregiver tools.

## 5) Caregiver PIN disabled flow

1. In caregiver settings, disable PIN protection toggle.
2. Return home and tap caregiver entry.

Expected:

- Caregiver settings open directly (no PIN prompt).

## 6) Home page switching

1. In caregiver `Layout and Pages`, change the page count between 1 and 3.
2. Return home and confirm the fixed senior grid is unchanged.

Expected:

- Page count stays bounded and consistent.
- The senior home remains a fixed six-tile grid.

## 7) Fixed layout stability after app addition

1. Open caregiver `Home Apps` management.
2. Assign one or more apps into available app slots.
3. Return home and relaunch app.

Expected:

- Essentials stay anchored (Phone, Flashlight, Camera, Emergency, Health Info, SOS).
- Added Home Apps occupy predictable fixed slots.
- Layout persists after relaunch.

## 8) Phone tile behavior

1. Tap `Phone` on home.

Expected:

- System dialer opens via safe dialer path.
- If unavailable, user receives clear fallback message.

## 9) Emergency tile behavior

1. Set emergency number in caregiver settings.
2. Tap `Emergency` on home.

Expected:

- Dialer opens with configured number prefilled.
- No silent direct call is placed.

## 10) Camera tile behavior

1. Tap `Camera` on home.

Expected:

- Camera app opens when available.
- Clear fallback message appears if camera action unavailable.

## 11) Flashlight tile behavior

1. Tap `Flashlight` tile to toggle.
2. Tap again to toggle back.

Expected:

- Torch toggles when supported.
- Unsupported devices show graceful fallback.

## 12) Health Info editing and viewing

1. In caregiver settings, open `Health Info`.
2. Save values for name, blood group, allergies, medicines, and notes.
3. Return home and tap `Health Info`.

Expected:

- Saved values are shown in readable format.
- Data persists after app relaunch.
- Empty values show clear `Not set` style text.

## 13) Battery visibility toggle and home display

1. In caregiver settings, toggle `Show battery on home` on.
2. Return home and confirm battery summary in header.
3. Toggle off and confirm battery summary disappears.

Expected:

- Battery visibility responds to caregiver setting and persists.

## 14) Home Apps selection

1. Open caregiver `Home Apps`.
2. Select a slot and place an installed app.
3. Remove a placed Home App.

Expected:

- Placement only occurs in non-reserved slots.
- Reserved essentials cannot be overwritten.
- Remove action clears only selected Home App tile.

## 15) All Apps entry point

1. Confirm the app list surface is not yet exposed from the senior home in this build.

Expected:

- The app list screen remains a scaffolded/internal surface until its home entry point is wired.

## 16) Hidden Apps behavior

1. Open caregiver `Hidden Apps`.
2. Hide an app.
3. Verify the hidden package state is reflected in the EasyUI surfaces that expose app inventory.
4. Unhide app and verify return.

Expected:

- Hidden app disappears from EasyUI surfaces/search where exposed.
- Unhide restores visibility.

## 17) Reboot continuity

1. Set EasyUI as default launcher.
2. Reboot device.
3. Press Home.

Expected:

- EasyUI remains default and layout persists after reboot.

## 18) Missing/unavailable system app fallback behavior

1. Remove/disable one app assigned as Home App.
2. Tap the missing tile on home.
3. If possible, test on device without flashlight/with restricted camera.

Expected:

- Missing app tile shows non-crashing fallback state.
- Action tiles show clear fallback messages for unavailable features.
