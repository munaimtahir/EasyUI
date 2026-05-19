# EasyUI Senior Launcher Context

## Project Overview
EasyUI Senior Launcher is an offline-first Android launcher designed for seniors and caregivers. Its main goal is to provide a simpler, larger, high-contrast, and stable home screen, reducing accidental changes and the resulting support burden on caregivers. It's not a kiosk app, OS-level lock-down, or enterprise management solution, but rather a consumer launcher.

## Key Users
* **Seniors:** Need larger touch targets, clear labels, high contrast, and reduced visual clutter.
* **Caregivers / Adult Children:** Need an easy way to configure the phone once and avoid constant support calls due to accidental layout changes or app deletions.

## Architecture & Tech Stack
* **Stack:** Kotlin, Jetpack Compose, AndroidX Navigation, Room (local DB), DataStore (settings). Play Billing is planned.
* **Structure:**
  * `app/`: Startup, DI, navigation graph, launcher registration.
  * `feature/*`: Specific screen/flow logic (`home`, `apps`, `caregiver`, `onboarding`).
  * `core/*`: Shared modules (`ui`, `domain`, `data`, `platform`, `testing`).
* **Offline-First:** All configuration is local. There is no cloud sync, remote backend, or account dependency.

## Current Status (Beyond MVP)
The app currently has a solid foundation with many features already implemented:
* **Implemented:**
  * First-run intro & default-launcher setup guidance.
  * Fixed 2x3 senior home grid with essentials (Phone, Messages, Contacts, Photos, Camera, Emergency).
  * Caregiver safety controls (PIN, layout lock, disabled edit gestures).
  * Hidden caregiver entry from home.
  * Hiding specific apps inside EasyUI and assigning home apps.
  * Local backup export/import and layout reset.
  * Theming, readability settings, very simple mode, and battery visibility controls.
  * Health Info storage and SOS features.
* **Remaining Gaps (for V1 completion):**
  * Senior-facing home entry point to the full app list screen is still being wired.
  * Play Billing wrapper and premium unlock logic.

## Guardrails & Product Truth
* Do not add features that increase complexity over stability.
* Do not make OS-level lockdown or enterprise management promises.
* Keep it a consumer launcher. If an action or app isn't available on the device, handle the failure gracefully with fallback UI instead of crashing.
* Avoid OEM-specific hacks as a core foundation.

## Important Documents
* Scope: `docs/product/mvp-scope.md`, `docs/product/v1-scope.md`
* Guardrails: `docs/product/guardrails.md`
* Engineering: `docs/engineering/architecture.md`, `docs/engineering/data-model.md`
* Status: `docs/delivery/current-status.md`
* Tasks: `docs/engineering/tasks.md`