# Architecture

## Overview

Single Android application, offline-first, no backend.

## Layers

1. UI layer
   - Jetpack Compose screens
   - app navigation
   - accessibility-focused components
   - premium/paywall scaffolding, not yet wired into the current build
2. Domain layer
   - home layout rules
   - app visibility filtering
   - caregiver lock-state rules
   - contact shortcut behavior
   - backup and restore flows
3. Data layer
   - Room database for structured records
   - DataStore for preferences
   - PackageManager integration for installed apps
   - local media references for contact photos
4. Platform integration layer
   - launcher and home intent integration
   - flashlight action
   - camera action
   - direct dial intent
   - package inventory
   - battery and device-status probes
   - billing wrapper scaffold for a later premium release

## Repository scaffold

- `app`
- `feature/home`
- `feature/apps`
- `feature/contacts`
- `feature/caregiver`
- `feature/onboarding`
- `feature/premium`
- `core/ui`
- `core/domain`
- `core/data`
- `core/platform`
- `core/testing`

## Navigation map

- onboarding
- home
- app list scaffold
- contact detail or action
- caregiver settings
- edit layout
- hidden apps
- premium unlock
- backup and restore
- about and help

## Key runtime flows

### First install

intro -> set default launcher guidance -> optional permission explanation -> starter layout -> handoff complete

### Daily senior use

open phone -> see large main actions -> tap app or photo contact -> optionally return home

- `Phone`, `Messages`, `Contacts`, `Photos`, `Camera`, and `Emergency` stay obvious on home
- the first home page uses a fixed 2x3 layout with equal tiles, a large clock/date header card, and no visible settings or app-list entry
- caregiver settings are not visible on home
- caregiver entry uses a deliberate hidden gesture on the top status bar, with a clock-tap fallback
- the app list screen still exists for secondary flows, but it is not exposed on the main senior home page
- home pages are fixed and caregiver-managed, not draggable

### Caregiver edit flow

open caregiver area -> enter PIN -> land on caregiver dashboard -> open a focused section -> save or relock -> return home

- caregiver session starts from hidden home entry
- if caregiver PIN protection is enabled, PIN is required before caregiver settings open
- the first caregiver screen is a dashboard with a compact header, four section cards, setup status, and grouped control cards
- caregiver manages `Home Apps` separately from the senior-facing home surface
- caregiver assigns home apps to fixed page and slot positions

## Crash sensitivity

Launcher apps sit on a critical path. Default to graceful fallback:

- if package icon is unavailable, show a safe fallback icon
- if torch is unavailable, hide or disable the action
- if dial action is unavailable, show explanation instead of crashing
- if premium state is unavailable, default to free mode

## Privacy and security

- no cloud account
- no remote control
- no unnecessary network permissions
- local-only storage
- explicit user-controlled backup and export
