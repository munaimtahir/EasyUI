## Executive Summary (2026-05-02)

### Why this sprint existed
Physical device testing showed the shipped APK still rendered the old onboarding (`Welcome to EasyUI`) with light/cream styling and clipped bottom copy, despite a prior PR claiming fixes.

### What was proven first
- The actually launched first-run onboarding is `Routes.GuidedSetup` step 1 → `WelcomeScreen` in `feature/onboarding/.../GuidedSetupScreens.kt`.
- A newer “SetupScene”-based onboarding exists in `OnboardingScreens.kt` but is not the active first-run route.

### What was fixed in the active route
- Onboarding scroll/clipping: `WizardShell` now scrolls via `LazyColumn`, ensuring bottom copy is reachable above the CTA.
- Theme mismatch: app theme now follows persisted `SkinConfig` (not system theme), and defaults to a dark visual theme (`DARK_COMFORT`) for first run.
- New onboarding steps are wired into the *active* guided setup flow:
  - Protection Options
  - Theme Picker
  - Permissions Explanation

### What was added to prevent regressions
- Instrumentation tests for:
  - guided setup new steps + scroll reachability
  - swipe paging behavior
  - allowed apps “place here” wiring
- A local ADB device retest script that launches via explicit activity start.

### Current state
- Build/test/lint results are pending from this checkout (see `08_TEST_RESULTS.md`).
- Device validation is pending (`NOT RUN — requires local ADB`).

