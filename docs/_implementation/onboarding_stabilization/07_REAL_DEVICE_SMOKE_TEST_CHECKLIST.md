# Real Device Smoke Test Checklist

Use this checklist on a physical device to verify the onboarding stabilization.

## 1. Fresh Install Test
- [ ] Uninstall the old app.
- [ ] Install the new debug APK.
- [ ] Launch the app.
- [ ] Confirm the first actionable step is "Set EasyUI as Home".
- [ ] Select EasyUI as default launcher.
- [ ] Confirm no crash.
- [ ] Press the hardware/software Home button.
- [ ] Confirm EasyUI opens.

## 2. Onboarding Scroll Test
Confirm scrolling works seamlessly on small screens for:
- [ ] Welcome
- [ ] Protection Level
- [ ] Security / Layout Lock (now directly after Protection Level)
- [ ] Theme
- [ ] Readability
- [ ] Home Pages (Verify the dynamic preview grid updates correctly)
- [ ] Apps on Home Screen / Added Pages (App list scrolls independently of the grid)
- [ ] Call Shortcut / Emergency Number
- [ ] Helpful Features Status
- [ ] Battery Indicator
- [ ] Setup Complete

## 3. Theme Test
- [ ] Select Calm Teal. Confirm visible dark teal theme change.
- [ ] Select Midnight Indigo. Confirm visible dark purple/indigo theme change.
- [ ] Select Soft Blue. Confirm visible light blue theme change.
- [ ] Restart app. Confirm the selected theme persists.

## 4. Page Test
- [ ] Select 1 page. Verify the preview updates to show 1 page (6 slots).
- [ ] Select 2 pages. Verify the preview updates to show 2 pages (12 slots).
- [ ] Select 3 pages. Verify the preview updates to show 3 pages (18 slots).
- [ ] Complete setup. Verify the created pages appear on the actual home screen.

## 5. App Slot Test
- [ ] On the Allowed Apps screen, select an app from the list.
- [ ] Select an empty slot (e.g., on Page 2).
- [ ] Press Place.
- [ ] Verify the app appears in the slot immediately.
- [ ] Try to place an app into an occupied slot (e.g., Phone). Verify a warning appears.

## 6. Call/Emergency Test
- [ ] Enter an emergency number.
- [ ] Enter/configure a call shortcut if supported.
- [ ] Proceed forward and back in the wizard.
- [ ] Verify the entered values persist.

## 7. Helpful Features / Permissions Test
- [ ] On the Helpful Features screen, verify the status labels (e.g., "Ready", "Not available on this device").
- [ ] Ensure unavailable features have their toggle disabled.

## 8. Settings/Caregiver Access Test
- [ ] Complete onboarding.
- [ ] Long-press the top status bar (or tap the clock 4 times).
- [ ] Verify entry to caregiver/settings works.
- [ ] Verify Flexible Mode does not prompt for a PIN.
- [ ] Verify Protected Mode prompts for a PIN if one was selected.
- [ ] Verify layout editing can be reached from the caregiver menu.

## 9. Regression Test
- [ ] Press Home button repeatedly.
- [ ] Rotate the device if supported.
- [ ] Restart the app.
- [ ] Reboot the phone if possible.
- [ ] Confirm EasyUI still reliably acts as the launcher.