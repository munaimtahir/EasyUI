# Architecture

## Overview

EasyUI Senior Launcher is a single Android app built with Kotlin and Compose. It is offline-first, stores setup data locally, and does not depend on a backend or account system.

## Module responsibilities

- `app`
  - startup
  - dependency wiring
  - navigation graph
  - launcher registration
- `feature/home`
  - senior home surface
  - health info viewer
  - tile layout presentation
- `feature/apps`
  - installed-app list and search surface
- `feature/caregiver`
  - caregiver settings
  - PIN setup and verification
  - home-app assignment
  - hidden apps
  - emergency and SOS settings
  - backup and restore
- `feature/onboarding`
  - intro
  - default launcher guidance
  - caregiver help
- `core/domain`
  - layout rules
  - visibility rules
  - fallback rules
  - reset rules
- `core/data`
  - Room
  - DataStore
  - backup serialization
  - repository implementations
- `core/platform`
  - PackageManager wrappers
  - flashlight
  - camera launch
  - dial and emergency actions
  - battery and device status
- `core/ui`
  - shared components
  - current typography, color, spacing, and tile styling system
- `core/testing`
  - shared fixtures and test helpers

## Current runtime model

### Onboarding

`Intro -> Default Launcher Guidance -> Caregiver Help -> Home`

- onboarding is fully local
- onboarding copy must stay honest about launcher limitations
- long onboarding content must remain usable on smaller displays

### Senior daily use

- home is a fixed essentials grid
- the visible essentials are `Phone`, `Flashlight`, `Camera`, `Emergency`, `Health Info`, and `SOS`
- caregiver access is hidden behind a deliberate top-bar long-press, with a clock-tap fallback
- the app-list screen exists, but the senior-facing home entry is still not wired in this build
- home-app slots are caregiver-managed and bounded; the senior surface is not freeform

### Caregiver flow

- caregiver enters through the hidden home gesture
- PIN is required when protection is enabled
- caregiver configures layout, hidden apps, contact shortcuts, emergency numbers, SOS numbers, health info, battery visibility, and backup/restore

## Platform behavior

- `Phone` opens the in-app contact/dial flow
- `Emergency` uses the emergency configuration and safe dial fallbacks
- `SOS` can send SMS and attempt a direct call to the primary SOS number when Android permissions are granted; otherwise the app must degrade safely
- flashlight and camera remain optional capability paths

## Reliability rules

- launcher flows should never dead-end because of missing hardware, permissions, or apps
- onboarding and home must remain reachable on smaller displays
- if a referenced app disappears, EasyUI must fail safely and stay usable
- if premium or billing is unavailable, the app must stay in free mode without crashing

## Product boundaries

- consumer launcher only
- no device-owner or kiosk claims
- no remote caregiver control
- no backend dependency
