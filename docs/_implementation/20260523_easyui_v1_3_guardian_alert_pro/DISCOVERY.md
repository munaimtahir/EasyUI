# EasyUI V1.3 Guardian Alert Pro Discovery Report

## Overview
V1.3 makes the Guardian Checks proactive. Instead of just showing status on the home screen, the app will now actively prompt the senior to notify their caregiver when critical issues are detected.

## Critical Alert Triggers
The following conditions will trigger a proactive "Alert Caregiver" prompt:
1. **Critical Battery**: Battery level below the configured critical threshold (default 10%).
2. **Prolonged No Internet**: Internet has been disconnected for longer than the configured delay (default 30 mins). *Note: In this local-first sprint, we will trigger the alert as soon as the internet is off if the check is enabled, to keep implementation simple and immediate.*
3. **EasyUI Not Default**: If the senior accidentally switches launchers, the next time they return to EasyUI, they will be prompted to fix it or alert the caregiver.
4. **Emergency Contact Missing**: If no emergency number is configured.

## Senior User Experience
- **Alert Banner**: A high-visibility, high-contrast banner will appear on the Senior Home screen when a critical state is detected.
- **Single Action**: The banner will have one primary button: "Alert Caregiver".
- **Interaction Flow**:
    1. Senior taps "Alert Caregiver".
    2. App automatically generates the encoded status packet (from V1.2).
    3. App opens the Android Share Sheet with a pre-filled message: *"My phone needs attention: [EasyUI Status Link]"*.
    4. Senior selects their caregiver from the share sheet (or favorite messaging app).

## Logic Implementation
- `GuardianRules`: Add `shouldPromptAlert` flag to `PhoneHealthState`.
- `HomeViewModel`: Observe health state and manage the alert visibility.
- `HomeScreen`: Render the `SeniorAlertBanner` component.

## Limitations
- **Notification-less**: Since we are avoiding background services and notification shade interference in this sprint, the alert is only visible when the senior is on the Home screen.
- **Manual Share**: The senior still needs to select the recipient in the share sheet, maintaining user control and avoiding "automatic SMS" policy risks.
