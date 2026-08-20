# Roadmap — EasyUI

This document outlines the development phases of the **EasyUI Senior & Caregiver Product Suite**, built upon the frozen **Core Launcher** baseline. EasyUI is an intentional product derivative of Core. Core's original product-variant prohibitions do not govern EasyUI; caregiver and remote monitoring capabilities are valid within-scope features of this project.

## Current Stage Status — 2026-08-17

EasyUI is currently in the **Integration and Verification stage** (Conditional GO). 

### What is Completed:
- **Core Launcher baseline (`app` module)**: behves as default Android launcher, stable home screen, package-manager app discovery, app launcher, appearance options.
- **Senior Launcher (`senior-launcher` module)**: custom onboarding, large-touch home, pairing code display, caregiver PIN locks, emergency/SOS hold screen, voluntary check-in screen, notifications shade, custom local widgets, and background status reporting.
- **Caregiver Companion (`caregiver-companion` module)**: companion Android app with secure pairing input, seniors overview tab, check-in log, emergency alert view, settings pane, and suggestions staging.
- **Backend Orchestrator (`backend` module)**: Ktor bearer token auth, pairing endpoints, Status/Check-In/SOS Alert cache, and configuration suggestion queue.

### Active Focus:
- Documentation normalization (Priority 1)
- Feature inventory verification audit (Priority 2)
- End-to-end integration testing (pairing, authentication, telemetry, SOS alerts, and reminder suggestions sync)

---

## Phase 1 — Core Base Foundation (Completed)
- Stable Android launcher skeleton.
- Package discovery, real app list, and app launching.
- Persisted home layout grid selections and basic appearance settings.
- Build/lint/unit test gate setup.

## Phase 2 — Senior & Caregiver Implementation (Completed)
- Created `senior-launcher` module with accessible fonts/touch targets.
- Created `caregiver-companion` module with pairing and status widgets.
- Created Ktor Netty `backend` service with pairing and data endpoints.
- Implemented caregiver security PIN (SHA-256 with salt) and lockout behavior.

## Phase 3 — End-to-End Integration Verification (Active)
- **Objective**: Prove the complete workflow between the Senior Launcher, Caregiver Companion, and Backend service.
- **Key tasks**:
  1. Verify token authorization lifecycle.
  2. Test short-lived pairing code generation, entry, and device link establishment.
  3. Validate battery/charging status reporting and display.
  4. Verify voluntary check-in reporting and caregiver notification.
  5. Validate manual SOS button press triggering dialing and posting alert to backend.
  6. Verify suggestions push and pull (specifically remote reminders).

## Phase 4 — Offline, Failure & Security Hardening (Planned)
- **Objective**: Ensure the senior launcher remains fully functional offline and handles faults gracefully.
- **Key tasks**:
  - Test network connectivity drops and recovery transitions.
  - Audit database and preference storage file security.
  - Review token lifetime, secure transport (HTTPS/TLS in production), and brute-force lockout constraints.
  - Review and fix non-blocking lint warnings.

## Phase 5 — Accessibility & UI Polishing (Planned)
- **Objective**: Perform a dedicated senior accessibility audit.
- **Key tasks**:
  - Verify display scaling and font scaling support in the senior launcher.
  - Confirm Touch Target sizes (minimum 48dp) and contrast ratios.
  - Run TalkBack accessibility scans.
  - Refine error messages and user action feedback.

## Phase 6 — Release Preparation (Planned)
- **Objective**: Set up signing keys, production endpoints, and verify release builds.
- **Key tasks**:
  - Configure secure signing parameters.
  - Run `assembleRelease` builds.
  - Collect final verification assets and draft Google Play metadata.
