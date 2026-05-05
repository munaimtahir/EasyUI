#!/usr/bin/env bash
set -euo pipefail

PACKAGE_NAME="${1:-com.easyui.launcher.debug}"
MAIN_ACTIVITY="${2:-com.easyui.launcher.MainActivity}"
EVIDENCE_DIR="${ADB_EVIDENCE_DIR:-build/github-emulator-evidence}"
SCREENSHOT_DIR="$EVIDENCE_DIR/screenshots"
UI_DIR="$EVIDENCE_DIR/ui-dumps"
mkdir -p "$SCREENSHOT_DIR" "$UI_DIR"

capture() {
  local name="$1"
  adb exec-out screencap -p > "$SCREENSHOT_DIR/${name}.png" || true
  adb shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
  adb pull /sdcard/window.xml "$UI_DIR/${name}.xml" >/dev/null 2>&1 || true
}

tap_text_or_desc() {
  local needle="$1"
  adb shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
  adb pull /sdcard/window.xml "$UI_DIR/current.xml" >/dev/null 2>&1 || return 1
  python3 - "$UI_DIR/current.xml" "$needle" <<'PY'
import re, sys, xml.etree.ElementTree as ET
path, needle = sys.argv[1], sys.argv[2].lower()
try:
    root = ET.parse(path).getroot()
except Exception:
    sys.exit(1)
for node in root.iter('node'):
    text = (node.attrib.get('text') or '').lower()
    desc = (node.attrib.get('content-desc') or '').lower()
    if needle in text or needle in desc:
        b = node.attrib.get('bounds', '')
        m = re.match(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', b)
        if m:
            x1, y1, x2, y2 = map(int, m.groups())
            print((x1+x2)//2, (y1+y2)//2)
            sys.exit(0)
sys.exit(1)
PY
}

tap_if_present() {
  local label="$1"
  local coords
  if coords="$(tap_text_or_desc "$label" 2>/dev/null)"; then
    adb shell input tap $coords || true
    sleep 3
    return 0
  fi
  return 1
}

adb wait-for-device
adb shell input keyevent 82 || true
adb shell am start -W -n "$PACKAGE_NAME/$MAIN_ACTIVITY" >/dev/null 2>&1 || true
sleep 5
capture "01_launch"

adb shell input keyevent HOME || true
sleep 4
capture "02_home"

# Best-effort onboarding captures using stable visible text/content descriptions.
# These do not fail the workflow because exact labels may change during active development.
for label in "Get started" "Start" "Continue" "Next" "Skip"; do
  if tap_if_present "$label"; then
    safe_label="$(echo "$label" | tr '[:upper:] ' '[:lower:]_' | tr -cd 'a-z0-9_')"
    capture "onboarding_${safe_label}"
  fi
done

# Best-effort caregiver/settings captures using stable screen labels.
# Proper regression should be covered by Compose/Espresso/UIAutomator tests, not raw coordinates.
for label in "Settings" "Caregiver" "Home Layout" "Allowed Apps" "Emergency" "Security" "Device" "Support"; do
  if tap_if_present "$label"; then
    safe_label="$(echo "$label" | tr '[:upper:] ' '[:lower:]_' | tr -cd 'a-z0-9_')"
    capture "screen_${safe_label}"
    adb shell input keyevent BACK || true
    sleep 2
  fi
done

capture "99_final_state"

echo "Screenshot capture complete. Files saved under $SCREENSHOT_DIR"
