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
  - emergency settings
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
- the visible essentials are `Phone`, `Messages`, `Contacts`, `Photos`, `Camera`, and `Emergency`
- the first home page uses a fixed 2x3 layout with equal tiles, a large clock/date header card, and no visible settings or app-list entry
- caregiver access is hidden behind a deliberate top-bar long-press, with a clock-tap fallback
- caregiver settings are not visible on home
- the app list screen still exists for secondary flows, but it is not exposed on the main senior home page
- home-app slots are caregiver-managed and bounded; the senior surface is not freeform or draggable

### Caregiver flow

open caregiver area -> enter PIN -> land on caregiver dashboard -> open a focused section -> save or relock -> return home

- caregiver session starts from the hidden home entry
- if caregiver PIN protection is enabled, PIN is required before caregiver settings open
- the first caregiver screen is a dashboard with grouped control cards and focused sections
- caregiver manages `Home Apps` separately from the senior-facing home surface
- caregiver assigns home apps to fixed page and slot positions

## Platform behavior

- `Phone` opens the in-app contact and dial flow
- `Messages` and `Photos` resolve through safe installed-app matching and fail with a clear message if unavailable
- `Emergency` uses the emergency configuration and safe dial fallbacks
- camera remains an optional capability path

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
