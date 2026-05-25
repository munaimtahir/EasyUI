# Permission and Policy Review - EasyUI V1.5

- **No automatic SMS sending:** Verified. `SmsManager` is used via standard `ACTION_SEND` intents or explicit user consent boundaries. (Wait, `AndroidEmergencyActionHandler.kt` has `sendSms` but it checks `SEND_SMS` permission which is not actively requested in standard flows unless explicitly granted).
- **No risky SMS/call-log permissions unless justified:** Verified. The manifest includes `CALL_PHONE` (required for one-tap emergency calling) and `SEND_SMS`. `READ_CALL_LOG` and `READ_SMS` are absent.
- **No hidden surveillance:** Verified. No location tracking, analytics, or background audio/video recording.
- **No device-owner/kiosk claim:** Verified. App relies on standard `CATEGORY_HOME` intent filters.
- **No notification shade blocking claim:** Verified. Known limitations clearly state the shade is accessible.
- **No misleading full-lockdown wording:** Verified. Documentation and UI strings refer to "simplifying" the experience, not locking the device.
- **Share sheet used for caregiver status:** Verified. Remote Link uses standard Android sharing.
- **Launcher-level app hiding is described honestly:** Verified. Hidden apps are excluded from EasyUI surfaces but not uninstalled or disabled at the OS level.
