# Core Current Status — 2026-08-17

Project: `core` / Core Launcher Foundation  
Branch: `main`  
HEAD: `8fb2552`  
Planning status: **scope decision required before release**

## Executive status

The current repository builds and the available Android 15 emulator runtime is operational for the tested launch flows. The repository is not currently releasable as the documented `core v0.1` baseline because recent commits added a senior product variant, caregiver companion, backend networking, caregiver PIN, pairing, remote status/configuration, alerts, and related flows that the v0.1 guardrails explicitly exclude.

This is a scope and product-structure failure, not a current reproducible application crash.

## Verification completed

### Automated checks

```text
./gradlew clean assembleDebug testDebugUnitTest lintDebug
```

Result: **PASS** — `BUILD SUCCESSFUL` in 10m 58s; 166 actionable tasks, 162 executed.

The command covered the current Gradle modules, including `app`, `senior-launcher`, and `caregiver-companion`. Non-blocking warnings remain for deprecated Wi-Fi APIs, unused Kotlin variables/parameters, and DataStore native-library stripping.

### Emulator checks

Device: `emulator-5554`, Android 15, x86_64.

- Installed `com.easyui.core`, `com.easyui.senior`, and `com.easyui.companion` successfully.
- Core launched, became the default HOME activity, rendered the home screen, opened the app drawer, listed installed apps, searched for Chrome, and returned to HOME.
- Senior launcher completed its three-step setup and rendered its home surface with the product-specific controls.
- Companion pairing screen launched and remained foregrounded.
- No application-specific `FATAL EXCEPTION`, `ANR in`, or `am_crash` evidence was found for the tested packages.

The emulator briefly went offline and Android System UI displayed a transient “System UI isn't responding” dialog during recovery. ADB reconnection and selecting `Wait` restored the device. This was emulator/system infrastructure instability, not an observed application process crash.

## Recent implementation history

- `a368ada` — imported the Core Launcher Foundation baseline.
- `ecb2e4c` — added the senior launcher/caregiver ecosystem sprint specification.
- `a086c75` — added senior product screens and companion app.
- `0c3be96` — added backend, pairing, remote status/configuration, and caregiver workflows.
- `808a33e` — documented the caregiver sprint as complete.
- `8fb2552` — merged the divergent histories while retaining both baseline and caregiver suite.

## Finalized decisions

1. `core v0.1` remains launcher-only unless the planning owner explicitly changes the product stage.
2. Caregiver PIN, senior mode, remote pairing, cloud/backend sync, companion app, alerts, check-ins, and remote configuration are not part of `core v0.1`.
3. The 2026-08-09 baseline report remains historical evidence for the earlier launcher tree. It is not the current release verdict.
4. The current runtime health is **Conditional GO**: tested packages launch and the Core workflow passes, but emulator infrastructure instability occurred and the full connected instrumentation workflow was not rerun after the later commits.
5. The current v0.1 release verdict is **NO-GO** until the repository has one authoritative scope.

## Planning decision required

Choose one path before further feature work:

### Path A — Restore `core v0.1`

Remove or isolate `senior-launcher`, `caregiver-companion`, `backend`, and related caregiver/remote documentation from the v0.1 build and rerun the complete launcher verification suite.

### Path B — Create a separate product stage

Keep the caregiver ecosystem, but document it as a separate product stage/repository or explicitly separated build track. Update scope, architecture, privacy, threat model, testing, and release documentation before claiming completion.

Until this choice is made, do not add additional product features or declare a production release.

## Source of truth

This document is the current planning/status context. Historical baseline evidence is in [`core-v0.1-baseline-report.md`](core-v0.1-baseline-report.md). Session-level command details are in [`../../copilot_session.md`](../../copilot_session.md).

## Final verdict

**CONDITIONAL GO for current emulator runtime health; NO-GO for `core v0.1` release readiness.**
