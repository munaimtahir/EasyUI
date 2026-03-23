# Play Release Readiness

## Scope

This release-readiness pass applies to the Android project: `EasyUI Senior Launcher`.

## Audit date

March 21, 2026 (UTC)

## Release audit summary

### Build and packaging

- App module: `app`
- Application ID: `com.easyui.launcher`
- `minSdk`: 26
- `targetSdk`: 35
- `versionCode`: 1
- `versionName`: `1.0.0`
- **Build Status**:
  - `assembleDebug`: ✅ PASSED
  - `test`: ✅ PASSED (all unit tests passing)
  - `lintRelease`: ✅ PASSED (with hardening and suppressions)
  - `assembleRelease`: ⚠️ Requires signing key (`easyui-release.jks`) to complete final APK/Bundle.

### Permissions and feature declarations

The manifest has been updated to support core launcher features while ensuring maximum device compatibility:

- `android.permission.SEND_SMS`: Required for emergency and messaging features.
- `android.permission.CALL_PHONE`: Required for dialer integration.
- `android.permission.ACCESS_NETWORK_STATE`: Required for status bar connectivity indicators.
- `android.permission.READ_PHONE_STATE`: Added to support SIM carrier name display.

**Hardware Features (Optional)**:
- `android.hardware.camera.flash`: `required="false"` (for flashlight tile).
- `android.hardware.telephony`: `required="false"` (ensures installation on non-telephony devices like tablets).

### Code Hardening

- **AndroidDeviceStatusRepository**: Implemented defensive `try-catch` blocks and safe fallback values for connectivity, signal strength, and SIM status to prevent crashes on devices with restricted permissions or OEM-specific API quirks.
- **AndroidBatteryStatusRepository**: Added API-level compatibility guards for battery status monitoring.

## Recent Fixes & Improvements

1. **Merge Conflict Resolution**: Resolved conflicts in `LockedHomeSmokeTest.kt` to restore test suite integrity.
2. **Lint Error Resolution**:
   - Fixed `PermissionImpliesUnsupportedChromeOsHardware` by declaring telephony hardware as optional.
   - Fixed `MissingPermission` and `NewApi` errors in platform repositories through a combination of manifest updates and safe API suppression with guards.
3. **Verification**: Successfully ran a full clean build and test pass (`./gradlew test lintRelease assembleDebug`).

## Manual Release Boundary

- **Signing**: The project expects a keystore at `app/easyui-release.jks`. This must be provided or generated for the final Play Store upload.
- **Play Console**: Data Safety and content rating questionnaires must be updated to reflect the `READ_PHONE_STATE` and `SEND_SMS` usage.
- **Assets**: Final feature graphics and store screenshots are required.
