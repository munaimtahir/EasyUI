# Hidden Caregiver Entry Fix

The 4-tap access to the caregiver dashboard from the clock was implemented.

- The `HomeScreen` `HomeHeaderCard` component was updated so the pointer input logic handles standard taps (`onTap = { onClockTapped() }`) in addition to long presses (`onPress`).
- The logic within `HomeViewModel.kt` `onClockTappedCaregiverAccess` handles debounce tracking for taps, waiting to see 4 sequential taps within 3 seconds. The `ClockTapTriggerCount` was reduced from 5 to 4 to match the expected design constraint.
- When `onClockTappedCaregiverAccess` fulfills the state count, it requests the navigation controller to route to the caregiver dashboard.
