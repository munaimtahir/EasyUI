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

## Current Project Status — 2026-08-17

EasyUI is in a **Conditional GO (development/verification stage)**. The launcher foundation and caregiver ecosystem features are substantially implemented. Automated builds, unit tests, and lint pass successfully. End-to-end integration and emulator verification are actively underway.

* For the latest reviewed status and planning decisions, see [docs/VERIFICATION/easyui-current-status-2026-08-17.md](file:///home/munaim/srv/apps/easyui/docs/VERIFICATION/easyui-current-status-2026-08-17.md).
* For the historical Core v0.1 baseline report, see [docs/VERIFICATION/core-v0.1-baseline-report.md](file:///home/munaim/srv/apps/easyui/docs/VERIFICATION/core-v0.1-baseline-report.md).

## Greenfield Policy

EasyUI is built upon clean greenfield principles. While we inherit the Core baseline launcher module, we do not copy or import legacy workflows, state systems, or UI flows from external legacy projects.

## Documentation Map

Important files:

* [AGENTS.md](file:///home/munaim/srv/apps/easyui/AGENTS.md)
* [GEMINI.md](file:///home/munaim/srv/apps/easyui/GEMINI.md)
* [TASKS.md](file:///home/munaim/srv/apps/easyui/TASKS.md)
* [copilot_session.md](file:///home/munaim/srv/apps/easyui/copilot_session.md)
* [docs/PROJECT_CONTEXT.md](file:///home/munaim/srv/apps/easyui/docs/PROJECT_CONTEXT.md)
* [docs/GREENFIELD_POLICY.md](file:///home/munaim/srv/apps/easyui/docs/GREENFIELD_POLICY.md)
* [docs/BASELINE_SCOPE.md](file:///home/munaim/srv/apps/easyui/docs/docs/BASELINE_SCOPE.md)
* [docs/PRODUCT_GUARDRAILS.md](file:///home/munaim/srv/apps/easyui/docs/PRODUCT_GUARDRAILS.md)
* [docs/ARCHITECTURE.md](file:///home/munaim/srv/apps/easyui/docs/ARCHITECTURE.md)
* [docs/ARCHITECTURE_PRODUCT.md](file:///home/munaim/srv/apps/easyui/docs/ARCHITECTURE_PRODUCT.md)
* [docs/ROADMAP.md](file:///home/munaim/srv/apps/easyui/docs/ROADMAP.md)
* [docs/TESTING/TESTING_STRATEGY.md](file:///home/munaim/srv/apps/easyui/docs/TESTING/TESTING_STRATEGY.md)
* [docs/DEFINITION_OF_DONE.md](file:///home/munaim/srv/apps/easyui/docs/DEFINITION_OF_DONE.md)
