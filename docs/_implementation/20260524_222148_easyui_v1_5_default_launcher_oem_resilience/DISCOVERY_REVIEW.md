# Discovery Review - EasyUI V1.5

## What is already working
- **Core Navigation:** Guided Setup (onboarding) is functional and follows a 12-step flow.
- **V1.1-V1.4 Features:** Senior Home, Phone Health Card, Guardian Alert Banner, Assisted Recovery, and Alert Caregiver (Share Status) are built and visible on the device.
- **Basic Intents:** Tapping tiles like Phone, Camera, and Messages initiates the intended flows (either internal screens or safe handoffs).
- **Stability:** The app is stable on the vivo V2109 device with no fatal crashes or ANRs detected during discovery.
- **Persistence:** Local persistence via Room and DataStore appears to be working as intended.

## What is marked partial
- **Default Launcher Behavior:** While EasyUI can be launched and set as home, its detection as "isDefault" and the guidance provided when it is NOT the default is inconsistent or incomplete.
- **OEM Resilience:** The app's ability to stay as the default launcher and handle OEM-specific home settings (like those on vivo) is not fully validated.

## What is launch-blocking
- **Default Launcher Status:** The app must reliably detect its own default launcher status and provide clear, honest guidance to the caregiver to fix it if it's lost.
- **Intent Hardening:** Critical tiles (Phone, Emergency, Messages) must have robust fallbacks if the underlying system apps are missing or unresponsive.

## What must be fixed in this sprint
- **Default Launcher Detection:** Improve the logic for checking if EasyUI is the current default home app.
- **Guided Recovery for Home Settings:** Enhance the Assisted Recovery screen to handle "Set Default Launcher" more effectively across different Android versions.
- **Intent Hardening:** Add safety checks and better error messaging for all external app handoffs.
- **OEM-Specific Fallbacks:** Improve wording and guidance for specific OEM behaviors (e.g., vivo's unique settings paths).

## What should not be touched
- **Core Product Scope:** Do not add kiosk/lockdown features or cloud dependencies.
- **Stable UX:** Do not perform broad redesigns of the functional senior home or caregiver dashboard unless it directly relates to resilience/guidance.

## Conclusion
Default Launcher and OEM resilience are the primary blockers for a limited alpha release. This sprint will focus on stabilizing these critical system-integration points.
