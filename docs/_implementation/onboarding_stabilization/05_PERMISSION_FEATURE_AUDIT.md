# Permission and Feature Audit

## Issue
Helpful Features screen (PermissionsExplanationScreen) claims support for dialer, emergency, camera, etc. but doesn't verify intent availability.

## Required Check
I need to update `PermissionsExplanationScreen` or its ViewModel to use Android's `PackageManager` or `Intent.resolveActivity` to verify if features are actually available on the device, and show "Not available on this device" if they aren't.

Features to check:
1. Dialer / Call shortcut: `Intent(Intent.ACTION_DIAL)`
2. Contacts: `Manifest.permission.READ_CONTACTS` (check if implemented).
3. Camera: `Intent(MediaStore.ACTION_IMAGE_CAPTURE)`
4. Photos/Media: `Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)`
5. Backup/Restore: SAF intents `Intent.ACTION_CREATE_DOCUMENT`
6. Notifications: POST_NOTIFICATIONS permission.
7. Battery: Check if battery broadcast receiver is available.
8. Flashlight: Check `PackageManager.FEATURE_CAMERA_FLASH`.

## Findings
Pending updates to `PermissionsExplanationScreen`.