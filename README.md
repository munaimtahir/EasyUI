# EasyUI Senior Launcher

EasyUI Senior Launcher is an offline-first Android launcher for seniors. It is designed to simplify the home screen, reduce accidental changes, and give caregivers a stable way to configure a phone once and maintain it with minimal ongoing support.

This repository now has three canonical areas:

- `docs/` contains the consolidated product, engineering, delivery, and AI guidance.
- `app/`, `core/`, and `feature/` contain the Android project scaffold that matches the documented architecture.
- `archive/legacy-docs/` preserves the original documentation packs and duplicate source material.

## Product truth

This product is not a kiosk app, device-owner solution, or enterprise lockdown tool. The scope is a consumer launcher that improves clarity and stability on normal Android devices without making OS-level promises it cannot reliably enforce.

## Planned stack

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- Room
- DataStore
- Play Billing for one-time premium unlock
- No backend in MVP or v1

## Documentation map

- [Docs Overview](/home/munaim/Documents/github/easyui/docs/README.md)
- [Current Status](/home/munaim/Documents/github/easyui/docs/delivery/current-status.md)
- [Product Brief](/home/munaim/Documents/github/easyui/docs/product/project-brief.md)
- [Architecture](/home/munaim/Documents/github/easyui/docs/engineering/architecture.md)
- [Implementation Roadmap](/home/munaim/Documents/github/easyui/docs/engineering/tasks.md)
- [Agent Guide](/home/munaim/Documents/github/easyui/AGENTS.md)

## Project layout

The current implementation follows this split:

- `app` for application shell, navigation host, launcher manifest, and dependency wiring
- `feature/home`, `feature/apps`, `feature/onboarding`, and `feature/caregiver` for the current user-facing flows
- `core/*` for shared UI, domain rules, local persistence, platform integration, and test support

## Current implementation snapshot

Implemented in the current build:

- first-run intro and default launcher guidance
- caregiver help screen with honest setup guidance
- fixed six-tile senior home
- clock/date header and status summary
- hidden caregiver access through a deliberate home gesture, with clock fallback
- starter layout persisted in Room
- launcher settings persisted in DataStore
- Home essentials on the senior surface: Phone, Flashlight, Camera, Emergency, Health Info, and SOS
- flashlight, emergency, camera, and dialer tiles with graceful fallback messaging
- installed app enumeration with refresh on package changes
- caregiver-managed Home Apps placement in fixed slots
- caregiver PIN setup and verification
- layout lock and protected edit mode
- hidden app management inside EasyUI
- offline Health Info storage and viewing
- favorite contact tiles with optional local photo and initials fallback
- home readability presets and very simple mode
- battery visibility toggle
- backup export/import and reset-to-safe-default flow

Present but not yet fully surfaced:

- the app list screen and search are implemented, but the senior-facing home entry point is still being wired in this build

Deferred on purpose:

- billing and premium unlock
- system-wide blocking or kiosk-style controls

## Setup

Requirements:

- Android Studio Ladybug or newer
- JDK 17
- Android SDK with API 35 installed

Common commands:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew assembleDebugAndroidTest
```

Run flow:

1. Open the project in Android Studio.
2. Sync Gradle.
3. Install the `app` module on an emulator or physical device.
4. On first launch, follow the intro and default-launcher guidance screens.
5. Set EasyUI as the default home app from system settings if you want to test launcher replacement behavior.

## Manual QA notes

- Default launcher:
  Open EasyUI, go through setup, tap `Open Default App Settings`, and select EasyUI as the home app. Confirm that pressing Home returns to EasyUI.
- Reboot continuity:
  After EasyUI is set as the default launcher, reboot the device and confirm the home button still resolves to EasyUI.
- Missing target app:
  Remove or disable an app that appears in the starter layout. The tile should show `App not installed` and remain non-destructive.
- Flashlight unsupported:
  On a device with no torch, or where torch access is blocked, the flashlight tile should stay disabled and show a fallback message instead of crashing.
- Caregiver protection:
  Long-press the top status bar, set a local PIN, enable protection, then confirm caregiver sections ask for the PIN when protection is enabled.
- Caregiver re-entry:
  Confirm the hidden caregiver entry path still opens caregiver settings and remains PIN-protected when enabled.
- Hidden apps:
  Hide one installed app and confirm it disappears from EasyUI app surfaces and search results where those surfaces are exposed.
- Health info:
  In `Caregiver Settings`, edit Health Info and confirm the home `Health Info` tile opens readable saved details.
- Favorite contacts:
  In `Caregiver Tools`, open `Manage Favorite Contacts`, add a name and phone number, optionally choose a local photo, and confirm the tile appears on Home and opens the dialer instead of placing a direct call.
- Home readability:
  In `Caregiver Tools`, open `Home Readability`, switch between `Standard`, `Larger Text`, `Larger Tiles`, and `Extra Simple Spacing`, then confirm the EasyUI home screen updates and persists after relaunch.
- Very simple mode:
  Turn on `Very simple home mode` and confirm the home screen shows a calmer subset of tiles with more spacing. Turn it off and confirm the normal home returns.
- Visibility presets:
  Open `Show or Hide Apps`, apply `Essentials Only`, and confirm that non-essential apps disappear from EasyUI surfaces while remaining installed on Android.
- Finish setup:
  From `Caregiver Tools`, tap `Finish Setup` and confirm the app returns to the senior-facing home screen without entering another setup wizard.
- Reset flow:
  Hide apps, add a favorite contact tile, and change the home layout, then run `Reset to Safe Default` and confirm the starter layout returns, favorite contact tiles are removed, and hidden apps become visible again.

## Notes

- The app requests `CAMERA` because torch control may require it on some devices.
- Android launcher behavior varies by OEM. The default-home setup flow may differ slightly across Samsung, Xiaomi, and stock Android devices.
- The caregiver PIN is a local launcher-level barrier. There is no cloud recovery and it does not secure Android outside this launcher.
- Hidden apps are hidden only inside EasyUI. They are not blocked system-wide.
- Favorite contact tiles use the Android dialer intent path. EasyUI does not place direct calls itself.
- Contact photos are optional and local. If the chosen image URI later becomes unavailable, the launcher falls back to initials.
- Home readability presets and very simple mode affect EasyUI only. They do not change the rest of Android.
- The Room database now includes a real `1 -> 2` migration for the contact-tile schema instead of relying on destructive migration fallback.
