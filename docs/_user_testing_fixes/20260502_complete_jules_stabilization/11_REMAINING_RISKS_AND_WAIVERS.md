# Remaining Risks and Waivers

| Issue | Severity | Fixed? | Evidence | Waiver allowed? | Next action |
| ----- | -------- | ------ | -------- | --------------- | ----------- |
| onboarding scroll | High | Yes | Scaffold with padding applied to `OnboardingScreens.kt`. | No | None |
| layout editor placement | High | Yes | Fixed pointer unassignment state in `CaregiverScreens.kt` and `GuidedSetupScreens.kt`. | No | None |
| hidden caregiver 4-tap entry | High | Yes | Trigger added via `onTap` listener reducing trigger count to 4 in `HomeViewModel.kt`. | No | None |
| swipe paging | Medium | Yes | HorizontalPager wraps `HomeScreen` properly in `HomeScreen.kt`. | No | None |
| onboarding color scheme | Medium | Yes | `OnboardingTokens.kt` refactored to pull directly from `MaterialTheme` and `LocalSkinColors.current`. | No | None |
| new protection step | High | Yes | Integrated step 3 `ProtectionOptionsScreen` correctly mapping to state. | No | None |
| new theme step | High | Yes | Integrated step 4 `ThemePickerScreen` mapping strictly to `SkinConfig.visualTheme`. | No | None |
| new permissions step | High | Yes | Integrated step 5 `PermissionsExplanationScreen` without forcing OS interactions prematurely. | No | None |
| connected device testing status | Medium | No | Passed over to local ADB retest scripts `device_dry_run.sh`. | Yes | Run script on local device. |
| session timeout real timed verification | Low | No | Relying on test/codebase review due to timing complexity in automation. | Yes | Perform manually if desired. |
| emergency call safe verification | Low | No | Using `ACTION_DIAL` safe path. Code verified. | Yes | Perform manually if desired. |
| release AAB/signing | Blocker | No | Keys are missing so release bundle is unsigned. | Yes | Block Play Store upload until provided. |
| Play Store privacy/data safety | Medium | No | Requires manual console entries. | Yes | Fill out store listing forms. |
| default launcher behavior on TECNO/other OEMs | High | No | Launcher guidance covers steps safely. Some OEM variations can't be reliably enforced natively. | Yes | Monitor testing feedback. |
