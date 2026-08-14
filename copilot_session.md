# copilot_session.md — Full Sprint Continuation

## Goal
Execute all remaining phases of the master implementation sprint starting from commit `a086c75`.
Deliver GO / CONDITIONAL GO / NO-GO verdict against the **complete** original sprint, not just
implemented phases.

---

## Phase Reconciliation Status (against original master sprint)

| Phase | Name | Status |
|-------|------|--------|
| A | Bootstrap (modules, Gradle) | ✅ DONE |
| B | Security — PIN hashing, lockout | ✅ DONE |
| C | Navigation wiring (all screens routed) | ✅ DONE |
| D | Senior screens (Emergency, Notifications, Reminders, TrustCenter) | ✅ DONE |
| E | Caregiver Companion app (tabbed UI) | ✅ DONE |
| F | Unit + instrumentation tests (Phase B-D coverage) | ✅ DONE |
| G | Lint / quality gates (0 errors) | ✅ DONE |
| H | Pairing token generation + QR display (senior side) | ❌ STUB |
| I | Pairing code entry + validation (companion side) | ❌ STUB |
| J | Backend — Ktor HTTP server, pairing, status, alert routes | ❌ STUB (prints only) |
| K | Device status reporting (battery, sync ts, app version) | ❌ NOT STARTED |
| L | Voluntary check-in ("I'm OK" button) | ❌ NOT STARTED |
| M | Alert routing — SOS → backend → companion push (FCM-free local) | ❌ NOT STARTED |
| N | Remote config staging (caregiver sends reminders to senior) | ❌ NOT STARTED |
| O | Caregiver permission enforcement on senior device | ❌ NOT STARTED |
| P | Privacy/Security hardening (TLS config, secret constants, OWASP review) | ❌ NOT STARTED |
| Q | Accessibility (touch targets ≥48dp, TalkBack labels, font scaling) | ❌ NOT STARTED |
| R | Release build (assembleRelease, ProGuard, signing) | ❌ NOT STARTED |
| S | Backend unit tests (routes, auth, permissions) | ❌ NOT STARTED |
| T | Final end-to-end verification report | ❌ NOT STARTED |

---

## Implementation Plan

### Phase H — Pairing token generation (senior-launcher)
- Generate cryptographically random 8-char pairing code
- Display code + manual instructions on `TrustCenterScreen`
- Store pending pairing token in DataStore with expiry
- Confirm pairing upon companion connection

### Phase I — Pairing code entry (caregiver-companion)
- Allow caregiver to enter 8-char code
- POST code to backend
- On success, store paired device ID in DataStore

### Phase J — Backend (Ktor + in-memory store)
- Ktor HTTP server on port 8080
- Routes: POST /pair, POST /status, GET /status/{deviceId}, POST /alert, GET /alerts/{deviceId}, POST /config, GET /config/{deviceId}
- In-memory datastore (sufficient for dev/local use; clearly documented)
- Bearer token auth (device token issued at pairing)
- Unit tests for all routes

### Phase K — Device status reporting (senior → backend)
- Senior launcher WorkManager periodic job
- Reads battery level (BatteryManager), charging state, sync timestamp
- POSTs to backend /status every 15 min (configurable)

### Phase L — Voluntary check-in
- "I'm OK" button in senior launcher (quick access panel)
- POSTs check-in event to backend
- Companion displays last check-in time

### Phase M — Alert routing (SOS → companion)
- SOS trigger → POST to backend /alert
- Backend stores alert with timestamp
- Companion polls GET /alerts (no FCM required for dev)
- Companion shows alert in Alerts tab with timestamp

### Phase N — Remote config (caregiver → senior)
- Caregiver can POST /config with reminder list
- Senior launcher polls GET /config, applies incoming reminders to local DataStore

### Phase O — Permission enforcement
- PairingState holds granted permissions (battery, checkin, config)
- Senior launcher only sends data / accepts config if permission is in PairingState
- TrustCenterScreen shows active permissions and allows selective revoke

### Phase P — Security hardening
- BuildConfig.DEBUG gating for any dev URLs
- HTTP timeout config
- Input validation on pairing code (length, charset)
- SHA-256 comparison uses constant-time compare
- Minimum touch target enforcement (48dp)

### Phase Q — Accessibility
- Add contentDescription to all icon-only buttons
- Verify all Text elements scale with SP
- Touch targets ≥ 48dp on all interactive elements
- TalkBack-friendly semantic roles

### Phase R — Release build
- Add signing config to senior-launcher and caregiver-companion
- Run `assembleRelease`
- Confirm R8/ProGuard does not strip critical classes

### Phase S — Backend tests
- JUnit tests for /pair, /status, /alert, /config routes
- Auth rejection test (wrong token)
- Permission enforcement test

### Phase T — Final verification report

---

## Next Action: Phase J (Backend) first — all client-side network code depends on the server routes being defined.
