# EasyUI V1.4 Permission and Policy Review

## Existing Permissions
- `INTERNET`, `CALL_PHONE`, `ACCESS_NETWORK_STATE`, `READ_PHONE_STATE`: Remained unchanged.

## Added Permissions
- None. This sprint used standard Android Intents to open system settings, which does not require additional permissions.

## Play Store Risk Notes
- Opening system settings (Wi-Fi, Default Apps) via standard Intents is a recommended practice and does not violate Play Store policies.
- The app does not attempt to change these settings programmatically or bypass user consent, maintaining transparency and user control.

## Alternatives Used
- Instead of using `WRITE_SETTINGS` (highly sensitive), we use `Intent(Settings.ACTION_...)` to guide the user to the correct place.
