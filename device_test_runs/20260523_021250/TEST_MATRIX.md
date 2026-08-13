# Test Matrix

| ID | Suite | Scenario | Priority | Status | Evidence Path | Note |
| --- | --- | --- | --- | --- | --- | --- |
| A4 | 00_static | Build health | P0 | PASS | screenshots/a4.png | assembleDebug and testDebugUnitTest completed successfully. |
| A5 | 00_static | Docs-to-code alignment | P2 | PASS | screenshots/a5.png | Feature flags mark the senior-facing home entry for app list as not wired yet, matching docs/engineering/tasks.md. |
| A2 | 00_static | Launcher manifest audit | P1 | PARTIALLY_VERIFIED | screenshots/a2.png | Main launcher activity declares HOME and DEFAULT only; standard LAUNCHER category is intentionally absent for a launcher-only surface. |
| A3 | 00_static | Permission declaration audit | P2 | PARTIALLY_VERIFIED | screenshots/a3.png | Manifest declares CALL_PHONE, SEND_SMS, ACCESS_NETWORK_STATE, and optional camera flash feature. ACCESS_NETWORK_STATE should be reviewed because product is offline-first. |
| A1 | 00_static | Repository and identity audit | P1 | PASS | screenshots/a1.png | Debug package=com.easyui.launcher.debug; release package=com.easyui.launcher. |
| B1 | 10_install_and_first_run | Fresh install and first-run flow | P0 | PASS | screenshots/b1.png | Fresh install succeeded, onboarding completed, and relaunch returned to the home screen. |
| B1A | 10_install_and_first_run | Default launcher guidance and HOME role verification | P1 | PASS | screenshots/b1a.png | Device now resolves Home to EasyUI. |
| B2 | 20_launcher_core | Home button and launcher root behavior | P1 | PASS | screenshots/b2.png | Home and recents roundtrip returned to EasyUI while it was the resolved HOME app. |
| B3 | 30_senior_home | Home visibility and tile clarity | P1 | PASS | screenshots/b3.png | Home screen exposes the expected large-label essentials on the current build. |
| B4 | 30_senior_home | Simple app access | P1 | PARTIALLY_VERIFIED | screenshots/b4.png | The app list screen exists in code, but the docs and current feature flags still mark the senior-facing home entry as not wired in this build. |
| B5 | 40_essential_actions | Essential actions | P1 | PARTIALLY_VERIFIED | screenshots/b5.png | Emergency flow opened, but the dialer surface could not be confidently identified from the UI dump. |
| B6 | 50_caregiver | Caregiver entry and protection | P1 | PARTIALLY_VERIFIED | screenshots/b6.png | Home was reachable, but the hidden caregiver gesture could not be confirmed in this adb-driven run on the current OEM build. |
| B10 | 80_permissions_oem_resilience | Permission and OEM resilience | P2 | PASS | screenshots/b10.png | App relaunched after force-stop without an immediate crash. |
| B9 | 90_offline_and_guardrails | Offline-first behavior | P1 | PASS | screenshots/b9.png | App launched and remained usable with airplane mode enabled. |
