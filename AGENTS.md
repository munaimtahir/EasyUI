# AGENTS.md

## Mission

Build EasyUI Senior Launcher as an offline-first Android launcher for seniors and caregivers. Every change must improve clarity, safety, stability, or delivery readiness without expanding into unsupported Android control claims.

## Source of truth

Read these documents before making substantial product or architecture changes:

1. `/docs/product/project-brief.md`
2. `/docs/product/mvp-scope.md`
3. `/docs/product/v1-scope.md`
4. `/docs/product/guardrails.md`
5. `/docs/engineering/architecture.md`
6. `/docs/engineering/data-model.md`
7. `/docs/engineering/api-interfaces.md`
8. `/docs/engineering/tasks.md`

If legacy documents differ from the canonical docs, prefer the canonical `docs/` tree and treat `archive/legacy-docs/` as historical input only.

## Product truth

- This is a consumer Android launcher, not kiosk software.
- Do not promise device-owner behavior, OS lockdown, or enterprise management.
- Do not add cloud dependency, remote caregiver control, subscriptions, or ad-driven flows in MVP or v1.
- Keep the daily senior experience simpler than stock Android, not more configurable than stock Android.

## Primary users

- Senior users who need larger targets, clearer labels, and less visual clutter
- Caregivers or adult children who set up the phone and want fewer support calls

## Non-negotiable product goals

- Large, readable, high-contrast UI
- Stable home layout with low accidental-change risk
- Offline-first behavior with no account requirement
- Clear first-run default-launcher guidance
- Caregiver-only protection features for premium scope

## Delivery phases

### Phase 0: Foundation

- Establish Android project structure
- Add launcher manifest support
- Set up navigation, Room, DataStore, and testing baseline

### Phase 1: MVP

- Onboarding and default launcher guidance
- Large-tile home screen
- Simple app list with search
- Emergency action and flashlight shortcuts
- Local persistence for layout and settings

### Phase 2: Caregiver Safety Pack

- Caregiver PIN
- Layout lock and disabled edit gestures in locked mode
- Hidden apps
- Photo contacts
- One-time premium unlock

### Phase 3: Stability and Restore

- Backup/export
- Import with validation
- Reset to defaults
- Edge-case hardening

## Architecture expectations

- `app` owns startup, DI, app-wide navigation, and launcher registration
- `feature/*` owns screens and feature-specific presentation logic
- `core/domain` owns business rules and should remain as platform-independent as practical
- `core/data` owns Room, DataStore, and repository implementations
- `core/platform` wraps Android APIs such as PackageManager, launcher integration, torch, dial intents, and billing
- `core/ui` owns shared design primitives and common composables
- `core/testing` owns fixtures, fakes, and shared test utilities

## Coding rules

- Use Kotlin and Compose
- Prefer explicit state models and small composables
- Keep business logic testable outside UI where practical
- Avoid OEM-specific hacks as product foundations
- Use defensive fallbacks for missing apps, unavailable torch, denied permissions, or billing failures
- Keep strings plain, caregiver-friendly, and honest

## UX guardrails

- One obvious primary action per screen or panel
- Large touch targets
- High contrast
- Clear labels
- Minimal clutter
- No hidden gestures for essential actions
- No feature-heavy widgets in MVP or v1

## Data and privacy rules

- No backend or account dependency in MVP/v1
- No unnecessary network permissions
- Persist user-owned configuration locally
- Treat installed app inventory as refreshable device state, not owned data
- Version backups and validate imports before applying

## Testing expectations

Each meaningful feature should include:

- unit tests for rules and repository behavior where feasible
- UI coverage for critical flows when feasible
- manual QA notes for launcher-specific or device-specific behavior

Always verify:

- default launcher flow
- home stability after reboot
- app list refresh on install/uninstall
- graceful handling when a referenced app disappears
- premium degradation to free mode if billing is unavailable

## Change management

- Keep changes small and scoped
- Update canonical docs when product, UX, or architecture changes
- Do not edit or resurrect archived legacy docs unless the user explicitly asks
- Record assumptions in docs instead of leaving them implicit in code

## When implementing

Start from `docs/engineering/tasks.md`. If the task affects scope, check `docs/product/guardrails.md` before proceeding. If implementation pressure conflicts with product truth, choose the smaller and more honest feature.
