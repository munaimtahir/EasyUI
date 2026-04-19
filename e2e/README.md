# EasyUI E2E

ADB-driven Playwright test harness for EasyUI Senior Launcher.

- Static suites live in `tests/00_static`.
- Device suites stay serial with `workers: 1`.
- Artifacts are written to `device_test_runs/YYYYMMDD_HHMM/`.

Primary commands:

```bash
./e2e/scripts/run-static.sh
./e2e/scripts/run-device-smoke.sh 34081500040008N
./e2e/scripts/run-device-full.sh 34081500040008N
./e2e/scripts/run-release-device-suite.sh 34081500040008N
```

Release handoff command:

- `./e2e/scripts/run-release-device-suite.sh <adb-serial>` runs Gradle debug build + unit tests, connected Android tests, static Playwright checks, then the smoke and full ADB-driven Playwright suites.
- If exactly one physical device is attached, the serial argument is optional.
- The script writes a suite summary and combined log under `device_test_runs/release_suite_<timestamp>/`.
