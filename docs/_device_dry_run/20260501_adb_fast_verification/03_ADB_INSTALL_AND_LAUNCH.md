# ADB Install and Launch

Serial: 08357252AE006901

## APK

- Debug APK path:
- Package name (applicationId):
- Launcher activity resolve:

## Install

Command: `adb -s 08357252AE006901 install -r <apk>`

Result: TBD

## Launch

Command: `adb -s 08357252AE006901 shell monkey -p <package> -c android.intent.category.LAUNCHER 1`

Result: TBD

Evidence:

- Screenshot: `home_launch.png`
- Logcat: `logcat_after_launch.txt`
