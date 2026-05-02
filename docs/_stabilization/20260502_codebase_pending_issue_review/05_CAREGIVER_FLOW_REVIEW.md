# Caregiver Flow Review

Code verification:
- `CaregiverViewModel` handles caregiver session state logic.
- Route guards: `RequireCaregiverSession` Composable is used on Caregiver routes, including `CaregiverDashboard`, `HiddenApps`, `BackupRestore`. If `caregiverSessionActive` is false, it forces a popBackStack navigation to `Routes.Home`.
- Session timeout: Logic inside `RequireCaregiverSession` checks `caregiverViewModel.checkSessionTimeout()` every second. Warning triggers at `WarningActive` state, logout at `TimedOut` state.
- No visible caregiver access on Home without the specific hidden gesture or fallback.

This fulfills the core requirements.
