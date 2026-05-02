## Remaining Risks (2026-05-02)

| Issue | Severity | Status | Evidence | Next action |
| --- | --- | --- | --- | --- |
| Onboarding scroll/clipping on real device | High | Needs device retest | Code change in `WizardShell` + UI test exists | Run `scripts/easyui_final_device_retest.sh` and capture screenshots |
| Onboarding theme default (dark) on real device | High | Needs device retest | Defaults changed to `DARK_COMFORT` and theme now follows persisted `SkinConfig` | Run device retest; confirm first-run is not warm light |
| New onboarding steps visible in running flow | High | Needs device retest | `EasyUiNavGraph` step mapping updated (1–13) | Run device retest; capture Protection/Theme/Permissions screens |
| Layout editor placement | High | Needs device retest | Regression UI test confirms callback wiring; placement persistence still needs device validation | Place an app, go home, relaunch, confirm tile persists |
| Four-tap caregiver entry | High | Needs device retest | Active logic is in `HomeViewModel` with 4-tap/3s window | Validate on physical device; confirm no accidental triggers |
| Home swipe paging | Medium | Needs device retest | Active home uses `HorizontalPager`; swipe regression UI test exists | Validate gesture feel and tile-tap vs swipe conflict |
| Local ADB retest still needed | High | Blocked on device | This environment has no attached device | Run script and upload evidence zip |
| Release signing/AAB | Medium | Not covered | No work in this sprint | Separate release-readiness task |
| Play Store privacy/data safety | Medium | Not covered | No work in this sprint | Separate compliance task |
| Broader OEM/device testing | Medium | Not covered | No work in this sprint | Test across at least 2–3 OEMs |

