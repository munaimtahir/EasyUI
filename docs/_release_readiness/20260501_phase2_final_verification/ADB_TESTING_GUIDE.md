# ADB Testing Guide — Phase 2 Release Verification

**Date**: 2026-05-01  
**Project**: EasyUI Senior Launcher  
**Phase**: 2 (Caregiver Safety + Stability Pack)  
**Status**: ✅ COMMITTED TO GITHUB — Ready for Device Testing

---

## Prerequisites

Before testing on Android devices, ensure:

- ✅ Phase 2 code committed and pushed to GitHub
- ✅ Android devices available (phones/tablets)
- ✅ Android SDK tools installed
- ✅ USB cable and drivers installed
- ✅ Developer mode enabled on test devices
- ✅ Android Studio or command-line tools available

---

## Quick Setup

### 1. Clone Latest Code

```bash
# Clone or update your local repository
git clone https://github.com/munaimtahir/EasyUI.git
cd EasyUI
git checkout main
git pull origin main

# Verify Phase 2 is present
git log --oneline -5
```

Expected output shows:
```
e8d6b3d Phase 2: Caregiver Safety + Stability Pack
9adea13 Merge remote-main...
```

### 2. Build APK for Testing

```bash
# Build debug APK (faster, better for testing)
./gradlew :app:assembleDebug

# Expected: BUILD SUCCESSFUL
# Output: app/build/outputs/apk/debug/app-debug.apk
```

Or for release APK:

```bash
# Build release APK (optimized, for Play Store)
./gradlew :app:assembleRelease

# Expected: BUILD SUCCESSFUL
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### 3. Connect Android Device via USB

```bash
# List connected devices
adb devices

# Expected output:
# List of attached devices
# device-id    device
```

If no devices appear:
- Enable Developer Mode (Settings → About → Build Number × 7)
- Enable USB Debugging (Settings → Developer Options → USB Debugging)
- Reconnect USB cable

---

## Installation

### Install Debug APK on Device

```bash
# Install to first connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Expected: Success

# Or install to specific device
adb -s device-id install app/build/outputs/apk/debug/app-debug.apk
```

### Verify Installation

```bash
# List installed packages
adb shell pm list packages | grep easyui

# Expected: com.easyui.launcher

# Get app info
adb shell dumpsys package com.easyui.launcher
```

---

## Launch App

### Start App on Device

```bash
# Launch the app
adb shell am start -n com.easyui.launcher/.MainActivity

# Expected: App opens on device

# Or with specific activity
adb shell am start com.easyui.launcher/com.easyui.launcher.MainActivity
```

### Check for Crashes

```bash
# View system log
adb logcat | grep -i "easyui\|error\|exception"

# Or save to file for later analysis
adb logcat > logcat.txt &

# Let it run while testing, then Ctrl+C to stop
```

---

## Phase 2 Feature Testing via ADB

### 1. Caregiver PIN Protection

**Test via ADB**:
```bash
# Launch app
adb shell am start -n com.easyui.launcher/.MainActivity

# Check for PIN entry screen
adb logcat | grep -i "PIN\|caregiver"

# Manually test on device:
# - Tap "Enter Caregiver Mode"
# - Enter PIN (default: 1234 if first launch)
# - Verify caregiver dashboard appears
```

### 2. Session Timeout

**Test via ADB**:
```bash
# Monitor timeout events in logs
adb logcat | grep -i "session\|timeout"

# Expected: Logs showing:
# - "Session active" at start
# - "Warning at 13 min"
# - "Auto-logout at 15 min"

# Manually test on device:
# - Enter caregiver mode
# - Wait 13 minutes
# - Watch for timeout warning
# - At 15 min, should auto-logout
```

### 3. Lock Icons Display

**Test via ADB**:
```bash
# Check icon rendering in logs
adb logcat | grep -i "lock\|icon"

# Manually test on device:
# - Enter caregiver mode
# - Go to "Security & Lock"
# - Toggle "Lock Layout"
# - Return to home screen
# - Verify 🔒 icons appear on tiles
```

### 4. Multi-Page Rendering

**Test via ADB**:
```bash
# Check page navigation in logs
adb logcat | grep -i "page\|render\|navigation"

# Manually test on device:
# - If multiple pages enabled
# - Look for page indicator dots at bottom
# - Tap Previous/Next buttons
# - Verify tiles change per page
```

### 5. Layout Editor

**Test via ADB**:
```bash
# Monitor layout changes
adb logcat | grep -i "layout\|edit\|save"

# Manually test on device:
# - Enter caregiver mode
# - Go to "Home Layout"
# - Verify visual preview of layout
# - Rearrange tiles
# - Tap "Save Layout"
# - Verify changes on senior home
```

### 6. App Visibility Control

**Test via ADB**:
```bash
# Check app filtering
adb logcat | grep -i "app\|visibility\|hidden"

# Manually test on device:
# - Go to "Allowed Apps"
# - Uncheck some apps
# - Go to senior home
# - Verify unchecked apps don't appear
```

### 7. Backup/Restore

**Test via ADB**:
```bash
# Monitor backup events
adb logcat | grep -i "backup\|restore\|export"

# Manually test on device:
# - Go to "Backup/Restore"
# - Tap "Backup Layout"
# - Verify backup file created
# - Change layout
# - Tap "Restore"
# - Verify restored to original
```

---

## Comprehensive Test Checklist

### Device Setup
```bash
☐ Clone latest code from GitHub
☐ Build debug APK successfully
☐ Connect device via USB
☐ Install APK successfully
☐ App launches without crash
```

### First Run
```bash
☐ Onboarding screen appears
☐ PIN setup completes
☐ Default launcher prompt shows
☐ Senior home screen loads
☐ Caregiver mode accessible
```

### Caregiver Features
```bash
☐ PIN protection works
☐ Dashboard loads (5 sections visible)
☐ Home Layout editor loads
☐ Lock toggle works
☐ Icons appear when locked (🔒)
☐ App filtering works
☐ Backup/restore functions
☐ Emergency contacts load
```

### Navigation
```bash
☐ Back button safe (doesn't exit)
☐ Home key returns to launcher
☐ Tab navigation smooth
☐ No stuck dialogs
☐ Orientation changes work
```

### Session Timeout
```bash
☐ Session stays active with activity
☐ Warning appears at ~13 min
☐ Auto-logout at ~15 min
☐ Re-entry requires PIN
```

### Multi-Page (if enabled)
```bash
☐ Page indicators visible
☐ Previous/Next buttons work
☐ Correct tiles per page
☐ Page navigation smooth
☐ No blank pages
```

### Performance
```bash
☐ App loads quickly
☐ No ANR (Application Not Responding)
☐ No lag in navigation
☐ No memory leaks visible
```

### Stability
```bash
☐ No crashes on cold start
☐ No crashes on warm start
☐ Config persists after restart
☐ Layout saved correctly
☐ No orphaned settings
```

---

## Testing Commands Cheat Sheet

```bash
# Installation & Launch
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.easyui.launcher/.MainActivity
adb shell am force-stop com.easyui.launcher
adb shell pm uninstall com.easyui.launcher

# Logging
adb logcat
adb logcat | grep -i "easyui"
adb logcat > logcat-phase2.txt &

# Device Info
adb devices
adb shell getprop ro.build.version.release
adb shell getprop ro.product.model

# App Info
adb shell dumpsys package com.easyui.launcher
adb shell pm list packages | grep easyui
adb shell pm dump com.easyui.launcher

# Settings
adb shell settings get secure default_launcher_package
adb shell settings set secure default_launcher_package com.easyui.launcher
adb shell settings list global | grep launcher

# File Access
adb push local-file device:/data/local/tmp/
adb pull device:/data/local/tmp/file local-file
adb shell ls -la /data/data/com.easyui.launcher/

# Database Access
adb shell run-as com.easyui.launcher cat /data/data/com.easyui.launcher/databases/database_name

# Memory
adb shell dumpsys meminfo com.easyui.launcher
adb shell ps | grep easyui
```

---

## Multi-Device Testing

### Test on Multiple Devices Simultaneously

```bash
# List all connected devices
adb devices

# Install on all devices
for device in $(adb devices | tail -n +2 | awk '{print $1}'); do
  echo "Installing on $device..."
  adb -s $device install app/build/outputs/apk/debug/app-debug.apk
done

# Launch on all devices
for device in $(adb devices | tail -n +2 | awk '{print $1}'); do
  echo "Launching on $device..."
  adb -s $device shell am start -n com.easyui.launcher/.MainActivity
done

# Collect logs from all devices
for device in $(adb devices | tail -n +2 | awk '{print $1}'); do
  echo "Collecting logs from $device..."
  adb -s $device logcat > logcat-${device}.txt &
done
```

---

## Recommended Device Configurations

### Must Test On

- ✅ Android 8.0+ (API 26+)
- ✅ Android 14.0+ (API 34+)
- ✅ Android 15.0+ (API 35+)

### Preferred Test Mix

1. **Phone with small screen** (4-5")
   - Test: Touch target sizing, layout stability

2. **Phone with large screen** (6-7")
   - Test: Multi-page rendering, layout coverage

3. **Tablet** (7-10")
   - Test: landscape orientation, large tiles

4. **Different OEMs** (if possible)
   - Samsung, Google Pixel, OnePlus, etc.
   - Different launchers/overlays can affect behavior

---

## Issue Reporting

### If You Find Issues

```bash
# Collect diagnostic info
adb logcat > logcat-full.txt
adb shell dumpsys package com.easyui.launcher > package-info.txt
adb shell dumpsys meminfo com.easyui.launcher > memory-info.txt

# Report with:
1. Device: Model, Android version
2. Action: What did you do?
3. Expected: What should happen?
4. Actual: What happened instead?
5. Logs: Attach logcat-full.txt
6. Screenshots: If visual issue

# Example crash:
adb logcat | grep -i "exception\|crash\|fatal"
```

---

## Performance Monitoring

### Check Battery Impact

```bash
# Battery stats while running session timeout
adb shell dumpsys batterystats --reset
adb shell am start -n com.easyui.launcher/.MainActivity
# Wait 5 minutes
adb shell dumpsys batterystats > battery-report.txt

# Check timeout polling frequency
adb logcat | grep -i "timeout\|poll" | wc -l
```

### Memory Profiling

```bash
# Start monitoring
adb shell dumpsys meminfo com.easyui.launcher --local

# Or via Android Studio Profiler for real-time data
# (see Android Studio documentation)
```

---

## Clean Up

### After Testing

```bash
# Uninstall from device
adb uninstall com.easyui.launcher

# Uninstall from all devices
for device in $(adb devices | tail -n +2 | awk '{print $1}'); do
  adb -s $device uninstall com.easyui.launcher
done

# Kill logcat process
pkill -f "adb logcat"

# Remove temporary files
rm -f logcat*.txt
rm -f *.txt
```

---

## Success Criteria

Phase 2 testing is SUCCESSFUL if:

✅ **Stability**
- App installs without error
- App launches without crash
- No ANRs during testing
- App survives home button press

✅ **Features**
- PIN protection works
- Session timeout triggers (13+15 min)
- Lock icons display
- Layout editor functional
- Multi-page navigation works
- App filtering works
- Backup/restore functional

✅ **Performance**
- < 100MB memory use
- Responsive UI (no lag)
- Session timeout ≤ 5% CPU

✅ **Safety**
- No crash loops
- No data loss
- Layout persists after reboot
- No security issues

---

## Next Steps

1. **Build & Install** (5 min)
   - Follow "Quick Setup" section above

2. **Run Checklist** (30 min)
   - Test all features listed above

3. **Monitor Logs** (5 min)
   - Watch for errors/crashes

4. **Report Results** (5 min)
   - Note any issues found
   - Provide logs if crash occurs

5. **Ready for Play Store** (if all tests pass)
   - Code is ready for internal testing release

---

## Troubleshooting

### App Won't Install

```bash
# Check if already installed
adb shell pm list packages | grep easyui

# Uninstall first
adb uninstall com.easyui.launcher

# Try again
adb install app/build/outputs/apk/debug/app-debug.apk
```

### App Crashes on Launch

```bash
# Check logs
adb logcat | grep -i "exception\|crash"

# Common issues:
# 1. Wrong Android version
# 2. Missing permissions
# 3. DataStore corruption (clear app data)
adb shell pm clear com.easyui.launcher
```

### ADB Not Recognizing Device

```bash
# Restart ADB
adb kill-server
adb start-server
adb devices

# Check USB connection
# - Unplug and re-plug cable
# - Use different USB port
# - Try different cable
```

### Stuck on PIN Screen

```bash
# Clear app data
adb shell pm clear com.easyui.launcher

# Uninstall and reinstall
adb uninstall com.easyui.launcher
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Support

**For questions**: Check logs first
```bash
adb logcat | grep -E "Exception|Error|Crash"
```

**For issues**: Open issue on GitHub with:
- Device info (model, Android version)
- Reproduction steps
- Attached logcat output

---

## Summary

✅ Phase 2 code is committed to GitHub  
✅ Ready for device testing  
✅ Use this guide for comprehensive testing  
✅ Report any issues found  

**Get started**: `git pull && ./gradlew :app:assembleDebug`

---
