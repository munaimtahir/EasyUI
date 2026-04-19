# Test Strategy

Launcher software sits on a critical path. Favor reliability and clear behavior over feature breadth.

## Unit tests

Cover:

- home tile ordering and validation
- hidden app filtering
- caregiver lock rules
- PIN verification
- backup payload validation
- SOS and fallback rules
- theme and settings persistence rules

## Integration tests

Cover:

- reading installed apps into visible list
- restoring home layout from backup
- import rejection on malformed payload
- premium fallback when billing is unavailable

## UI tests

Cover:

- onboarding flow
- home rendering with large tiles
- app list search and alphabetical display
- caregiver PIN gate
- hide and unhide app flow
- add and edit photo contact flow
- layout lock behavior
- backup and restore confirmation flow

## Manual device tests

1. Set app as default launcher.
2. Reboot device and verify launcher continuity.
3. Install and uninstall apps; confirm app list refresh.
4. Lock layout and verify accidental move prevention.
5. Use the senior daily flow without caregiver steps.
6. Backup configuration and restore it.
7. Verify the onboarding CTA remains reachable on smaller-height displays.
8. Test on a device with unsupported torch.
9. Test without call or SMS permissions granted.
10. Confirm readability at different font scales.
11. Confirm safe behavior when a target app is removed.

## Release verification workflow

Run this sequence before calling a build release-ready:

1. `./gradlew testDebugUnitTest`
2. `./gradlew connectedDebugAndroidTest`
3. `./e2e/scripts/run-static.sh`
4. `./e2e/scripts/run-device-smoke.sh <adb-serial>`
5. `./e2e/scripts/run-device-full.sh <adb-serial>`

Single-command physical-device handoff:

- `./e2e/scripts/run-release-device-suite.sh <adb-serial>`
- If one device is attached, the serial may be omitted.

Expected release evidence:

- local unit tests pass
- Compose and instrumentation smoke tests pass on an attached Android device or emulator
- static Playwright checks pass
- smoke and full ADB-driven Playwright runs write artifacts under `device_test_runs/<timestamp>/`
- manual QA confirms default launcher flow, reboot persistence, missing-app fallback, and caregiver lock behavior

If no ADB device is attached, do not mark device verification complete. Report the exact missing serial and keep release status as pending physical-device validation.
