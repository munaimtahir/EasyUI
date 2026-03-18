# Crash Fix: ACCESS_NETWORK_STATE

## Root cause

`AndroidDeviceStatusRepository.readStatus()` called `ConnectivityManager.activeNetwork` and `getNetworkCapabilities(...)` during launcher startup without `android.permission.ACCESS_NETWORK_STATE` declared in the app manifest.

On affected devices, this raised:

- `SecurityException: ConnectivityService: Neither user nor current process has android.permission.ACCESS_NETWORK_STATE`

Because device status is collected during Home startup, the exception crashed the launcher process.

## Files changed

- `app/src/main/AndroidManifest.xml`
- `core/platform/src/main/java/com/easyui/core/platform/actions/AndroidDeviceStatusRepository.kt`

## Exact fix

1. Added required permission:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

2. Hardened device-status reads:

- `ConnectivityManager`, `TelephonyManager`, and `SubscriptionManager` lookups are nullable.
- Network capability read is isolated in `readNetworkCapabilities()` with targeted exception guards:
  - `SecurityException`
  - `IllegalStateException`
- Signal and SIM reads are guarded with the same targeted exceptions.
- `observeDeviceStatus()` now catches `SecurityException` and `IllegalStateException` around `readStatus()` and emits a safe `DeviceStatus()` fallback.

## Hardening behavior

If status APIs fail (missing permission, OEM quirks, transient state), launcher startup continues and the top status bar degrades gracefully:

- Wi-Fi: `Wi-Fi unknown`
- Signal: `Signal unknown`
- SIM: `SIM`

No crash is propagated to UI startup.

## Similar-risk review

Connectivity usage was searched repository-wide. `AndroidDeviceStatusRepository` is the only direct `ConnectivityManager` usage path and is now both permission-backed and defensively guarded.

Battery status path (`AndroidBatteryStatusRepository`) does not require network-state permission and already has null-safe fallback mapping.

## Verification

- Debug manifest merge contains `ACCESS_NETWORK_STATE`:
  - `app/build/intermediates/merged_manifest/debug/processDebugMainManifest/AndroidManifest.xml`
  - `app/build/intermediates/merged_manifests/debug/processDebugManifest/AndroidManifest.xml`
- Build checks:
  - `:app:processDebugMainManifest` ✅
  - `:app:compileDebugKotlin` ✅
  - `:app:assembleDebug` ✅
