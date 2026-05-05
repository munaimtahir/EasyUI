#!/usr/bin/env bash
set -euo pipefail

PACKAGE_NAME="${1:-com.easyui.launcher.debug}"
EVIDENCE_DIR="${ADB_EVIDENCE_DIR:-build/github-emulator-evidence}"
mkdir -p "$EVIDENCE_DIR"

{
  echo "# ADB Evidence Summary"
  echo
  echo "## Date"
  date -u
  echo
  echo "## Devices"
  adb devices -l || true
  echo
  echo "## Focus"
  adb shell dumpsys window | grep -E "mCurrentFocus|mFocusedApp" || true
  echo
  echo "## Package path"
  adb shell pm path "$PACKAGE_NAME" || true
  echo
  echo "## Package dumpsys excerpt"
  adb shell dumpsys package "$PACKAGE_NAME" | sed -n '1,220p' || true
} > "$EVIDENCE_DIR/adb_evidence_summary.md"

adb shell uiautomator dump /sdcard/window-final.xml >/dev/null 2>&1 || true
adb pull /sdcard/window-final.xml "$EVIDENCE_DIR/window-final.xml" >/dev/null 2>&1 || true
adb exec-out screencap -p > "$EVIDENCE_DIR/emulator-final-state.png" || true
adb bugreport "$EVIDENCE_DIR/bugreport.zip" >/dev/null 2>&1 || true
adb logcat -d -v time > "$EVIDENCE_DIR/logcat_final_full.txt" || true
adb logcat -b crash -d -v time > "$EVIDENCE_DIR/logcat_crash_buffer.txt" || true

echo "Evidence collection complete under $EVIDENCE_DIR"
