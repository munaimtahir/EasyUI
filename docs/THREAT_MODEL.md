# Security Threat Model

This document identifies security threats and the mitigations designed into the system.

## Threat Analysis & Mitigations

### 1. Stolen Caregiver Phone
* **Threat**: An attacker obtains the caregiver's phone and gains access to the senior's device status, battery level, or changes senior launcher configuration.
* **Mitigation**: Caregiver app requires authentication or local PIN check where appropriate. Senior can revoke pairing instantly from the launcher.

### 2. Pairing Token Theft / Replay
* **Threat**: An attacker intercepts a pairing token and attempts to pair with the senior's device.
* **Mitigation**: Pairing tokens are short-lived, single-use, cryptographically random, and require explicit senior confirmation on the launcher UI after scanning/entering.

### 3. Unauthorized API Requests
* **Threat**: An attacker attempts to query the backend for another user's device status or send remote commands.
* **Mitigation**: The backend strictly validates user authentication, linked relationships, resource ownership, and permission scopes for every API request.

### 4. Local Caregiver Mode PIN Guessing
* **Threat**: The senior user or another person guesses the caregiver PIN to bypass layout locks.
* **Mitigation**: The launcher hashes the PIN using SHA-256 with salt before storage. It implements rate limiting/lockouts (e.g. 5 failures triggers a cooldown).
