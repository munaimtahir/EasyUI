#!/usr/bin/env bash
set -euo pipefail

APK_PATH="${1:?Usage: adb_smoke_test.sh APK_PATH PACKAGE_NAME MAIN_ACTIVITY}"
PACKAGE_NAME="${2:-com.easyui.launcher.debug}"
MAIN_ACTIVITY="${3:-com.easyui.launcher.MainActivity}"
EVIDENCE_DIR="${ADB_EVIDENCE_DIR:-build/github-emulator-evidence}"
mkdir -p "$EVIDENCE_DIR/screenshots"

LOGCAT_FULL="$EVIDENCE_DIR/logcat_after_launch_full.txt"
LOGCAT_CRASH="$EVIDENCE_DIR/logcat_crash_scan.txt"
FOCUS_FILE="$EVIDENCE_DIR/focused_activity.txt"

if [[ ! -f "$APK_PATH" ]]; then
  echo "APK not found at: $APK_PATH" | tee "$EVIDENCE_DIR/adb_smoke_failure.txt"
  exit 2
fi

adb wait-for-device
adb devices -l | tee "$EVIDENCE_DIR/adb_devices.txt"

# Reduce animation-related flakiness.
adb shell settings put global window_animation_scale 0 || true
adb shell settings put global transition_animation_scale 0 || true
adb shell settings put global animator_duration_scale 0 || true
adb shell input keyevent 82 || true

adb logcat -c || true

echo "Installing APK: $APK_PATH" | tee "$EVIDENCE_DIR/adb_install.txt"
adb install -r "$APK_PATH" | tee -a "$EVIDENCE_DIR/adb_install.txt"

COMPONENT="$PACKAGE_NAME/$MAIN_ACTIVITY"
echo "Launching component: $COMPONENT" | tee "$EVIDENCE_DIR/adb_launch.txt"
if ! adb shell am start -W -n "$COMPONENT" | tee -a "$EVIDENCE_DIR/adb_launch.txt"; then
  echo "Direct component launch failed. Capturing resolver info and trying monkey fallback." | tee -a "$EVIDENCE_DIR/adb_launch.txt"
  adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME -p "$PACKAGE_NAME" | tee "$EVIDENCE_DIR/home_resolve_activity.txt" || true
  adb shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1 | tee -a "$EVIDENCE_DIR/adb_launch.txt" || true
fi

sleep 8

{
  echo "## dumpsys window focus"
  adb shell dumpsys window | grep -E "mCurrentFocus|mFocusedApp" || true
  echo
  echo "## dumpsys activity focus"
  adb shell dumpsys activity activities | grep -E "mResumedActivity|topResumedActivity|ResumedActivity" || true
} | tee "$FOCUS_FILE"

adb exec-out screencap -p > "$EVIDENCE_DIR/screenshots/01_launch_after_install.png" || true

# Launcher-specific check: try setting/using app as HOME on emulator. This is important for EasyUI.
echo "Checking launcher/home resolution" | tee "$EVIDENCE_DIR/home_launcher_check.txt"
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME -p "$PACKAGE_NAME" | tee -a "$EVIDENCE_DIR/home_launcher_check.txt" || true
adb shell cmd package set-home-activity "$COMPONENT" | tee -a "$EVIDENCE_DIR/home_launcher_check.txt" || true
adb shell input keyevent HOME || true
sleep 5
adb shell dumpsys window | grep -E "mCurrentFocus|mFocusedApp" | tee -a "$EVIDENCE_DIR/home_launcher_check.txt" || true
adb exec-out screencap -p > "$EVIDENCE_DIR/screenshots/02_after_home_key.png" || true

# Basic back/home stability.
adb shell input keyevent BACK || true
sleep 2
adb shell input keyevent HOME || true
sleep 4
adb exec-out screencap -p > "$EVIDENCE_DIR/screenshots/03_after_back_home_stability.png" || true

adb logcat -d -v time > "$LOGCAT_FULL" || true

# Crash gate. Keep package-specific patterns to reduce false positives from emulator/system noise.
if grep -E "FATAL EXCEPTION|ANR in ${PACKAGE_NAME}|Process: ${PACKAGE_NAME}|am_crash.*${PACKAGE_NAME}|Force finishing.*${PACKAGE_NAME}|has died.*${PACKAGE_NAME}" "$LOGCAT_FULL" > "$LOGCAT_CRASH"; then
  echo "Crash/ANR pattern detected. See $LOGCAT_CRASH"
  cat "$LOGCAT_CRASH"
  exit 1
fi

echo "ADB smoke test passed: install, launch, focus capture, home key, screenshots, and crash scan completed." | tee "$EVIDENCE_DIR/adb_smoke_pass.txt"
