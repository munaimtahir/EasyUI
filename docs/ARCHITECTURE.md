# Architecture — EasyUI

This document describes the architectural foundation of the **EasyUI Senior & Caregiver Product Suite**. EasyUI is built on top of the frozen **Core Launcher** baseline, which is a separate repository. EasyUI is an intentional product derivative of Core. Therefore, Core's original product-variant prohibitions do not govern EasyUI, and caregiver monitoring capabilities are valid within-scope features of this project.

For the multi-module interaction architecture (Senior Launcher ↔ Backend ↔ Caregiver Companion), see [docs/ARCHITECTURE_PRODUCT.md](file:///home/munaim/srv/apps/easyui/docs/ARCHITECTURE_PRODUCT.md).

## Architectural Goals

The architecture of EasyUI is designed around:
- **Resilience**: The launcher runs fully local-first. Settings and layout state reside in local storage, and the app remains functional offline.
- **Modularity**: Code is structured into distinct modules for the launcher (`senior-launcher`), companion (`caregiver-companion`), and backend API (`backend`).
- **Simplicity**: UI states flow from defined repositories and local stores. We avoid complex global state dependencies where possible.
- **Traceability**: All network telemetry respects user-controlled permission flags.

## Module Structure

### 1. Launcher Module (`senior-launcher`)
The launcher module inherits Core launcher concepts and extends them for senior accessibility:
- **Launcher Shell**: Android entry points, home/launcher intent configuration, and onboarding controller.
- **App Discovery & Launching**: Resolves installed package info via `PackageManager` and caches labels, icons, and categories.
- **Home Layout Grid**: Coordinates favorite tile arrangements (2x2, 3x3, 4x4) and built-in shortcuts (contacts, emergency, clock, battery).
- **Local Settings Storage**: Datastore-backed storage for app layout, themes, font sizes, and paired caregiver settings.
- **Caregiver Repository**: Manages SHA-256 salted PIN hash, login lockout limits, and active pairing token/permissions state.
- **Telemetry Worker (`StatusReportWorker`)**: A periodic `CoroutineWorker` scheduled with WorkManager that POSTs battery level/charging status and pulls config suggestions from the backend (when permitted).

### 2. Companion Module (`caregiver-companion`)
A standalone companion app:
- **Pairing Session Manager**: Persists linked senior device credentials and permissions.
- **Telemetry Views**: UI tabs displaying senior battery levels, check-ins, and active emergency alerts.
- **Config Stager**: Interactive builder for stagining reminder recommendations to push to the backend.

### 3. Backend Module (`backend`)
A Ktor/Netty service:
- **Routing & Endpoint Controllers**: Validates pairing tokens and coordinates telemetry routing.
- **Bearer Authentication Filter**: Standard bearer authenticator checking tokens against active paired device states.
- **In-Memory Cache Store**: Stores temporary pairing codes, active status updates, and config suggestions.

## State Management Rules

### 1. One Source of Truth
Each persistent state has exactly one source of truth:
- Local layouts and settings are stored in local DataStore.
- Caregiver status is cached on the backend, but local senior launcher values remain the ultimate truth.

### 2. Validate References
If a pinned app is uninstalled or a favorite contact is deleted, the senior launcher must fall back safely to empty slot states without crashing.

### 3. Error Isolation
Network failures during check-in or SOS posting must not freeze the launcher UI thread. Error states must be surfaced as non-blocking UI banners while allowing the user to interact with the launcher normally.
