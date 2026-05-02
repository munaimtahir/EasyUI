## Active Route Truth Audit (2026-05-02)

### Snapshot
- Date (UTC): 2026-05-02
- Repo: `/home/munaim/srv/apps/easyui`
- Branch: `main`
- HEAD: `b3bbd610c1558eab181d12824442dcbf11baefde`

### Launch reality (per task prompt)
- Debug direct-launch activity: `com.easyui.launcher.MainActivity`
- Example command: `adb -s 08357252AE006901 shell am start -n com.easyui.launcher.debug/com.easyui.launcher.MainActivity`

### Entry + navigation chain (source of truth)
1) `app/src/main/java/com/easyui/launcher/MainActivity.kt`
   - `setContent { EasyUiApp(...) }`
   - `EasyUiApp` wraps `EasyUiNavGraph(...)` in `EasyUiTheme { ... }`

2) `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
   - Chooses `startDestination`:
     - `Routes.Home` when `appState.settings.onboardingComplete == true`
     - `Routes.GuidedSetup` otherwise
   - `Routes.GuidedSetup` renders onboarding by `guidedSetupState.guidedSetupStep`

### “Old onboarding” is confirmed active
The visible strings reported from device testing are rendered by the currently active Guided Setup step 1:
- `Welcome to EasyUI` + `Start Setup` are rendered by:
  - `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` → `WelcomeScreen(...)`
- This is the screen actually used by the `Routes.GuidedSetup` composable:
  - `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` → `when (guidedSetupState.guidedSetupStep) { 1 -> WelcomeScreen(...) }`

### Evidence table (minimum set)
| Visible text/screen | File | Function/composable | Active route? | Evidence |
| --- | --- | --- | --- | --- |
| `Welcome to EasyUI` title | `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` | `WelcomeScreen` | Yes | Text literal + referenced by `EasyUiNavGraph` step `1` |
| `Start Setup` CTA | `feature/onboarding/src/main/java/com/easyui/feature/onboarding/GuidedSetupScreens.kt` | `WelcomeScreen` | Yes | `WizardShell(nextLabel = "Start Setup")` |
| Guided setup routing | `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt` | `NavHost` route `Routes.GuidedSetup` | Yes | `startDestination = if (...) else Routes.GuidedSetup` |

### Suspected “unused newer onboarding”
There is a separate onboarding UI file that contains a different “SetupScene”-based design:
- `feature/onboarding/src/main/java/com/easyui/feature/onboarding/OnboardingScreens.kt`

This file is NOT referenced from the active `Routes.GuidedSetup` step switch shown above, so edits there can fail to affect the real launched onboarding.

