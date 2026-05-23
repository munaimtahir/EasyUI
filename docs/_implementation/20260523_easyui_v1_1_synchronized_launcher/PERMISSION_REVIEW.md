# EasyUI V1.1 Permission Review

## Existing Permissions
- `INTERNET`: For app catalog and basic connectivity.
- `CALL_PHONE`: For direct call functionality from tiles.
- `QUERY_ALL_PACKAGES`: To list and launch installed apps.
- `READ_EXTERNAL_STORAGE` (Legacy): For photo access if needed.

## Added Permissions
- None. This sprint focused on logic using existing connectivity and system status APIs.

## Rejected Permissions
- `READ_SMS` / `SEND_SMS`: Avoided to minimize Play Store restriction risk.
- `READ_CALL_LOG`: Avoided as we do not yet implement a full dialer history.
- `ACCESS_FINE_LOCATION`: Not needed for this phase of Guardian Checks.

## Play Store Risk Notes
- `QUERY_ALL_PACKAGES` remains the highest risk permission for a launcher, but is justifiable as a core launcher feature.
- No new sensitive permissions were introduced in this sprint.

## Alternatives Used
- Used `ConnectivityManager` and `NetworkCapabilities` for internet health checks instead of attempting to ping external servers or requiring special permissions.
- Used `DefaultLauncherManager` logic to check home screen status via intent resolution.
