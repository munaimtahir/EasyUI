# Guided Setup Implementation Plan

## Current Truth Map

EasyUI has a mature caregiver dashboard and domain rules for senior home layout, app visibility, and caregiver protection. Onboarding is currently a simple 3-screen flow (`Intro`, `LauncherGuidance`, `CaregiverHelp`) that sets `onboardingComplete = true`.

### Discovered architecture
- **Core Persistence**: `LauncherSettingsRepository` (DataStore) in `core/data` handles all settings.
- **Rules Engine**: `core/domain/rules` (HomeReadability, HomeLayout, CaregiverProtection, etc.) contains the logic.
- **Navigation**: `MainActivity` + `EasyUiNavGraph`.
- **Feature Modules**: `feature/onboarding` (bare), `feature/caregiver` (robust set of screens/components).

## Classification of code for Guided Setup

1. **Reusable as-is**: `HomeReadabilityRules`, `CaregiverProtectionRules`, `LauncherSettingsRepository`, `CaregiverDashboardTokens`.
2. **Needs extension**: `LauncherSettings` (add Guided Setup progress), `Routes` (new setup steps).
3. **Refactor for reuse**: `CaregiverScreens.kt` (move common layouts to shared components if needed), `DefaultLauncherGuidanceScreen`.
4. **Missing / Must be built**: `WizardShell`, `GuidedSetupViewModel`, 10 screens based on the contract pack.

## Target Architecture

Implementing a `GuidedSetupViewModel` in `feature/onboarding` that orchestrates the 10 screens defined in the contract. Each screen will map to existing settings in `LauncherSettings` or new Guided Setup-specific fields.

### Screens to implement
1. **Welcome**: `IntroScreen` refactored into `WelcomeScreen`.
2. **Set EasyUI as Home Launcher**: `DefaultLauncherGuidanceScreen` refactored.
3. **Readability Preset**: Screen showing `HomeReadabilityPreset` choices.
4. **Home Layout Setup**: Preview of 2x3 home and page count/skin config.
5. **Allowed Apps**: Reuse `AllowedAppsScreen` logic.
6. **Contacts & Emergency**: Configuration for primary contacts and emergency mode.
7. **Security & Lock**: Caregiver PIN and layout lock.
8. **Device & Support**: Themes and simple indicators (as already supported).
9. **Review & Confirm**: Summary cards with edit-to-return behavior.
10. **Completion**: Final message, mark setup finished.

## Progress Tracking

### Slice 1: Foundation
- [ ] Add `guidedSetupStep` and `guidedSetupCompleted` to `LauncherSettings`.
- [ ] Create `WizardShell` for consistent top/bottom UI.
- [ ] Implement `Welcome`, `Launcher`, and `Readability` screens.
- [ ] Update `EasyUiNavGraph` to navigate through Guided Setup.

### Slice 2: Layout & Apps
- [ ] Implement `Home Layout Setup` screen.
- [ ] Implement `Allowed Apps` screen (choosing visible apps).
- [ ] Add scaffolding for `Review & Confirm`.

### Slice 3: Security & Completion
- [ ] Implement `Contacts & Emergency` screen.
- [ ] Implement `Security & Lock` screen (PIN and layout lock).
- [ ] Implement `Device & Support` screen.
- [ ] Finalize `Review & Confirm` with validation.
- [ ] Implement `Completion` screen.
- [ ] Enable manual re-entry from caregiver dashboard.

### Slice 4: Polishing & Tests
- [ ] Polish UI transitions and spacing.
- [ ] Add unit tests for `GuidedSetupViewModel`.
- [ ] Add UI tests for the setup flow.
- [ ] Final audit of implementation vs. contract.

## Test Plan
- **Persistence**: Verify session state survives process death.
- **Navigation**: Check Back/Next/Skip and resume behavior.
- **Validation**: Verify hard blocks (Launcher, Readability, PIN decision, etc.).
- **Integration**: Ensure setup changes reflect accurately in Senior Home and Caregiver Dashboard.
