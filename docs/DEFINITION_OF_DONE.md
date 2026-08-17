# Definition of Done

## General rule

Work is complete only when it is implemented, verified, documented, and handed off.

## Current baseline audit status — 2026-08-17

The current status report is authoritative for the planning and release decision: `docs/VERIFICATION/core-current-status-2026-08-17.md`. The historical 2026-08-09 report must not override later scope changes. A v0.1 GO additionally requires one authoritative launcher-only scope with no caregiver/product-variant/backend additions in the baseline build.

## Required checklist

For every task:

- [ ] Scope confirmed
- [ ] No out-of-scope feature added
- [ ] No legacy code copied
- [ ] No legacy architecture reused
- [ ] Implementation completed
- [ ] Relevant tests added or updated
- [ ] Build run
- [ ] Unit tests run
- [ ] Lint run where applicable
- [ ] Emulator/manual verification run where applicable
- [ ] Documentation updated
- [ ] `copilot_session.md` updated
- [ ] Final verdict written

## Verdict options

### GO

Task is complete and verified.

### Conditional GO

Task is mostly complete, but a documented non-blocking issue remains.

### NO-GO

Task is incomplete, unstable, unsafe, or unverified.

## Baseline Done

`core v0.1` is done only when:

- app can function as a launcher
- home screen is stable
- installed apps display
- icons display
- apps launch
- selected home apps persist
- theme persists
- reset works
- build passes
- unit tests pass
- lint passes
- emulator runtime passes
- documentation is current
- final baseline report exists

## Forbidden completion behavior

Do not:

- declare success without verification
- skip tests without documenting why
- delete failing tests to pass CI
- add unrelated features during a fix
- leave the handoff file stale
- hide unresolved issues
