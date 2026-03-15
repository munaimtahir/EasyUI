# TASKS

## Phase 0 — Foundation
- [ ] Initialize Android project with Kotlin + Compose
- [ ] Add launcher intent filters
- [ ] Set up navigation
- [ ] Set up local persistence (Room + DataStore)
- [ ] Add app theme and typography foundation
- [ ] Add lint/test baseline

## Phase 1 — MVP
### Onboarding
- [ ] First-run intro
- [ ] Default launcher guidance flow
- [ ] Starter layout selection or default preset
- [ ] Help screen for caregivers

### Home screen
- [ ] Large-tile home screen
- [ ] Clock/date block
- [ ] Core app tiles
- [ ] Emergency action tile
- [ ] Flashlight action tile
- [ ] Safe fallback states for unsupported actions

### App list
- [ ] Enumerate installed apps
- [ ] Alphabetical app list
- [ ] Search
- [ ] Launch app action
- [ ] Refresh handling on install/uninstall

### Data and settings
- [ ] Persist home tiles
- [ ] Persist simple theme/settings
- [ ] Persist emergency contact config

### QA
- [ ] Unit tests for layout rules
- [ ] UI smoke tests for onboarding and home
- [ ] Manual default-launcher verification

## Phase 2 — Caregiver Safety Pack
### Protection
- [ ] Caregiver PIN creation
- [ ] PIN gate for edit mode
- [ ] Layout lock
- [ ] Disable long-press editing in locked mode

### App visibility
- [ ] Hidden apps settings screen
- [ ] Hidden app filtering
- [ ] Restore hidden apps action

### Photo contacts
- [ ] Add/edit photo contact shortcut
- [ ] Call action
- [ ] Safe delete/edit behavior

### Premium
- [ ] Billing wrapper
- [ ] One-time premium product
- [ ] Feature gating
- [ ] Purchase restore

### QA
- [ ] Tests for premium gating
- [ ] Tests for hidden app rules
- [ ] Manual billing tests in internal track

## Phase 3 — Stability and Restore
- [ ] Backup export
- [ ] Backup import
- [ ] Layout reset to defaults
- [ ] Import validation
- [ ] Schema/version safety

## Phase 4 — Release Readiness
- [ ] Polish copy and onboarding
- [ ] Store assets and screenshots
- [ ] Closed testing with caregiver users
- [ ] Review analysis and fixes
- [ ] Release checklist completion

## Explicitly deferred
- [ ] full kiosk mode
- [ ] enterprise device-owner track
- [ ] cloud sync
- [ ] remote caregiver portal
- [ ] subscription plans
