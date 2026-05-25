Repo: munaimtahir/easyui (/home/munaim/srv/apps/easyui)
Branch: UNKNOWN (update after git status)
Sprint: Settings Stabilization Sprint (2026-05-25)

Bug List (User-Reported)
1. Apps added on additional pages are not visible on the home screen.
2. Layout lock shows a lock icon on every tile.
3. Clock 5-tap does not open caregiver flow when PIN not set.
4. Theme changes revert after a moment.
5. Onboarding and caregiver settings are inconsistent.
6. Layout options in caregiver settings do not work.
7. Font options in onboarding do not work.
8. Contact/emergency/direct-call shortcuts do not render.

Execution Checklist
- [ ] Phase 1: Repository discovery and truth map
- [ ] Phase 2: Canonical settings contract
- [ ] Phase 3: Implementation fixes
- [ ] Phase 4: Tests added/updated
- [ ] Phase 5: Commands run
- [ ] Phase 6: Manual/ADB verification guide + evidence
- [ ] Phase 7: Final report

Commands To Run
- ./gradlew clean assembleDebug
- ./gradlew testDebugUnitTest
- ./gradlew lintDebug
- ./gradlew connectedDebugAndroidTest (if device/emulator available)

Files Inspected
- (pending)

Files Changed
- copilot_session.md

Tests Added/Updated
- (pending)

Verification Results
- (pending)

Remaining Issues
- (pending)

Final Verdict
- NO-GO (pending evidence)
