# Project Context — EasyUI

This repository is **EasyUI**, a multi-module senior/caregiver product. It is built on top of the **Core Launcher** foundation, which is a separate, frozen repository. EasyUI is an intentional product derivative of Core. Core's original product-variant and caregiver prohibitions do not govern EasyUI; therefore, caregiver functionality, backend integration, the Senior Launcher, and the Caregiver Companion app are fully valid, in-scope capabilities of EasyUI.

## Purpose

The purpose of EasyUI is to build a complete, stable senior-friendly launcher ecosystem with secure caregiver companion remote capabilities and a supporting backend server. EasyUI leverages the robust, tested local-first launcher baseline from Core and extends it with pairing, status reporting, emergency alerts, check-ins, and suggested reminders.

## Modules Overview

1. **`app`**: Reference launcher foundation derived from the Core baseline.
2. **`senior-launcher`**: Accessible senior-friendly smartphone launcher with simplified UI, emergency SOS triggers, check-in flows, local/remote reminders, and local configuration suggestion support.
3. **`caregiver-companion`**: Caregiver-facing application for pairing with a senior device, monitoring status, alerts, check-ins, and submitting reminder suggestions.
4. **`backend`**: Remote gateway orchestration service managing Ktor bearer authentication, secure pairing logic, and caching telemetry (status, check-ins, alerts, configuration).

## Product Philosophy

- **Stability First**: EasyUI must remain reliable and functional local-first. Network/backend availability is an enhancement, not a critical runtime dependency for the senior device launcher.
- **Explicit Consent**: Caregiver pairing requires explicit consent on the senior device launcher (via a short-lived pairing code displayed in the Privacy & Trust section).
- **User Privacy**: No private user contents (SMS, calls, logs, location) are sent to the backend. Sharing is strictly limited to battery status, voluntary check-ins, manual SOS alerts, and caregiver config suggestions.
- **Granular Permissions**: Permissions (battery status, alerts, config suggestions, check-ins) are controlled on the senior device launcher. If a permission is revoked, the corresponding data flow is halted.

## Launcher Customization Boundaries

While EasyUI supports advanced launcher-level customization (layout grid selection, pages, widgets, contacts, themes), it operates within Android platform rules:
- EasyUI does **not** replace the real Android status bar.
- EasyUI does **not** replace the notification shade or Quick Settings.
- EasyUI does **not** perform full device lock down or act as a device owner/MDM kiosk unless explicitly developed under managed configurations.
- A custom top bar inside the senior launcher is allowed, but it only displays launcher-level status.

## Greenfield Policy

EasyUI adheres to the Greenfield Policy. Although it derives from Core Launcher v0.1, we build features specifically for this ecosystem and do not import legacy state systems, workflows, or code from unrelated legacy codebases.
