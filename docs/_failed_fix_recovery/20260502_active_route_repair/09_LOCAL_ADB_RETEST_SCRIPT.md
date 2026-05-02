## Local ADB Retest Script (2026-05-02)

### Purpose
Provide a repeatable local device retest that launches the launcher app using an explicit activity start (not `monkey`).

### Script
- `scripts/easyui_final_device_retest.sh`

### Defaults (as requested)
- `ADB_SERIAL="08357252AE006901"`
- `PACKAGE_NAME="com.easyui.launcher.debug"`
- Launch command uses:
  - `adb -s "$ADB_SERIAL" shell am start -n "$PACKAGE_NAME/com.easyui.launcher.MainActivity"`

### Output
Creates a timestamped evidence folder:
- `device_test_runs/<timestamp>/`

Captures:
- first-launch screenshot (best effort)
- logcat dump
- simple crash scan
- `FINAL_DEVICE_RETEST_SUMMARY.md` with a manual screenshot checklist

### How to run
From repo root:
- `bash scripts/easyui_final_device_retest.sh 08357252AE006901`

### NOT RUN
- `NOT RUN — requires local ADB` (must be executed on a machine connected to the physical device).

