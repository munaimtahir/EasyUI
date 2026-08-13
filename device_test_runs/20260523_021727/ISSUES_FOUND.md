# Issues Found

## A3-ISSUE — Manifest includes ACCESS_NETWORK_STATE despite offline-first product scope
- severity: P2
- type: scope-alignment
- affected workflow: Permission declaration audit
- reproduction steps: Inspect AndroidManifest.xml
- expected result: Only permissions strictly needed for implemented launcher actions should be declared.
- actual result: ACCESS_NETWORK_STATE is declared, but the current offline-first launcher flow does not clearly justify it.
- evidence: screenshots/a3.png, ui_dumps/a3.xml
- suspected area: app/src/main/AndroidManifest.xml
- recommended fix direction: Remove ACCESS_NETWORK_STATE unless a shipped feature depends on it and is documented.

