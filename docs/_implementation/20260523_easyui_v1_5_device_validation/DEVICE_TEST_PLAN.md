# EasyUI V1.5 Device Test Plan

## Objective
Verify the real-world behavior of EasyUI on physical Android hardware.

## Test Case 1: Critical Battery Alert (V1.3/V1.4)
- **Pre-condition**: Battery is at 15%.
- **Action**: Use `adb shell dumpsys battery set level 5`.
- **Expected Result**: 
    - `PhoneHealthCard` shows "Please charge phone".
    - `SeniorAlertBanner` appears on Home screen.
    - Tapping alert opens Share Sheet with status link.
    - Tapping health card opens `AssistedRecoveryScreen`.

## Test Case 2: No Internet Recovery (V1.4)
- **Pre-condition**: Wi-Fi and Data are ON.
- **Action**: Disable Wi-Fi and Data.
- **Expected Result**:
    - `PhoneHealthCard` shows "Internet is off".
    - Tapping health card opens `AssistedRecoveryScreen`.
    - Tapping "Check Internet" opens System Wi-Fi settings.

## Test Case 3: Launcher Handoff (V1.1/V1.4)
- **Pre-condition**: EasyUI is the default launcher.
- **Action**: Go to Android Settings and switch default home to "Pixel Launcher" or "OneUI Home". Return to EasyUI.
- **Expected Result**:
    - `PhoneHealthCard` shows "EasyUI not set as home".
    - `AssistedRecoveryScreen` provides "Set as Main Home" button.
    - Tapping button opens System Default Apps settings.

## Test Case 4: Remote Link Deep Link (V1.2)
- **Pre-condition**: App is closed.
- **Action**: `adb shell am start -a android.intent.action.VIEW -d "easyui://status?d=<TEST_PACKET>"`
- **Expected Result**:
    - EasyUI opens directly to "Linked Phones" screen.
    - Device is added/updated in the list.

## Test Case 5: UX Visual Check
- **Verification**: 
    - Large text scaling (Accessibility settings).
    - Contrast in Senior Home Gradient vs White Text.
    - Tap target size (min 48dp) for all new buttons.
    - Haptic feedback on Caregiver Access (long press).

## ADB Simulation Commands
- **Battery**: `adb shell dumpsys battery set level <n>`
- **Deep Link**: `adb shell am start -W -a android.intent.action.VIEW -d "easyui://status?d=..." com.easyui.launcher`
- **Reset Battery**: `adb shell dumpsys battery reset`
