# Product Guardrails — EasyUI

This document protects the **EasyUI Senior & Caregiver Product Suite** from scope creep, security oversights, and unrealistic system claims. EasyUI is built on top of the frozen **Core Launcher** foundation, which is a separate repository. EasyUI is an intentional product derivative of Core. Therefore, Core's original product-variant prohibitions do not govern EasyUI, and caregiver monitoring capabilities are valid within-scope features of this project.

## Guardrail 1 — No full-lockdown promise
Do not claim that EasyUI can fully block Android Settings, system UI, notification shade, OEM pull-downs, or all non-launcher behavior on normal consumer devices. It is a home launcher replacement, not an MDM lockdown tool.

## Guardrail 2 — No enterprise MDM positioning
Do not describe the product as mobile device management (MDM), enterprise lockdown, kiosk mode, or complete parent/guardian device control.

## Guardrail 3 — Local-first resilience (Offline-first)
The senior launcher must remain fully usable and resilient when offline. Network/backend availability is an enhancement, not a dependency for core operations. The senior launcher must not crash or hang if the network is absent or the backend is offline. Local emergency dialing and local reminder alerts must work reliably under any network state.

## Guardrail 4 — Explicit Senior Consent
No caregiver pairing can occur without explicit, voluntary consent on the senior device launcher. The senior must initiate pairing in the Privacy & Trust section to generate and display a short-lived pairing code. The senior must also have the ability to disconnect and revoke the pairing locally at any time.

## Guardrail 5 — Permission-respecting Telemetry
The caregiver can only access telemetry (battery status, alerts, check-ins, configurations) for which the senior has granted permission. The backend and the client applications must respect these permission boundaries.

## Guardrail 6 — No monetization
The project must not include ads, subscriptions, premium tiers, or in-app payment logic.

## Guardrail 7 — No hidden workflows before visible workflows
Do not implement hidden caregiver settings entries, remote commands, or secret layouts. Control paths must be visible, transparent, and easy to manage via the local settings UI.

## Guardrail 8 — Customization boundaries
Allowed launcher-level customizations include:
- Home screen layout selection (grid sizes, page management, label toggles).
- App drawer customizations (favorites, search, layout modes).
- App styling (font scaling, icon size, high-contrast themes).
- Pinned tiles (contacts, built-in widgets, clock/date, battery).

Not allowed:
- Replacing the real Android status bar.
- Replacing the OEM notification shade or Quick Settings.
- Blocking notification pull-downs or OEM control centers.
- Directly changing restricted system-level hardware settings (e.g. toggling mobile data or Wi-Fi state directly without going through system dialogs).
