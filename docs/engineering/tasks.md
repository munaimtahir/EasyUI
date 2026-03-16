# Implementation Tasks

## Phase 0: Foundation

- [ ] Initialize Android project with Kotlin and Compose
- [ ] Add launcher intent filters
- [ ] Set up navigation
- [ ] Set up local persistence with Room and DataStore
- [ ] Add theme and typography foundation
- [ ] Add lint and test baseline

## Phase 1: MVP

### Onboarding

- [ ] First-run intro
- [ ] Default launcher guidance flow
- [ ] Starter layout preset
- [ ] Help screen for caregivers

### Home screen

- [ ] Large-tile home screen
- [ ] Clock and date block
- [ ] Core app tiles
- [ ] Emergency action tile
- [ ] Flashlight action tile
- [ ] Safe fallbacks for unsupported actions

### App list

- [ ] Enumerate installed apps
- [ ] Alphabetical app list
- [ ] Search
- [ ] Launch app action
- [ ] Refresh on install and uninstall

### Data and settings

- [ ] Persist home tiles
- [ ] Persist theme and settings
- [ ] Persist emergency contact config

### QA

- [ ] Unit tests for layout rules
- [ ] UI smoke tests for onboarding and home
- [ ] Manual default-launcher verification

## Phase 2: Caregiver Safety Pack

### Protection

- [ ] Caregiver PIN creation
- [ ] PIN gate for edit mode
- [ ] Layout lock
- [ ] Disable long-press editing when locked

### App visibility

- [ ] Hidden apps settings screen
- [ ] Hidden app filtering
- [ ] Restore hidden apps action

### Photo contacts

- [ ] Add and edit photo contact shortcut
- [ ] Call action
- [ ] Safe delete and edit behavior

### Premium

- [ ] Billing wrapper
- [ ] One-time premium product
- [ ] Feature gating
- [ ] Purchase restore

### QA

- [ ] Tests for premium gating
- [ ] Tests for hidden app rules
- [ ] Manual billing tests in internal track

## Phase 3: Stability and Restore

- [ ] Backup export
- [ ] Backup import
- [ ] Layout reset to defaults
- [ ] Import validation
- [ ] Schema and version safety

## Phase 4: Release Readiness

- [ ] Polish copy and onboarding
- [ ] Prepare store assets and screenshots
- [ ] Run closed testing with caregivers
- [ ] Review feedback and fix regressions
- [ ] Complete release checklist
