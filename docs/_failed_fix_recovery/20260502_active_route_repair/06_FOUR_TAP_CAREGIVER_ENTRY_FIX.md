## Four-Tap Hidden Caregiver Entry (2026-05-02)

### User-reported issue
“After onboarding completion, tapping the time 4 times does not re-enter caregiver configuration.”

### Active implementation (home header → view model)
- Home UI:
  - `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
  - The time/date header forwards taps to `onClockTapped`.
- Navigation wiring:
  - `app/src/main/java/com/easyui/launcher/navigation/EasyUiNavGraph.kt`
  - `HomeScreen(..., onClockTapped = { homeViewModel.onClockTappedCaregiverAccess(openCaregiverAccess) })`
- Tap counting + timing:
  - `app/src/main/java/com/easyui/launcher/app/HomeViewModel.kt` → `onClockTappedCaregiverAccess`
  - Trigger: 4 taps (`ClockTapTriggerCount = 4`)
  - Reset window: 3 seconds (`ClockTapWindowMs = 3_000L`)
  - Debounce between access launches: 1.5 seconds

### Status
- Code matches the requested behavior (4 taps, reset after 3 seconds, time card only).
- This sprint did not change the logic; it confirmed the active route and documented it.

### NOT TESTED
- `NOT RUN — requires local ADB` to validate interaction on a physical device.

