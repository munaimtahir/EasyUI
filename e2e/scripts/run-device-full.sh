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
export ANDROID_SERIAL="$SERIAL"
npx playwright test
"$ROOT/e2e/scripts/postprocess-results.sh" "$RUN_DIR"
printf '%s\n' "$RUN_DIR"
