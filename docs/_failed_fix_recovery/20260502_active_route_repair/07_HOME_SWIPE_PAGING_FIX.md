## Home Swipe Paging (2026-05-02)

### User-reported issue
“Home pages are button-only, not swipeable.”

### Active home implementation
- `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt`
  - Uses `HorizontalPager`
  - Swipe enabled when `pageCount > 1` (`userScrollEnabled = pageCount > 1`)
  - Next/Previous buttons remain as a fallback
  - Dots/indicators render and use `pagerState.currentPage`

### What changed in this sprint
- Added a stable test hook to the pager for instrumentation tests:
  - `feature/home/src/main/java/com/easyui/feature/home/HomeScreen.kt` adds `testTag("home_pager")`
- Added an instrumentation test that swipes left and verifies the navigation buttons update:
  - `app/src/androidTest/java/com/easyui/launcher/HomePagingSwipeTest.kt`

### NOT TESTED
- `NOT RUN — requires local ADB` to validate gesture feel and tile tap vs swipe conflict on real hardware.

