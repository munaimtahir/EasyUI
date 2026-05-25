# EasyUI Senior Launcher - Project Context & Instructions

This document provides essential context and instructions for AI agents working on the EasyUI project.

## Project Overview

EasyUI Senior Launcher is an **offline-first Android launcher** designed for seniors. It prioritizes clarity, stability, and simplicity. It includes a "Caregiver Mode" for configuration and protection against accidental changes.

### Core Philosophy
- **Senior-Centric:** Large targets, high contrast, minimal clutter.
- **Offline-First:** No backend, no accounts, local storage only (Room/DataStore).
- **Caregiver-Managed:** Stable layouts, PIN protection, hidden app management.
- **Non-Kiosk:** It is a consumer launcher, not a lockdown tool. It does not make OS-level promises it cannot enforce.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Navigation:** AndroidX Navigation
- **Persistence:** Room (DB), DataStore (Settings)
- **Dependency Management:** Gradle (KTS)
- **Architecture:** Multi-module (app, feature, core)

## Project Structure

### Android Modules
- `:app`: Application shell, DI wiring, navigation graph, launcher manifest.
- `:feature:home`: Senior home surface, tile grid, health info.
- `:feature:apps`: Installed app list and search.
- `:feature:caregiver`: Caregiver settings, PIN, layout management, backups.
- `:feature:onboarding`: First-run experience, default launcher guidance.
- `:core:domain`: Pure Kotlin business rules and domain models.
- `:core:data`: Room, DataStore, and Repository implementations.
- `:core:platform`: Android API wrappers (PackageManager, Intents, Torch).
- `:core:ui`: Shared design system, colors, typography, common components.
- `:core:testing`: Shared test fixtures and helpers.

### End-to-End Testing
- `e2e/`: ADB-driven Playwright test harness for driving tests on physical or emulated devices.

### Documentation
- `docs/`: Canonical project documentation (Product, Engineering, Delivery).
- `AGENTS.md`: Specific mission guidance and source-of-truth pointers.
- `README.md`: High-level overview and setup instructions.

## Building and Running

### Common Gradle Commands
```bash
./gradlew assembleDebug           # Build debug APK
./gradlew testDebugUnitTest       # Run all JVM unit tests
./gradlew assembleDebugAndroidTest # Build instrumentation tests
./gradlew lint                    # Run Android lint
```

### E2E Testing Commands
```bash
./e2e/scripts/run-static.sh       # Run static Playwright checks
./e2e/scripts/run-device-smoke.sh # Run smoke tests on attached device
./e2e/scripts/run-device-full.sh  # Run full test suite on attached device
```

## Development Conventions

### Coding Rules
- **Compose First:** Use small, stateless composables and hoisted state.
- **Explicit State:** Prefer clear state models for ViewModels and UI.
- **Defensive Fallbacks:** Always handle missing apps, unavailable hardware (e.g., torch), or denied permissions gracefully.
- **Offline-Only:** Do not introduce network dependencies or cloud sync.
- **Truthful UX:** Keep strings plain and honest about launcher limitations.

### Testing Strategy
- **Unit Tests:** Focus on `core:domain` rules and `ViewModel` logic.
- **UI Tests:** Use Compose Testing framework for screen-level verification.
- **Instrumentation:** Use for device-specific behavior (Launcher activation, PackageManager).
- **E2E:** Use the Playwright/ADB harness for critical user journeys.

### Documentation Management
- **Canonical Docs:** Always refer to `docs/` and `AGENTS.md`.
- **Avoid Legacy:** Ignore `archive/legacy-docs/` unless specifically directed.
- **Update on Change:** Keep documentation in sync with architectural or product changes.

## Working with EasyUI
- **Start with `docs/engineering/tasks.md`:** This is the current implementation roadmap.
- **Check `docs/product/guardrails.md`:** Ensure new features align with the product mission.
- **Safety First:** Protect user data and maintain local-only privacy.
