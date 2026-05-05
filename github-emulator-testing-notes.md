# GitHub Android Emulator Testing Notes — EasyUI Senior Launcher

## Purpose

This workflow turns GitHub Actions into the repeatable Android verification environment for EasyUI Senior Launcher. It is intended to reduce dependence on manual phone testing and local laptop emulator runs.

## Preserved from the existing workflow

- Ubuntu GitHub-hosted runner
- JDK 17
- Gradle cache/setup
- KVM enablement
- `reactivecircus/android-emulator-runner@v2`
- API 29 x86_64 emulator
- Nexus 6 profile
- no-window emulator options
- separate bash runner script to avoid the `/bin/sh` `pipefail` issue

## Added verification layers

1. Debug APK build
2. Local JVM/unit tests
3. Android lint
4. Instrumented Android tests on emulator
5. ADB APK install and launch smoke test
6. Launcher/Home resolution check
7. Focused activity capture
8. Logcat crash/ANR scan
9. Screenshot capture
10. Full evidence artifact upload with `if: always()`

## Project-specific default values

```text
APP_MODULE=app
PACKAGE_NAME=com.easyui.launcher.debug
MAIN_ACTIVITY=com.easyui.launcher.MainActivity
DEBUG_APK_PATH=app/build/outputs/apk/debug/app-debug.apk
```

The workflow should be updated if the application ID, module name, APK output path, or launcher activity changes.

## Important EasyUI regression areas to cover with instrumented tests

- App can run as launcher/home activity without crashing.
- First-run/default-launcher guidance is visible.
- Home screen loads with senior-friendly large tiles.
- Home paging/swipe behavior works if multi-page support exists.
- Previous/next page buttons are not shown if horizontal swipe paging is now the intended UI.
- Hidden caregiver entry by tapping the clock works both with PIN configured and with no PIN configured.
- Settings/caregiver screen opens correctly.
- Onboarding screens scroll correctly on emulator-sized displays.
- App picker/Allowed Apps list is not collapsed and remains scrollable/visible.
- Emergency settings screen scrolls and saves values correctly.
- Home layout/app placement screen scrolls and shows full picker content.

## Screenshot coverage target

Minimum:

```text
01_launch.png
02_home.png
03_after_back_home_stability.png
99_final_state.png
```

Preferred through Compose/Espresso/UIAutomator tests:

```text
01_launch.png
02_onboarding_start.png
03_permissions_or_default_launcher.png
04_home.png
05_home_second_page.png
06_caregiver_gate_or_settings_entry.png
07_settings_dashboard.png
08_allowed_apps.png
09_app_picker_expanded.png
10_emergency_settings.png
11_home_layout_editor.png
12_error_or_empty_state.png
```

## GO/NO-GO rule

GO only when:

- `assembleDebug` passes.
- Unit tests run and pass, or missing tests are explicitly documented.
- Lint runs and pass/fail is visible in artifacts.
- Emulator boots successfully.
- Instrumented tests run.
- ADB installs and launches the app.
- Launcher/Home behavior is verified.
- No launch crash or ANR is detected in logcat.
- Screenshots and logcat are uploaded as artifacts.

NO-GO when:

- Build fails.
- Emulator cannot boot.
- App cannot install.
- App cannot launch.
- Current launcher/home activity crashes.
- Known navigation regressions remain untested.
