# Senior Launcher & Caregiver Ecosystem Product Suite

This product suite is built on top of the Core Launcher v0.1 baseline, providing a simplified senior-friendly smartphone launcher with remote monitoring and care capabilities for caregivers.

## Product Modules

* **`:senior-launcher`**: The senior user's application. A fully offline-capable, highly readable Android launcher featuring simplified contact discovery, widgets (clock, date, battery, note, local reminders), and a secure SOS emergency screen.
* **`:caregiver-companion`**: A separate application used by the caregiver to securely pair with a senior's launcher, monitor device status (online status, battery level, missed check-ins, active alerts), manage remote configuration (reminders, contact shortcuts, home apps), and receive SOS alerts.
* **`:backend`**: A secure remote service providing authentication, pairing orchestration, telemetry sync, alert routing, and remote configuration transport.

## Getting Started

To compile all modules:
```bash
./gradlew assembleDebug
```

To run unit tests:
```bash
./gradlew testDebugUnitTest
```
