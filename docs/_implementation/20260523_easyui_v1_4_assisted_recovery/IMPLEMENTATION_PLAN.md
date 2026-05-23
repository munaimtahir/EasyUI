# EasyUI V1.4 Assisted Recovery Implementation Plan

## Objective
Enable guided recovery for detected phone health issues, helping seniors and caregivers resolve problems proactively.

## 1. Domain Enhancements
- **Recovery Models**: Define `RecoveryAction` (Enum: OPEN_WIFI, OPEN_BATTERY, SET_DEFAULT_LAUNCHER, etc.) and `RecoveryGuidance`.
- **Logic Update**: In `GuardianRules`, map each critical/warning check to a specific `RecoveryAction`.
- **State Update**: Update `GuardianCheckResult` and `PhoneHealthState` to include recommended recovery actions.

## 2. Navigation Update
- **Routes**: Add `AssistedRecovery` route.
- **NavGraph**: Implement navigation to `AssistedRecoveryScreen` and handle the execution of system intents for recovery actions.

## 3. UI Enhancements (Senior)
- **PhoneHealthCard**: Make it clickable on the Home screen to open the troubleshooting flow.
- **AssistedRecoveryScreen**: A new, high-contrast, large-text screen that:
    - Explains the specific problem in simple terms.
    - Provides a "Guided Fix" button (e.g., "Fix Wi-Fi", "Set as Home").
    - Provides an "Alert Caregiver" fallback.
    - Provides a "Back to Home" option.

## 4. Assisted Recovery Flows
- **Battery Critical**: Prompt to connect charger. Action: None (system info).
- **No Internet**: Action: Open Wi-Fi settings.
- **EasyUI Not Default**: Action: Open Default Home settings.
- **Setup Incomplete**: Action: Open Caregiver Tools (requires PIN).
- **Emergency Contact Missing**: Action: Open Caregiver Tools -> Emergency Settings.

## 5. Verification Strategy
- **Unit Tests**: Update `GuardianRulesTest` to verify correct recovery action mapping.
- **Build**: Ensure `./gradlew assembleDebug` passes.
- **Logic**: Verify navigation routing in `EasyUiNavGraph`.

## Risks and Mitigations
- **Intent Failure**: Some system settings might not exist on all devices. *Mitigation*: Fallback to "Open Settings" or show a "Manual Instruction" screen.
- **PIN Loop**: Accessing caregiver recovery items from senior mode must still respect PIN protection.
