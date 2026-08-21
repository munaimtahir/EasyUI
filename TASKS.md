# TASKS — EasyUI

This task list tracks the integration, verification, and hardening stages of the **EasyUI Senior & Caregiver Product Suite**. EasyUI is built on top of the frozen **Core Launcher** foundation, which is a separate repository. EasyUI is an intentional product derivative of Core. Therefore, Core's original product-variant prohibitions do not govern EasyUI.

## Stage 1 — Documentation Normalization

- [x] Identify all active documentation files containing legacy Core v0.1 variant restrictions.
- [x] Update `README.md` to define the EasyUI repository, modules, and relationship with Core.
- [x] Update `docs/PROJECT_CONTEXT.md` to detail the purpose and modules of EasyUI.
- [x] Update `docs/ROADMAP.md` to layout the active integration and verification roadmap.
- [x] Update `docs/BASELINE_SCOPE.md` to define EasyUI's in-scope caregiver and launcher features.
- [x] Update `docs/PRODUCT_GUARDRAILS.md` to protect senior consent, offline-first execution, and system limits.
- [x] Update `docs/ARCHITECTURE.md` to outline the modular design and state management rules.
- [x] Update `TASKS.md` to map current and future integration tasks.
- [x] Update `docs/TESTING/TESTING_STRATEGY.md` to outline verification layers and multi-module checks.
- [x] Update `docs/VERIFICATION/core-current-status-2026-08-17.md` (reconcile current status as EasyUI verification status).

## Stage 2 — Feature Inventory Audit

- [x] Audit the `backend` code for authentication, routing, and database capabilities.
- [x] Audit the `caregiver-companion` code for pairing inputs, tab navigation, and configuration suggest paths.
- [x] Audit the `senior-launcher` code for caregiver settings, security PINs, status workers, and emergency triggers.
- [x] Classify each capability (e.g. `VERIFIED`, `IMPLEMENTED_NOT_VERIFIED`, `PARTIAL`, `STUB`, `MISSING`).
- [x] Document the feature inventory results in an audit artifact.

## Stage 3 — End-to-End Integration Verification

- [x] Verify Ktor backend bearer token validation checks.
- [x] Test code pairing lifecycle: code generation on senior launcher → entry on companion → backend validation and token exchange.
- [x] Test status worker battery reporting and companion status updates.
- [x] Test voluntary check-in reporting from senior launcher and receipt on companion dashboard.
- [x] Test manual SOS hold triggering dialer activity and posting emergency alert to companion log.
- [x] Verify suggested reminders push from companion and successful pull/merge on senior launcher.
- [x] Run a test on paired permission revocations (e.g., revoking battery access disables status reporting).

## Stage 4 — Offline & Reconnect Hardening

- [x] Test senior launcher functionality when network connectivity is lost.
- [x] Verify that failed background status updates retry gracefully using WorkManager backoff constraints.
- [x] Ensure that UI operations (Check-In, SOS triggering) don't crash when offline; display clear feedback.
- [x] Test pairing revocation local data clearing.

## Stage 5 — Accessibility Audit

- [x] Audit display scaling and font scaling support across the senior launcher onboarding and home screens.
- [x] Confirm TalkBack content descriptions exist for all custom visual tiles and icons.
- [x] Validate touch target sizes (minimum 48dp) and contrast ratios.

## Stage 6 — Release Build & Production Readiness Verification

- [x] Run release build tasks: `./gradlew assembleRelease bundleRelease`.
- [x] Configure secure release signing templates and `.gitignore` exclusions.
- [x] Finalize Google Play metadata guidelines and Data Safety mapping in `RELEASE_READINESS.md`.
- [x] Prepare 14-day 10-pair pilot testing protocol in `PILOT_TEST_PLAN.md`.
