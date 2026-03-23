#!/usr/bin/env bash
set -euo pipefail
RUN_DIR="${1:?run dir required}"
node "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/postprocess-results.mjs" "$RUN_DIR"
