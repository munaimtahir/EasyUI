# Copilot Instructions for EasyUI Senior Launcher

## Quick Reference

**Project:** Offline-first Android launcher for seniors and caregivers  
**Tech Stack:** Kotlin, Jetpack Compose, AndroidX Navigation, Room, DataStore  
**Architecture:** Multi-module (app, feature/*, core/*)  
**Min SDK:** 26 | **Target SDK:** 35  

## Build & Test Commands

### Building
```bash
./gradlew assembleDebug              # Build debug APK
./gradlew assembleRelease            # Build release APK (requires signing keys)
./gradlew --no-build-cache clean build  # Full clean build
```

### Testing
```bash
./gradlew testDebugUnitTest          # Run all unit tests
./gradlew testDebugUnitTest --tests "com.easyui.core.domain.rules.HomeLayoutRulesTest"  # Single test class
./gradlew :core:domain:testDebugUnitTest  # Test single module
./gradlew assembleDebugAndroidTest   # Build instrumentation tests (not run directly; requires device)
```

### Linting
```bash
./gradlew lint                       # Android lint check
```

### Single Module Build
```bash
./gradlew :feature:home:assembleDebug  # Build specific module
./gradlew :core:data:testDebugUnitTest  # Test specific module
```

## High-Level Architecture

### Module Structure

**`:app`** — Application shell, startup, DI wiring
- `MainActivity.kt` — Launcher host activity
- `EasyUiApplication.kt` — Application entry point
- `di/` — Dependency injection (manual wiring via ViewModelFactory)
- `navigation/` — App-level navigation graph
- `BootReceiver.kt` — Launcher activation on boot

**`:core:domain`** — Pure Kotlin business rules (no Android dependencies)
- `rules/` — HomeLayoutRules, AppCatalogRules, FallbackStateRules, VerySimpleModeRules
- `model/` — Domain entities (HomeTile, InstalledApp, HealthInfo, etc.)
- Tests are comprehensive here; target is unit-testable rules

**`:core:data`** — Local persistence and repositories
- `database/` — Room database, migrations, DAOs
- `datastore/` — DataStore settings (user preferences)
- `repository/` — Repository implementations (single-source-of-truth pattern)
- `backup/` — Backup serialization and validation

**`:core:platform`** — Android API wrappers (device-specific behavior)
- Torch (flashlight) control
- PackageManager enumeration and app launching
- Phone dialer and emergency intents
- Battery and device status queries

**`:core:ui`** — Shared design system and composables
- Colors, typography, spacing, tile styling
- Reusable Compose components (LargeTileButton, etc.)

**`:core:testing`** — Shared test utilities, fixtures, fakes

**`:feature:home`** — Senior home surface
- Large 2×3 tile grid with fixed essentials (Phone, Messages, Contacts, Photos, Camera, Emergency)
- Clock/date header card
- Health Info viewer

**`:feature:apps`** — App enumeration and search

**`:feature:caregiver`** — Caregiver dashboard, settings, PIN protection
- Home app assignment
- Hidden app management
- Backup/restore

**`:feature:onboarding`** — First-run experience
- Intro and default launcher guidance
- Caregiver help screen

### Data Flow Patterns

1. **Repository-driven data access:** `core:data` repositories are the single source of truth. Always fetch via repository, not DAOs directly.
2. **State hoisting in ViewModels:** ViewModels hold mutable state; Compose screens receive state as parameters.
3. **Offline-first:** All data is local (Room/DataStore). No backend or account dependency.
4. **Defensive fallbacks:** Missing apps, unavailable torch, denied permissions, and billing failures have explicit fallback states.

## Key Conventions

### Naming & Structure

- **Test class naming:** `[Feature]Test` or `[Feature][Aspect]Test` (e.g., `HomeLayoutRulesTest`, `SetupCompletenessTest`)
- **Module-level entities:** Keep `HomeTile`, `InstalledApp`, `HealthInfo` in `core:domain:model`
- **Feature-scoped ViewModels:** Place in `feature:[name]:presentation` or `feature:[name]:ui`
- **Room migrations:** Always version with `version = N` and provide explicit migration logic; avoid destructive migrations

### Compose Patterns

- **Small, stateless composables:** Prefer composables that receive state as parameters over @Composable functions with internal state
- **Hoisted state:** ViewModels or screen-level functions manage state; pass callbacks to children
- **Tile system:** Reuse `LargeTileButton` (or similar) from `core:ui` for consistent sizing and styling

### Android-Specific

- **Permissions:** Check `AndroidManifest.xml` for declared permissions and `<queries>` block (required for API 30+)
- **Default launcher:** Handled via intent filter in manifest and `CATEGORY_HOME` routing
- **Boot receiver:** `BootReceiver.kt` re-activates launcher after reboot

### Testing Conventions

- **Domain tests:** Keep logic unit-testable in `core:domain:rules`. Use JUnit assertions.
- **Instrumentation tests:** Rare; only for PackageManager or launcher-specific behavior
- **No mocking of Android components unless unavoidable:** Prefer testing rules in pure Kotlin

## Product & Guardrails

**Canonical truth:** Always refer to `/docs/engineering/tasks.md` for the current roadmap and `/docs/product/guardrails.md` for scope boundaries.

**Non-negotiable principles:**
- **Not a kiosk app:** No OS-level lockdown or device-owner promises
- **No backend:** All data is local; no cloud sync or account requirement
- **Offline-first:** Works without network connectivity
- **Honest UX:** String copy must truthfully reflect launcher limitations
- **Graceful degradation:** Premium features degrade to free mode if billing unavailable

**UX guardrails:**
- Large touch targets and high contrast for senior usability
- One obvious primary action per screen
- Minimal clutter; no hidden gestures for essential actions
- Fixed home layout with low accidental-change risk

## Key Files & Locations

| File | Purpose |
|------|---------|
| `docs/engineering/architecture.md` | Module responsibilities and runtime model |
| `docs/engineering/data-model.md` | Entity definitions (HomeTile, InstalledApp, etc.) |
| `docs/engineering/tasks.md` | Current implementation roadmap |
| `docs/product/guardrails.md` | Product scope and non-negotiable rules |
| `AGENTS.md` | Mission statement and change management guidance |
| `app/src/main/AndroidManifest.xml` | Launcher registration and intent filters |
| `core/domain/src/test/java/` | Comprehensive unit tests for rules |

## Release & Signing

Release builds require signing keys in one of:
1. Local `release_keys/keystore.properties`
2. Gradle properties: `EASYUI_KEYSTORE_PATH`, `EASYUI_KEYSTORE_PASSWORD`, `EASYUI_KEY_ALIAS`, `EASYUI_KEY_PASSWORD`
3. Environment variables (CI): same keys as above with `EASYUI_` prefix

See `app/build.gradle.kts` for details. Never commit actual keys to version control.

## Common Tasks

### Adding a New Domain Rule
1. Create rule function in `core:domain:rules:` (e.g., `fun myRule(input: X): Y`)
2. Write unit tests in `core:domain:src/test/` (target 100% coverage for rules)
3. Use in repository or ViewModel

### Adding a New Composable
1. Create in `core:ui` if reusable across features
2. Use `LargeTileButton` or shared components for consistency
3. Hoist state to caller; pass callbacks as parameters

### Modifying Home Tiles
1. Update `HomeTile` entity in `core:domain:model`
2. Update Room migration if schema changes
3. Update `HomeLayoutRules` in `core:domain:rules`
4. Add tests in `core:domain:src/test`
5. Update corresponding feature presentation layer

### Updating Caregiver Protection
1. Check PIN verification in `feature:caregiver`
2. Ensure state is gated by `isPinProtected` flag
3. Update DataStore if settings change

## Performance & Memory Considerations

- **Compose recomposition:** Keep state minimal; hoist only necessary state to ViewModels
- **Room queries:** Use Flow for reactive queries; avoid blocking queries on main thread
- **Installed app enumeration:** Cache with PackageManager broadcast receiver; refresh on package change
- **Bitmap handling:** Reuse contact photo URIs; fall back to initials if unavailable

## Before Opening a Pull Request

1. Run `./gradlew testDebugUnitTest` and verify all tests pass
2. Run `./gradlew lint` and address any issues
3. Check that `docs/engineering/tasks.md` is updated if scope changed
4. Verify manual QA flows listed in `README.md` still work (if applicable)
5. Test on both emulator and physical device if launcher behavior is affected
6. Do not commit signing keys or secrets

## Troubleshooting

**Gradle sync failures:**
- Ensure JDK 17 is installed: `java -version` should show 17.x.x
- Clear Gradle cache: `./gradlew clean --no-build-cache`
- Refresh IDE: Android Studio > File > Invalidate Caches

**Test failures:**
- Check that Room migrations are in place: `core/data/src/main/java/com/easyui/core/data/database/migrations/`
- Verify FakeLauncherActivity is stubbed for instrumentation tests
- Ensure test fixtures in `core:testing` are up-to-date with current schema

**Build failures:**
- Check `lint.xml` for ignored lint issues (e.g., ObsoleteSdkInt for launcher icons)
- Verify `AndroidManifest.xml` intent filters are correct for launcher registration
- Ensure `queries` block is present for API 30+ permission scoping
