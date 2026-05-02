## Old Onboarding Source Found (2026-05-02)

### What the device shows
Per the task prompt (physical device, fresh install, first launch), the app still shows:
- Title: `Welcome to EasyUI`
- CTA: `Start Setup`
- Light/cream background
- Bottom explanatory text clipped/hidden under the bottom button area (scroll issue)

### Exact code responsible (active)
The above screen is implemented and currently routed as:
- `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt`
  - `@Composable fun WelcomeScreen(onNext: () -> Unit)`
  - Uses `WizardShell(...)` with `title = "Welcome to EasyUI"` and `nextLabel = "Start Setup"`

And it is invoked from the running navigation graph:
- `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
  - `composable(Routes.GuidedSetup.route) { when (guidedSetupState.guidedSetupStep) { 1 -> WelcomeScreen(...) ... } }`

### Why the “fixed UI” may not have appeared
There is a second onboarding implementation file with different composables and tokens:
- `feature/onboarding/src/main/java/com/easyui/feature/onboarding/OnboardingScreens.kt`

If prior work edited `OnboardingScreens.kt` (or other unused routes) instead of `GuidedSetupScreens.kt` + `WizardShell.kt` + `EasyUiNavGraph.kt`, the APK would still show the old `WelcomeScreen` on first launch.

