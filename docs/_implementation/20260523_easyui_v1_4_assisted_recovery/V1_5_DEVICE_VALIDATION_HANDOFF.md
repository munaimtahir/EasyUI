# EasyUI V1.5 Device Validation Handoff Plan

## Objective
Validate the synchronized launcher (V1.1), remote link (V1.2), proactive alerts (V1.3), and assisted recovery (V1.4) on physical Android devices.

## 1. Local Logic Validation
- **Trigger**: Lower battery to < 20% and < 10%.
- **Verify**: Correct message in `PhoneHealthCard` and appearance of `SeniorAlertBanner` at 10%.
- **Action**: Tap banner, verify share sheet opens with link.
- **Action**: Tap health card, verify `AssistedRecoveryScreen` opens.

## 2. Recovery Action Validation
- **Trigger**: Turn off Wi-Fi.
- **Action**: Open recovery screen, tap "Check Internet".
- **Verify**: System Wi-Fi settings open correctly.
- **Trigger**: Switch to standard launcher.
- **Action**: Return to EasyUI, open recovery, tap "Set as Main Home".
- **Verify**: System Home settings open.

## 3. Remote Link Validation
- **Device A (Senior)**: Share status link via SMS to Device B.
- **Device B (Caregiver)**: Tap link in SMS.
- **Verify**: EasyUI opens, "Linked Phones" screen shows Device A.
- **Verify**: Detailed status matches Device A's current state.

## 4. UI/UX Consistency
- Verify font scaling (Accessibility) works on all new screens.
- Verify haptic feedback on long-press (Caregiver access).

## ADB Helper Commands
- `adb shell dumpsys battery set level 5`: Force critical battery.
- `adb shell am start -a android.intent.action.VIEW -d "easyui://status?d=..."`: Test deep link.
