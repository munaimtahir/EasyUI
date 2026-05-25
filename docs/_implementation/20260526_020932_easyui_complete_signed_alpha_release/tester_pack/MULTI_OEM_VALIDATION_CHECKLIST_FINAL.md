# Multi-OEM Validation Checklist

This checklist is for alpha testers to verify EasyUI's behavior on different Android manufacturers.

## Device Information
- **Manufacturer:** (e.g., Samsung, Xiaomi, Pixel)
- **Android Version:** (e.g., 11, 12, 13, 14)
- **Model:**

## 1. Setup & Default Launcher
- [ ] **Onboarding Trigger:** Does clicking "Open Default App Settings" show a system chooser?
- [ ] **Home Settings:** Does the app correctly open the "Home App" selection screen?
- [ ] **Stickiness:** After setting as default, does pressing the Home button ALWAYS return to EasyUI?
- [ ] **Reboot Test:** Does EasyUI start immediately after the phone is restarted?

## 2. All Apps & Handoffs
- [ ] **App List:** Are all apps visible? (Except those hidden by caregiver).
- [ ] **Handoff (Photos):** Does "Photos" open your gallery app correctly?
- [ ] **Handoff (Camera):** Does "Camera" open your camera app correctly?
- [ ] **Safe Return:** When returning from an external app (like Camera), do you return directly to EasyUI?

## 3. Resilience & Alerts
- [ ] **Wi-Fi Off:** If Wi-Fi is disabled, does the "Connection is off" card appear?
- [ ] **Recovery Fix:** Does the "Fix Internet" button take you to Wi-Fi settings?
- [ ] **Low Battery:** Does the low battery warning appear at the correct threshold?

## 4. Caregiver Tools
- [ ] **Access:** Does the 5-tap or long-press on the clock consistently open the PIN/Dashboard?
- [ ] **Hidden Apps:** Does searching for an app in "Hidden Apps" work?
- [ ] **Persistence:** If you hide an app, does it disappear from "More Apps" immediately?

Please include these results in your feedback!
