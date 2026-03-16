# AGENT.md

## Mission
Build EasyUI Senior Launcher as a caregiver-first, offline-first Android launcher for seniors. Preserve simplicity, clarity, and stability in every implementation decision.

## Hard rules
1. Do not expand scope into kiosk mode, device-owner control, or enterprise lockdown.
2. Do not add backend dependencies unless explicitly required in a future phase.
3. Do not ship ads in the first version.
4. Do not add decorative complexity that hurts readability or touch accuracy.
5. Do not use hidden gestures or multi-step interaction for essential actions.
6. Do not bury caregiver controls in places that seniors can easily trigger.
7. Do not market or imply system-level restrictions that the app cannot reliably enforce.
8. Maintain strong offline behavior; app must remain fully useful without network access.
9. Treat accessibility as a core product requirement, not a finishing pass.
10. Prefer stable defaults over extensive customization.

## Design guardrails
- Large touch targets
- High contrast
- Clear labels
- Few actions per screen
- Predictable navigation
- Minimal visual clutter
- Obvious feedback
- One obvious primary action per state

## Engineering guardrails
- Kotlin + Jetpack Compose
- Use Room for local persistence
- Use DataStore for preferences
- Isolate platform-dependent code behind interfaces
- Keep business rules testable outside UI
- Structure billing so the one-time unlock is easy to replace or disable for testing
- Avoid reflection or OEM-specific hacks as core features

## Delivery guardrails
- Work in small verified increments
- Keep issue checklist updated
- Ensure every feature has acceptance criteria and tests
- Document assumptions and device-specific risks
- Prefer a stable MVP over an overreaching first release

## Definition of done
A feature is done only when:
- behavior matches product docs
- edge cases are handled
- tests are added or updated
- manual QA steps are documented
- UX wording is clear and caregiver-friendly
