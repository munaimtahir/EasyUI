# EasyUI Caregiver Trust Spine — E2E Device Testing Plan

This document provides a step-by-step runbook for verifying the EasyUI Caregiver-Senior trust spine on physical devices or emulators using standard Android Debug Bridge (ADB) commands and curl verification.

---

## 1. Prerequisites

1. **Host Backend Server**:
   Ensure the backend is running locally on port `8088`:
   ```bash
   ./gradlew :backend:run
   ```
   Verify health check:
   ```bash
   curl -i http://localhost:8088/health
   ```
   Ensure your host machine is accessible from the Android emulator loopback (`10.0.2.2`) or LAN IP if testing on real devices.

2. **Connected Targets**:
   Verify that at least one emulator/device is connected via ADB:
   ```bash
   adb devices
   ```

---

## 2. Installation

Install both the Senior Launcher and the Caregiver Companion APKs onto the target device(s):

```bash
# Install Senior Launcher
./gradlew :senior-launcher:assembleDebug
adb install senior-launcher/build/outputs/apk/debug/senior-launcher-debug.apk

# Install Caregiver Companion
./gradlew :caregiver-companion:assembleDebug
adb install caregiver-companion/build/outputs/apk/debug/caregiver-companion-debug.apk
```

---

## 3. Step-by-Step E2E Verification Workflows

### Workflow A: Pairing Code Generation & Trust Establishment

1. **Launch Senior Launcher**:
   Start the Senior Launcher app on the device/emulator:
   ```bash
   adb shell am start -n com.easyui.senior/com.easyui.senior.MainActivity
   ```
2. Navigate to **Privacy & Trust** (Trust Center) screen.
3. Click **Generate Pairing Code**. An 8-character uppercase code (e.g. `ABCDEF12`) will display.
4. **Launch Caregiver Companion**:
   Start the Caregiver Companion app on the second device/emulator (or same device):
   ```bash
   adb shell am start -n com.easyui.companion/com.easyui.companion.MainActivity
   ```
5. Enter the 8-character pairing code into the input field and click **Pair with Launcher Device**.
6. **Verify Success**:
   - The Companion app shifts to the main dashboard.
   - The Senior Launcher displays "Paired with Caregiver".
   - Verify backend memory maps the relationship by querying logs or check-in states.

---

### Workflow B: Status Report Synchronization

1. **Trigger Status Report Worker**:
   Wait for the WorkManager `StatusReportWorker` to run, or force execution of the background worker using WorkManager/job scheduler.
   Alternatively, view device logs to verify periodic reports:
   ```bash
   adb logcat | grep -E "StatusReportWorker|BackendClient"
   ```
2. **Retrieve Status on Companion**:
   - Refresh the Seniors tab on the Caregiver Companion.
   - Verify battery percentage and charging state match the Senior device exactly.

---

### Workflow C: Senior "I'm OK" Manual Check-In

1. **Submit Check-In**:
   On the Senior Launcher Home screen, click **I'm OK** or check-in confirmation.
2. **Verify Server Receipt**:
   Check the backend logs or query the check-in API directly from the host machine:
   ```bash
   # Replace {seniorDeviceId} with the UUID of the senior device
   curl -H "Authorization: Bearer dev-caregiver-token" http://localhost:8088/checkin/{seniorDeviceId}
   ```
3. **Verify Caregiver View**:
   - Refresh the Seniors tab on the Companion.
   - Verify the "Last Check-In" timestamp updates immediately.

---

### Workflow D: SOS Emergency Alert Flow

1. **Trigger SOS**:
   On the Senior Launcher, click the **SOS** floating button or enter the Emergency Screen and click **Trigger SOS**.
2. **Verify Alerts on Server**:
   Query the alerts endpoint to confirm the server received the SOS event:
   ```bash
   curl -H "Authorization: Bearer dev-caregiver-token" http://localhost:8088/alerts/{seniorDeviceId}
   ```
3. **Verify Companion Notification**:
   - Tap the **Alerts** tab (bell icon) in the Companion App.
   - Confirm that a red "SOS" alert item with the correct timestamp is displayed.

---

### Workflow E: Remote Configuration / Reminders Sync

1. **Push Suggested Reminder**:
   - Go to the **Reminders** tab in the Caregiver Companion.
   - Add a new reminder suggestion (e.g. "Heart Medication" at "09:00").
   - Click **Push suggestions to launcher**.
2. **Fetch Config on Senior**:
   - Trigger the config fetch worker or let the periodic status report run.
   - Verify the reminder is downloaded and merged into the Senior Launcher local reminder storage:
   ```bash
   adb shell run-as com.easyui.senior cat files/datastore/core_settings.preferences_pb
   ```

---

### Workflow F: Trust / Pairing Revocation

1. **Revoke from Senior**:
   - Open **Privacy & Trust** on the Senior Launcher.
   - Click **Disconnect Caregiver** and confirm.
2. **Verify Server Cleanup**:
   - Try fetching status on Caregiver Companion.
   - Verify the endpoint returns `401 Unauthorized` or `403 Forbidden` now that the pairing token has been deleted on the server.
   - The Companion UI should reset to the pairing screen on next sync.

---

### Workflow G: Caregiver Account / Link Deletion

1. **Delete Account from Caregiver**:
   - Open the **Settings** tab in the Caregiver Companion.
   - Click **Disconnect and Clear Device Link**.
2. **Verify Server Cleanup**:
   - Confirm the caregiver's token is deleted on the server, and the pairing relation is entirely purged.
   - Verify the Senior Launcher no longer registers the caregiver as paired.

---

## 4. Diagnostics & Troubleshooting

- **Check device logs in real time**:
  ```bash
  adb logcat *:S EasyUI:D BackendClient:D CompanionBackendClient:D
  ```
- **Wipe app data for fresh test run**:
  ```bash
  adb shell pm clear com.easyui.senior
  adb shell pm clear com.easyui.companion
  ```

---

## 5. v1.0 RELEASE CANDIDATE / REAL-NETWORK ACCEPTANCE

### 5.1 Test Environment Topology Matrix

| Tier | Backend Host / Protocol | Senior Client Configuration | Companion Client Configuration | Network Conditions | Purpose |
| ---- | ----------------------- | --------------------------- | ------------------------------ | ------------------ | ------- |
| **Development** | `http://10.0.2.2:8088` (Cleartext allowed) | `senior-launcher-debug.apk` (`BACKEND_BASE_URL="http://10.0.2.2:8088"`) | `caregiver-companion-debug.apk` (`BACKEND_BASE_URL="http://10.0.2.2:8088"`) | ADB port reverse / localhost | Automated CI & unit/connected testing |
| **Staging** | `https://staging-api.easyui.app` (Strict HTTPS) | `senior-launcher-release.apk` (`-PEASYUI_PROD_BACKEND_URL=https://staging-api.easyui.app`) | `caregiver-companion-release.apk` (`-PEASYUI_PROD_BACKEND_URL=https://staging-api.easyui.app`) | LTE / Cellular or external Wi-Fi | End-to-end integration & pilot validation |
| **Production** | `https://api.easyui.app` (Strict HTTPS) | `senior-launcher-release.aab` / `.apk` | `caregiver-companion-release.aab` / `.apk` | Heterogeneous Real-world Networks | Public Distribution / Production |

### 5.2 Release Candidate Acceptance Checklist (45 Steps)

1. [x] **Clean Senior RC Install**: Install signed/minified `senior-launcher-release.apk`.
2. [x] **Launch Senior**: Verify splash and home screen render with zero startup exceptions.
3. [x] **HOME Role Selection**: Set Senior Launcher as default Android launcher in system settings.
4. [x] **Onboarding Completion**: Confirm initial permission prompt and default 2x3 home grid rendering.
5. [x] **Reboot Test**: Restart device via `adb reboot` and confirm Senior Launcher resumes automatically as HOME.
6. [x] **Home Configuration**: Customize grid and add apps to slots; verify persistence across app relaunch.
7. [x] **Favorite Contacts Setup**: Assign speed-dial contact shortcuts; test calling intent dispatch.
8. [x] **Caregiver PIN Setup**: Access settings via PIN creation; verify 4-digit confirmation and SHA-256 persistence.
9. [x] **Emergency Setup**: Configure emergency contact number and test SOS confirmation screen.
10. [x] **Clean Companion RC Install**: Install signed/minified `caregiver-companion-release.apk`.
11. [x] **Companion Launch**: Verify clean onboarding and unlinked device pairing UI.
12. [x] **Generate Pairing Token**: On Senior Trust Center, tap "Generate Pairing Code" (receive 8-char uppercase code).
13. [x] **Pair Devices**: Enter pairing code in Caregiver Companion and submit.
14. [x] **Token Issuance**: Confirm server issues bearer token with scoped permissions (`battery`, `checkin`, `config`, `alerts`).
15. [x] **Companion Paired State**: Confirm Companion navigates to dashboard showing Senior Device ID.
16. [x] **Initial Status Sync**: Senior broadcasts battery percentage (e.g. 88%) and charging status.
17. [x] **Caregiver Dashboard View**: Confirm Companion displays live battery level, charging indicator, and timestamp.
18. [x] **Senior Check-In ("I'm OK")**: Senior taps "I'm OK" button on home screen.
19. [x] **Check-In Reflection**: Caregiver dashboard receives check-in timestamp and status message.
20. [x] **Reminder Creation**: Caregiver creates reminder ("Heart Medication" at 09:00 AM) and pushes to Senior.
21. [x] **Remote Config Download**: Senior fetches pending configuration and updates local reminder schedule.
22. [x] **SOS Emergency Trigger**: Senior triggers emergency SOS alert.
23. [x] **SOS Alert Broadcast**: Server registers alert; Companion Alerts tab displays high-priority red alert card.
24. [x] **Caregiver Alert Acknowledgment**: Caregiver reviews alert details.
25. [x] **Permission Scoping Test**: Server rejects unauthorized queries when permission is revoked.
26. [x] **Senior Revocation**: Senior taps "Disconnect Caregiver" in Privacy & Trust Center.
27. [x] **Caregiver Disconnection**: Companion polling receives 403 Forbidden and returns to pairing screen.
28. [x] **Senior Functional Isolation**: Senior Launcher remains completely operational locally after revocation.
29. [x] **Offline Resilience**: Disable Wi-Fi/cellular on Senior device (`airplane_mode_on 1`).
30. [x] **Offline HOME Navigation**: Verify all home apps, phone dialer, contacts, and launcher settings work offline.
31. [x] **Offline Actions Queuing**: Check-ins or emergency triggers do not crash when network is unreachable.
32. [x] **Network Reconnection**: Restore connectivity (`airplane_mode_on 0`).
33. [x] **Auto-Recovery**: WorkManager resumes periodic synchronization automatically.
34. [x] **No Duplicate State**: Confirm no duplicated reminders or phantom records on reconnect.
35. [x] **Senior Process Kill**: Force kill Senior (`am force-stop com.easyui.senior`); press HOME button; verify instant restore.
36. [x] **Companion Process Kill**: Force kill Companion (`am force-stop com.easyui.companion`); relaunch; verify session restored.
37. [x] **Device Reboot Persistence**: Reboot emulator/device; verify Senior remains default HOME with settings intact.
38. [x] **Logcat Inspection**: Confirm zero `FATAL EXCEPTION`, `NullPointerException`, or `ANR` traces in logcat.
39. [x] **Memory & Leak Inspection**: Confirm stable heap footprint during continuous screen transitions.
40. [x] **Minification Verification**: Confirm R8 obfuscated classes load serialization serializers without `ClassNotFoundException`.
41. [x] **Caregiver Account Deletion**: Caregiver triggers "Delete Account & Link"; verify server data purge.
42. [x] **Senior Device Data Deletion**: Senior triggers "Delete All Device Data"; verify complete remote erasure.
43. [x] **Single-Use Code Invalidation**: Expired or already-used pairing codes return 401 Unauthorized.
44. [x] **Backend Health Check**: `GET /health` consistently returns `{"status":"healthy"}`.
45. [x] **Final Security Sweep**: No hardcoded API keys, passwords, or cleartext credentials in release artifacts.

### 5.3 Multi-API Device Validation Matrix (RC2)

| Target Device / Emulator | OS & API Level | Arch / Profile | Connected Test Suite | Result |
| ------------------------ | -------------- | -------------- | -------------------- | ------ |
| `Android_15_Test` (emulator-5554) | Android 15 (API 35, VanillaIceCream) | x86_64 / Pixel 5 | `:senior-launcher` (23), `:caregiver-companion` (1), `:app` (2) | **26/26 PASS** |
| `Android_16_Test` (emulator-5556) | Android 16 (API 36, Baklava) | x86_64 / Pixel 8 | `:senior-launcher` (23), `:caregiver-companion` (1), `:app` (2) | **26/26 PASS** |

### 5.4 Dedicated Accessibility Acceptance Gate

- `SeniorAccessibilityTest.kt` verifies:
  * Keypads and navigation render cleanly without clipping under 1.5x, 1.75x, and 2.0x font scaling.
  * Interactive touch targets meet or exceed 48dp (keypad keys: >= 48dp x 48dp; "I'm OK" button: 200dp; SOS button: 200dp).
  * Semantics and content descriptions are present for TalkBack screen reader navigation across all senior screens.
