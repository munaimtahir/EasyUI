# EasyUI Senior Launcher Master Release Packet

## Purpose

This directory is the authoritative Google Play submission packet for `EasyUI Senior Launcher`.

Use this packet instead of the earlier draft documents in `docs/delivery/` when preparing the final Play submission.

## Product lock

EasyUI Senior Launcher is:

- a simple Android launcher for seniors
- caregiver-configured
- designed to make phones easier to read, easier to tap, easier to navigate, and harder to accidentally disrupt

EasyUI Senior Launcher is not:

- a kiosk shell
- enterprise MDM
- parental control software
- a cloud-first product
- a remote monitoring product
- a full Android lockdown system

## Verified build state

- Application ID: `com.easyui.launcher`
- `minSdk`: 26
- `targetSdk`: 35
- `compileSdk`: 35
- `versionCode`: 1
- `versionName`: `1.0.0`
- Runtime permissions: none
- Release shrink/minify: enabled
- Release outputs verified:
  - `app/build/outputs/apk/release/app-release-unsigned.apk`
  - `app/build/outputs/bundle/release/app-release.aab`

## Policy truth

- No login or account
- No ads
- No analytics SDK
- No crash-reporting SDK
- No backend/server transfer found in app code
- Local-only storage for launcher state, caregiver settings, hidden apps, and favorite contact metadata
- Favorite contact tiles open the dialer with `ACTION_DIAL`
- Flashlight support is optional and hardware-dependent

## What this packet contains

- final store listing copy
- final policy/declaration drafts
- screenshot and feature graphic capture instructions
- short real-phone QA steps
- Play Console runbooks
- dependency warning triage
- final go/no-go decision

## Operator workflow

1. Read [FINAL_STATUS.md](/home/munaim/srv/apps/easyui/release/final/FINAL_STATUS.md).
2. Complete [OPEN_MANUAL_ITEMS.md](/home/munaim/srv/apps/easyui/release/final/OPEN_MANUAL_ITEMS.md).
3. Use [PLAY_CONSOLE_RUNBOOK_FINAL.md](/home/munaim/srv/apps/easyui/release/final/console/PLAY_CONSOLE_RUNBOOK_FINAL.md).
4. Paste text from the files under `release/final/store` and `release/final/policy`.
5. Run the real-phone checks under `release/final/qa`.
6. Confirm the final gate in [GO_NO_GO_FINAL.md](/home/munaim/srv/apps/easyui/release/final/GO_NO_GO_FINAL.md).
