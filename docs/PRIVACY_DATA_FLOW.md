# Privacy & Data Flow Document

This document outlines the data minimization, storage, and transmission policies implemented in the Senior Launcher and Caregiver Companion ecosystem.

## Data Transmission Rules

1. **No System Logs or Audio**: The app does not transmit microphone recording, call audio, SMS contents, browser history, or arbitrary device telemetry.
2. **Minimal Remote Telemetry**: Only the following status information is transmitted to the Backend if the corresponding caregiver permission is granted:
   - Battery level and charging status (useful to warn caregivers of a dead phone).
   - Sync timestamp.
   - App version.
   - Voluntary senior check-in events ("I am OK").
3. **No Silent Pairing**: Pairing is initiated by the senior user sharing a pairing token and must be approved in-app.
4. **Explicit Consent & Revocation**: The senior user can view all connected caregivers and revoke pairing/permissions at any time from the Trust / Privacy screen.
