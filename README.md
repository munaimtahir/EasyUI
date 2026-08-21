# EasyUI

This repository is **EasyUI**, a multi-module senior/caregiver product. It is built on top of the **Core Launcher** foundation, which is a separate, frozen repository. EasyUI is an intentional product derivative of Core; therefore, Core's original product-variant and caregiver prohibitions do not govern EasyUI. Caregiver functionality, the `senior-launcher` module, the `caregiver-companion` app, and the Netty `backend` service are valid, in-scope features of this project.

## Internal Identity

Repository name:
```text
easyui
```

Internal project name:
```text
EasyUI Senior & Caregiver Product Suite
```

Base lineage:
```text
Core Launcher Foundation (Frozen baseline)
```

## Project Structure

This repository contains the following modules:

* **`app`**: Core-derived launcher foundation, kept as baseline/reference functionality.
* **`senior-launcher`**: A simplified, accessible Android launcher featuring senior-friendly layouts, dialing/contacts shortcuts, widgets, notifications pane, and caregiver security capabilities (pairing, status reporting, check-in, SOS alerts).
* **`caregiver-companion`**: A standalone companion Android app for caregivers to monitor senior device battery, receive voluntary check-ins, receive emergency alerts, and suggest reminders.
* **`backend`**: A Ktor/Netty service routing authentication, secure pairing, alerts, status, and configuration updates.

## Current Project Status — 2026-08-22

EasyUI has achieved **v1.0 Release Candidate 2 (RC2)** status:
* **Release Version**: `1.0.0` (versionCode 1)
* **SDK Target**: `compileSdk = 36`, `targetSdk = 36` (Android 16 / Baklava compliant), `minSdk = 24`
* **Release Artifacts**: R8-minified signed/optimized Release APKs and App Bundles (AAB) for `senior-launcher`, `caregiver-companion`, and `app`.
* **Multi-API Verification**: 100% PASS (26/26 connected tests) on both Android 15 (API 35) and Android 16 (API 36).
* **Accessibility Gate**: Dedicated automated tests (`SeniorAccessibilityTest`) passing for 2.0x font scaling, >=48dp touch targets, and TalkBack semantics.
* **Environment Separation**: Clean DEV vs STAGING vs PRODUCTION build targets.
* **Security & Privacy**: Strict HTTPS in production, SHA-256 PIN hashing, zero cleartext traffic in release, and full Google Play Data Safety mapping.
* **Release Documentation**: See [`RELEASE_READINESS.md`](file:///media/munaim/shared1/Documents/github/easyui/RELEASE_READINESS.md), [`BACKEND_DEPLOYMENT.md`](file:///media/munaim/shared1/Documents/github/easyui/BACKEND_DEPLOYMENT.md), and [`PILOT_TEST_PLAN.md`](file:///media/munaim/shared1/Documents/github/easyui/PILOT_TEST_PLAN.md).

## Greenfield Policy

EasyUI is built upon clean greenfield principles. While we inherit the Core baseline launcher module, we do not copy or import legacy workflows, state systems, or UI flows from external legacy projects.

## Documentation Map

Important files:

* [RELEASE_READINESS.md](file:///media/munaim/shared1/Documents/github/easyui/RELEASE_READINESS.md)
* [BACKEND_DEPLOYMENT.md](file:///media/munaim/shared1/Documents/github/easyui/BACKEND_DEPLOYMENT.md)
* [PILOT_TEST_PLAN.md](file:///media/munaim/shared1/Documents/github/easyui/PILOT_TEST_PLAN.md)
* [DEVICE_TESTING_PLAN.md](file:///media/munaim/shared1/Documents/github/easyui/DEVICE_TESTING_PLAN.md)
* [AGENTS.md](file:///media/munaim/shared1/Documents/github/easyui/AGENTS.md)
* [GEMINI.md](file:///media/munaim/shared1/Documents/github/easyui/GEMINI.md)
* [TASKS.md](file:///media/munaim/shared1/Documents/github/easyui/TASKS.md)
* [copilot_session.md](file:///media/munaim/shared1/Documents/github/easyui/copilot_session.md)
* [docs/PROJECT_CONTEXT.md](file:///media/munaim/shared1/Documents/github/easyui/docs/PROJECT_CONTEXT.md)
* [docs/GREENFIELD_POLICY.md](file:///media/munaim/shared1/Documents/github/easyui/docs/GREENFIELD_POLICY.md)
* [docs/BASELINE_SCOPE.md](file:///media/munaim/shared1/Documents/github/easyui/docs/BASELINE_SCOPE.md)
* [docs/PRODUCT_GUARDRAILS.md](file:///media/munaim/shared1/Documents/github/easyui/docs/PRODUCT_GUARDRAILS.md)
* [docs/ARCHITECTURE.md](file:///media/munaim/shared1/Documents/github/easyui/docs/ARCHITECTURE.md)
* [docs/ARCHITECTURE_PRODUCT.md](file:///media/munaim/shared1/Documents/github/easyui/docs/ARCHITECTURE_PRODUCT.md)
* [docs/ROADMAP.md](file:///media/munaim/shared1/Documents/github/easyui/docs/ROADMAP.md)
* [docs/TESTING/TESTING_STRATEGY.md](file:///media/munaim/shared1/Documents/github/easyui/docs/TESTING/TESTING_STRATEGY.md)
* [docs/DEFINITION_OF_DONE.md](file:///media/munaim/shared1/Documents/github/easyui/docs/DEFINITION_OF_DONE.md)
