# Settings Access Audit

## Issue
After onboarding completes, user cannot enter settings or caregiver setup.

## Investigation
In `HomeScreen.kt` or `EasyUiNavGraph.kt`, entering settings is triggered by:
```kotlin
onStatusBarLongPress = { homeViewModel.onTopBarLongPressCaregiverAccess(openCaregiverAccess) },
onClockTapped = { homeViewModel.onClockTappedCaregiverAccess(openCaregiverAccess) }
```
`openCaregiverAccess` navigates to `caregiverViewModel.requestCaregiverAccess()`.

Let's look at `CaregiverViewModel.requestCaregiverAccess()`:
```kotlin
fun requestCaregiverAccess(): String {
    // Returns route
}
```
I will inspect `CaregiverViewModel.kt` and `HomeViewModel.kt` to see why the navigation fails or is not triggered after onboarding. Maybe `caregiverProtectionEnabled` logic is blocking it, or the PIN gate is misconfigured.

## Findings
Pending inspection of `CaregiverViewModel.kt` and `HomeViewModel.kt`.