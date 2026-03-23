#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERIAL="${1:-34081500040008N}"
RUN_ID="$(date +%Y%m%d_%H%M%S)"
RUN_DIR="$ROOT/device_test_runs/$RUN_ID"
mkdir -p "$RUN_DIR"/{logs,screenshots,recordings,ui_dumps,notes,results}
adb -s "$SERIAL" shell getprop ro.product.manufacturer > "$RUN_DIR/notes/manufacturer.txt"
adb -s "$SERIAL" shell getprop ro.product.model > "$RUN_DIR/notes/model.txt"
adb -s "$SERIAL" shell wm size > "$RUN_DIR/notes/size.txt"
adb -s "$SERIAL" shell wm density > "$RUN_DIR/notes/density.txt"
printf '%s\n' "$RUN_DIR"
