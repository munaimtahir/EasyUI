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
