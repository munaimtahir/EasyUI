# EasyUI Senior Launcher — AI Developer Pack

This pack defines the product, scope, architecture, acceptance criteria, and autonomous build instructions for **EasyUI Senior Launcher**.

## Product summary
EasyUI Senior Launcher is an offline-first Android launcher for seniors, designed to simplify the phone interface and reduce common accidental changes. The product is aimed primarily at caregivers and adult children who set up and maintain phones for older relatives. It focuses on large touch targets, simple app access, emergency shortcuts, and premium caregiver controls such as layout locking, app hiding, and PIN-protected editing.

## Core promise
A simpler, safer, more stable Android home screen for seniors.

## Commercial angle
Reduce tech-support headaches for caregivers.

## Product truth
This is **not** a full device-lockdown or enterprise kiosk product. It should never promise OS-level control that a normal consumer launcher cannot reliably enforce across devices.

## Pack contents
- `PROJECT_BRIEF.md` — plain-language product and market fit
- `GOALS.md` — outcomes from MVP to v1.0
- `AGENT.md` — guardrails and execution rules for AI agents
- `ARCHITECTURE.md` — technical architecture and module boundaries
- `DATAMODEL.md` — app data structures and persistence rules
- `API_INTERFACES.md` — internal interfaces and platform boundaries
- `SETUP.md` — local development environment and conventions
- `TESTS.md` — unit, UI, and manual acceptance tests
- `QA_CHECKLIST.md` — release and verification checklist
- `CI_CD.md` — continuous integration and release workflow
- `TASKS.md` — implementation roadmap and issue checklist
- `FINAL_AI_DEVELOPER_PROMPT.md` — autonomous build prompt
- `CONTRIBUTING.md` — team and agent contribution rules
- `.github/ISSUE_TEMPLATE/*` — reusable issue templates

## Recommended stack
- Kotlin
- Jetpack Compose
- AndroidX Navigation
- Room
- DataStore
- Play Billing for one-time premium unlock
- No backend for v1

## Scope boundaries
Do build:
- replacement launcher behavior
- simplified home screen
- simple app list
- emergency and flashlight shortcuts
- caregiver edit lock
- app hiding
- layout locking
- photo contacts
- offline storage
- backup/restore of local configuration

Do not build in this track:
- full kiosk mode
- device-owner management
- remote caregiver dashboard
- cloud account system
- subscription-first billing
- invasive analytics
