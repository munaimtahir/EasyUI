# Implementation Tasks

## Phase 0: Foundation

- [x] Initialize Android project with Kotlin and Compose
- [x] Add launcher intent filters
- [x] Set up navigation
- [x] Set up local persistence with Room and DataStore
- [x] Add theme and typography foundation
- [x] Add lint and test baseline

## Phase 1: MVP

### Onboarding

- [x] First-run intro
- [x] Default launcher guidance flow
- [x] Starter layout preset
- [x] Help screen for caregivers

### Home screen

- [x] Large-tile home screen
- [x] Clock and date block
- [x] Core app tiles
- [x] Emergency action tile
- [x] Flashlight action tile
- [x] Safe fallbacks for unsupported actions

### App list

- [x] Enumerate installed apps
- [x] Alphabetical app list
- [x] Search
- [x] Launch app action
- [x] Refresh on install and uninstall

### Data and settings

- [x] Persist home tiles
- [x] Persist theme and settings
- [x] Persist emergency contact config

### QA

- [x] Unit tests for layout rules
- [x] UI smoke tests for onboarding and home
- [x] Manual default-launcher verification

## Phase 2: Caregiver Safety Pack

### Protection

- [x] Caregiver PIN creation
- [x] PIN gate for edit mode
- [x] Layout lock
- [x] Disable long-press editing when locked

### App visibility

- [x] Hidden apps settings screen
- [x] Hidden app filtering
- [x] Restore hidden apps action

### Photo contacts

- [x] Add and edit photo contact shortcut
- [x] Call action
- [x] Safe delete and edit behavior

### Premium

- [ ] Billing wrapper
- [ ] One-time premium product
- [ ] Feature gating
- [ ] Purchase restore

### QA

- [ ] Tests for premium gating
- [x] Tests for hidden app rules
- [ ] Manual billing tests in internal track

## Phase 3: Stability and Restore

- [x] Backup export
- [x] Backup import
- [x] Layout reset to defaults
- [x] Import validation
- [x] Schema and version safety

## Phase 4: Release Readiness

- [x] Polish copy and onboarding
- [x] Prepare store assets and screenshots
- [x] Run closed testing with caregivers
- [x] Review feedback and fix regressions
- [x] Complete release checklist
