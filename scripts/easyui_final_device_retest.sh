#!/usr/bin/env bash
set -euo pipefail

ADB_SERIAL_DEFAULT="08357252AE006901"
PACKAGE_NAME_DEFAULT="com.easyui.launcher.debug"
MAIN_ACTIVITY_DEFAULT="com.easyui.launcher.MainActivity"

ADB_SERIAL="${1:-${ADB_SERIAL:-$ADB_SERIAL_DEFAULT}}"
PACKAGE_NAME="${PACKAGE_NAME:-$PACKAGE_NAME_DEFAULT}"
MAIN_ACTIVITY="${MAIN_ACTIVITY:-$MAIN_ACTIVITY_DEFAULT}"

timestamp="$(date -u +%Y%m%d_%H%M%S)"
out_dir="device_test_runs/$timestamp"
mkdir -p "$out_dir"

fail() {
  echo "ERROR: $*" >&2
  exit 1
}

command -v adb >/dev/null 2>&1 || fail "adb not found on PATH"
command -v ./gradlew >/dev/null 2>&1 || true

echo "EasyUI final device retest run"
echo "UTC timestamp: $timestamp"
echo "ADB_SERIAL: $ADB_SERIAL"
echo "PACKAGE_NAME: $PACKAGE_NAME"
echo "MAIN_ACTIVITY: $MAIN_ACTIVITY"
echo

echo "Checking device connection..."
adb -s "$ADB_SERIAL" get-state >/dev/null 2>&1 || fail "device $ADB_SERIAL not connected (run: adb devices)"

echo "Stopping gradle daemon..."
./gradlew --stop >/dev/null 2>&1 || true

echo "Building debug APK..."
./gradlew :app:assembleDebug --stacktrace

apk_path="app/build/outputs/apk/debug/app-debug.apk"
[[ -f "$apk_path" ]] || fail "APK not found at $apk_path"

echo "Installing debug APK..."
adb -s "$ADB_SERIAL" install -r "$apk_path" | tee "$out_dir/00_install_output.txt"

echo "Clearing app data..."
adb -s "$ADB_SERIAL" shell pm clear "$PACKAGE_NAME" | tee "$out_dir/01_pm_clear.txt"

echo "Capturing logcat baseline..."
adb -s "$ADB_SERIAL" logcat -c || true

echo "Launching via explicit activity (launcher apps are not started via monkey)..."
adb -s "$ADB_SERIAL" shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY" | tee "$out_dir/02_am_start.txt"

sleep 2

echo "Capturing first-launch screenshot..."
adb -s "$ADB_SERIAL" exec-out screencap -p > "$out_dir/03_first_launch.png" || true

echo "Capturing logcat..."
adb -s "$ADB_SERIAL" logcat -d > "$out_dir/04_logcat.txt" || true

echo "Crash scan (best effort)..."
{ rg -n "FATAL EXCEPTION|AndroidRuntime" "$out_dir/04_logcat.txt" || true; } > "$out_dir/05_crash_scan.txt" || true

cat > "$out_dir/FINAL_DEVICE_RETEST_SUMMARY.md" <<EOF
## EasyUI Final Device Retest Summary

- UTC timestamp: $timestamp
- Device serial: $ADB_SERIAL
- Package: $PACKAGE_NAME
- Launch: \`adb -s "$ADB_SERIAL" shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY"\`

### Captured artifacts
- \`03_first_launch.png\` (best effort)
- \`04_logcat.txt\`
- \`05_crash_scan.txt\`

### Manual screenshot checklist (capture on device and save into this folder)
1) Onboarding intro scroll (bottom copy visible above CTA)
2) Protection Options screen
3) Theme Picker screen
4) Permissions Explanation screen
5) Senior home (first page)
6) Swipe paging (page 1 → page 2)
7) Four-tap caregiver entry on clock/time
8) Layout editor placement (select slot → place app)
9) Saved home layout after placement
10) Force-stop + relaunch stability

### Notes
- If any item fails, include a short description and add a screenshot + logcat excerpt.
EOF

echo
echo "Done. Evidence folder:"
echo "  $out_dir"
echo
echo "Next:"
echo "  - Add manual screenshots to $out_dir/"
echo "  - Zip the folder and upload it back to this thread"

