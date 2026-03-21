#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERIAL="${1:-34081500040008N}"
cd "$ROOT/e2e"
if [ ! -d node_modules ]; then
  npm install
fi
RUN_ID="$(date +%Y%m%d_%H%M%S)"
RUN_DIR="$ROOT/device_test_runs/$RUN_ID"
mkdir -p "$RUN_DIR"/{logs,screenshots,recordings,ui_dumps,notes,results}
export EASYUI_RUN_DIR="$RUN_DIR"
export EASYUI_DEVICE_SERIAL="$SERIAL"
npx playwright test tests/00_static tests/10_install_and_first_run tests/20_launcher_core/root-home-behavior.spec.ts tests/30_senior_home/home-visual-basics.spec.ts tests/30_senior_home/tile-actions.spec.ts tests/40_essential_actions/camera-flashlight-battery.spec.ts tests/50_caregiver/caregiver-entry.spec.ts tests/90_offline_and_guardrails/offline-first.spec.ts tests/80_permissions_oem_resilience/force-stop-relaunch.spec.ts
"$ROOT/e2e/scripts/postprocess-results.sh" "$RUN_DIR"
printf '%s\n' "$RUN_DIR"
