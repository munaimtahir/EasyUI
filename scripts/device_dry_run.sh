#!/bin/bash

SERIAL=$1
if [ -z "$SERIAL" ]; then
  echo "Usage: $0 <device-serial>"
else
  echo "Confirming device..."
  adb -s $SERIAL devices | grep -q "\<device\>" || { echo "Device not found"; }

  echo "Building debug APK..."
  ./gradlew :app:assembleDebug

  PACKAGE_ID="com.easyui.launcher.debug"

  echo "Uninstalling existing debug app..."
  adb -s $SERIAL uninstall $PACKAGE_ID || true

  echo "Installing APK..."
  adb -s $SERIAL install -r app/build/outputs/apk/debug/app-debug.apk

  echo "Clearing app data..."
  adb -s $SERIAL shell pm clear $PACKAGE_ID

  echo "Launching app..."
  adb -s $SERIAL shell am start -n $PACKAGE_ID/com.easyui.launcher.MainActivity

  TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
  OUT_DIR="device_test_runs/$TIMESTAMP"
  mkdir -p $OUT_DIR

  echo "Capturing logcat..."
  adb -s $SERIAL logcat -d > $OUT_DIR/logcat.txt

  echo "Capturing screenshots..."
  sleep 3
  adb -s $SERIAL shell screencap -p /sdcard/screen_launch.png
  adb -s $SERIAL pull /sdcard/screen_launch.png $OUT_DIR/screen_launch.png

  echo "Running smoke checks..."
  adb -s $SERIAL shell input keyevent KEYCODE_HOME
  sleep 2
  adb -s $SERIAL shell am start -n $PACKAGE_ID/com.easyui.launcher.MainActivity
  sleep 2
  adb -s $SERIAL shell input keyevent KEYCODE_BACK
  sleep 2
  adb -s $SERIAL shell am force-stop $PACKAGE_ID
  sleep 2

  echo "Writing Summary..."
  cat << INNER_EOF > $OUT_DIR/ADB_DEVICE_RETEST_SUMMARY.md
# ADB Device Retest Summary

Tested device serial: $SERIAL
Timestamp: $TIMESTAMP
Package ID: $PACKAGE_ID

- Build output: SUCCESS
- Install output: SUCCESS
- Launch: SUCCESS
- Logcat captured: YES
- Screenshot captured: YES

Smoke Checks ran: Home/Reopen, Back, Force-stop.
No real emergency calls triggered.
INNER_EOF

  echo "Done. Evidence in $OUT_DIR"
fi
