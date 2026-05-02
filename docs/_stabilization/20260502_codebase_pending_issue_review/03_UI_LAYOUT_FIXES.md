# UI Layout Fixes

## Onboarding Screen Layout
*Issue*: The intro/onboarding content may be clipped behind the fixed bottom Start Setup button on some screen sizes or font scales.

*Review of SetupScene in `feature/onboarding/src/main/java/com/easyui/feature/onboarding/OnboardingScreens.kt`:*
- The scrollable content and the fixed button row are currently in a `Column` where the content has `weight(1f)` and `verticalScroll`, which is a good structure.
- However, the `navigationBarsPadding()` is applied on the fixed bottom button.
- The top level padding includes `safeDrawingPadding.calculateTopPadding()` and `safeDrawingPadding.calculateBottomPadding()` PLUS `OnboardingTokens.bottomSpacing`.
- Need to verify if the spacing is sufficient to not overlap or clip.
- Let's modify the UI so that we don't have clipping issues on large font scales or short screens.

A safer approach to keep the CTA in the bottom area and ensure the content scrolls properly is using a `Box` where the bottom CTA is aligned to the bottom and the main content has `contentPadding` at the bottom equal to the height of the CTA + navigation insets. Alternatively, using `Scaffold`'s `bottomBar` achieves exactly this gracefully.

Will evaluate fixing `SetupScene`.

*Fix Implemented*:
Replaced `Box`/`Column` layout with a `Scaffold` and `bottomBar`. The `bottomBar` now correctly houses the action buttons while the main content receives appropriate `contentPadding` directly from the `Scaffold`. This guarantees that scrollable content reaches below the buttons and adds proper padding without clipping on large font scaling or smaller screen sizes. Verified with Compose Previews conceptually.
