#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERIAL="${1:-}"
STAMP="$(date +%Y%m%d_%H%M%S)"
SUITE_DIR="$ROOT/device_test_runs/release_suite_$STAMP"
SUMMARY_FILE="$SUITE_DIR/SUITE_SUMMARY.md"
LOG_FILE="$SUITE_DIR/release-suite.log"

mkdir -p "$SUITE_DIR"

resolve_serial() {
  if [ -n "${SERIAL}" ]; then
    printf '%s\n' "$SERIAL"
    return 0
  fi

  mapfile -t devices < <(adb devices | awk 'NR>1 && $2=="device" {print $1}')
  if [ "${#devices[@]}" -eq 1 ]; then
    printf '%s\n' "${devices[0]}"
    return 0
  fi

  if [ "${#devices[@]}" -eq 0 ]; then
    printf 'No attached ADB device found. Attach one device or pass a serial explicitly.\n' >&2
  else
    printf 'Multiple attached ADB devices found. Pass the target serial explicitly.\n' >&2
    printf 'Detected: %s\n' "${devices[*]}" >&2
  fi
  exit 1
}

run_step() {
  local label="$1"
  shift
  printf '\n[%s] %s\n' "$(date -u +%H:%M:%S)" "$label" | tee -a "$LOG_FILE"
  "$@" 2>&1 | tee -a "$LOG_FILE"
}

SERIAL="$(resolve_serial)"

cat > "$SUMMARY_FILE" <<EOF
# Release Device Suite

- Started: $(date -u +"%Y-%m-%d %H:%M:%S UTC")
- Device serial: \`$SERIAL\`
- Repo root: \`$ROOT\`

## Stages

- [ ] Gradle debug build and unit tests
- [ ] Connected Android instrumentation tests
- [ ] Static Playwright checks
- [ ] Playwright smoke device suite
- [ ] Playwright full device suite

## Outputs

- Suite log: \`$LOG_FILE\`
EOF

printf 'Release device suite directory: %s\n' "$SUITE_DIR" | tee -a "$LOG_FILE"
printf 'Using ADB serial: %s\n' "$SERIAL" | tee -a "$LOG_FILE"

run_step "Gradle debug build and unit tests" \
  "$ROOT/gradlew" ":app:assembleDebug" ":app:testDebugUnitTest" --no-daemon --console=plain
perl -0pi -e 's/- \[ \] Gradle debug build and unit tests/- [x] Gradle debug build and unit tests/' "$SUMMARY_FILE"

run_step "Connected Android instrumentation tests" \
  env ANDROID_SERIAL="$SERIAL" "$ROOT/gradlew" connectedDebugAndroidTest --no-daemon --console=plain
perl -0pi -e 's/- \[ \] Connected Android instrumentation tests/- [x] Connected Android instrumentation tests/' "$SUMMARY_FILE"

STATIC_DIR="$(run_step "Static Playwright checks" \
  env EASYUI_DEVICE_SERIAL="$SERIAL" ANDROID_SERIAL="$SERIAL" "$ROOT/e2e/scripts/run-static.sh" | tail -n 1)"
perl -0pi -e 's/- \[ \] Static Playwright checks/- [x] Static Playwright checks/' "$SUMMARY_FILE"

SMOKE_DIR="$(run_step "Playwright smoke device suite" \
  env EASYUI_DEVICE_SERIAL="$SERIAL" ANDROID_SERIAL="$SERIAL" "$ROOT/e2e/scripts/run-device-smoke.sh" "$SERIAL" | tail -n 1)"
perl -0pi -e 's/- \[ \] Playwright smoke device suite/- [x] Playwright smoke device suite/' "$SUMMARY_FILE"

FULL_DIR="$(run_step "Playwright full device suite" \
  env EASYUI_DEVICE_SERIAL="$SERIAL" ANDROID_SERIAL="$SERIAL" "$ROOT/e2e/scripts/run-device-full.sh" "$SERIAL" | tail -n 1)"
perl -0pi -e 's/- \[ \] Playwright full device suite/- [x] Playwright full device suite/' "$SUMMARY_FILE"

cat >> "$SUMMARY_FILE" <<EOF
- Static run dir: \`$STATIC_DIR\`
- Smoke run dir: \`$SMOKE_DIR\`
- Full run dir: \`$FULL_DIR\`

## Handback

Upload these back for triage if any stage fails:

- \`$SUMMARY_FILE\`
- \`$LOG_FILE\`
- \`$SMOKE_DIR\`
- \`$FULL_DIR\`
EOF

printf '\nRelease device suite finished.\nSummary: %s\n' "$SUMMARY_FILE" | tee -a "$LOG_FILE"
