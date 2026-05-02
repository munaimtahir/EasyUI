# Layout Editor Placement Fix

App Placement targeting was updated. Previously, a user would tap an empty slot and then select an installed app, but the logic didn't actually fire correctly or reset the state, leaving the app seemingly unresponsive.

The fix was applied in two files:
- `CaregiverScreens.kt` (AllowedAppsScreen)
- `GuidedSetupScreens.kt` (AllowedAppsSetupScreen)

In both flows, clicking the `Place` or `Place Here` button triggers the `onAssignApp` ViewModel lambda with the target app and `selectedPosition`, and instantly unsets the `selectedPosition` flag back to `null`, ensuring the UX feels responsive and the state resolves cleanly, ready for the next slot interaction.
