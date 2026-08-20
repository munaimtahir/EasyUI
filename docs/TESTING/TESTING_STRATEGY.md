# Testing Strategy — EasyUI

This document outlines the testing strategy for **EasyUI**, ensuring that the senior launcher, caregiver companion, and supporting backend remain stable, secure, and resilient. EasyUI is built on top of the frozen **Core Launcher** foundation, which is a separate repository. EasyUI is an intentional product derivative of Core. Therefore, Core's original product-variant prohibitions do not govern EasyUI.

## Testing Philosophy
- **Resilience First**: The senior launcher must be tested for offline robustness. Connectivity drops, server outages, and slow latency must never degrade launcher HOME stability.
- **Integration Coverage**: Telemetry and control loops (Pairing, Status reporting, SOS Alerts, and Reminder synchronization) must be validated end-to-end.
- **Accessibility Verification**: Accessibility is a core feature of the senior launcher. Visual scaling, touch targets, and screen reader labels must be audited.

---

## Required Verification Layers

### 1. Build Verification
Ensure all modules compile and package correctly:
```bash
./gradlew clean assembleDebug
```
And verify release packaging:
```bash
./gradlew assembleRelease
```

### 2. Unit Testing
Run local unit tests for all modules to verify storage, network clients, state transitions, hashing, and encryption logic:
```bash
./gradlew testDebugUnitTest
```
- **Senior Launcher**: Layout grid packing, theme settings, SHA-256 caregiver PIN hashing, local reminder scheduling, and pairing managers.
- **Companion**: Local session storage, suggestion staging, and UI state mappings.
- **Backend**: Token authentication filters, secure route authorizations, pairing code validation, and state stores (exercised in `BackendTest`).

### 3. Static Analysis & Linting
Validate codebase syntax and guidelines:
```bash
./gradlew lintDebug
```

### 4. Emulator & Instrumentation Testing
Verify UI interactions using simulated targets:
- **Onboarding workflow**: Verify steps completion.
- **Default Launcher setup**: Verify package set-home and HOME button routing.
- **Pairing screen**: Enter code and verify pairing completion.
- **Emergency screen**: Long-press SOS button and verify dialer launch.
- **Check-In screen**: Tap "I'm OK" and verify state transition.

### 5. Offline & Recovery Tests
Perform manual or automated failure injections:
- **Offline operation**: Install and launch senior launcher without internet. Verify normal HOME operation, app drawer searching, and local dialing.
- **Server unavailable**: Attempt Check-In / SOS trigger when backend is unreachable. Verify UI displays friendly errors without crashes.
- **Reconnection**: Restore connectivity and verify that `StatusReportWorker` successfully posts queued state.

---

## Accessibility Audit Priorities
Run dedicated accessibility checks:
- **Font Scaling**: Verify that layout text does not truncate or overflow at maximum system font size.
- **Touch Target Sizes**: Ensure interactive buttons, list entries, and tiles have a minimum size of 48dp x 48dp.
- **TalkBack Scan**: Check that all custom image tiles and buttons have clear, descriptive content labels.
- **Contrast Ratios**: Check readability of texts and icons against container backgrounds (especially on emergency screens).
