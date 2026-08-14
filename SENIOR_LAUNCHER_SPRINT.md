# MASTER IMPLEMENTATION SPRINT — SENIOR LAUNCHER + CAREGIVER ECOSYSTEM

## ROLE

You are the principal Android engineer, product engineer, QA lead, accessibility engineer, security engineer, and release engineer responsible for taking the completed **Core Launcher v0.1 GO baseline** and turning it into a production-ready senior-friendly launcher ecosystem.

Work continuously through all phases described below.

Do not stop after planning, scaffolding, partial implementation, individual phases, compilation, or first successful launch.

The assignment ends only when the complete defined product has been implemented, tested, documented, and given a final evidence-backed GO / CONDITIONAL GO / NO-GO verdict.

---

# 0. CURRENT AUTHORITATIVE BASELINE

The starting point is:

**Core Launcher v0.1 — FINAL VERDICT: GO**

Verified baseline capabilities include:

* Android HOME/launcher role
* stable launcher runtime
* Gradle 8.7 wrapper
* clean debug build
* unit tests
* lint
* instrumentation tests
* emulator runtime verification
* 2×2, 3×3 and 4×4 home grids
* persistent grid configuration
* installed-app discovery
* app icons and labels
* app drawer
* search
* grid/list drawer modes
* favorites
* application launching
* HOME-key return
* theme/appearance persistence
* Quick Access
* notifications
* Phone/SMS/contact functionality
* favorite contact infrastructure
* Clock widget
* Date widget
* Note widget
* flashlight
* reset workflow
* current verification documentation

Known non-blocking baseline items:

* legacy Wi-Fi API deprecation warnings
* one runtime target used in final baseline verification
* GitHub Actions not yet remotely executed

Do not reopen completed Core functionality merely to redesign it.

Repair Core-derived functionality only where required for this product.

---

# 1. PRODUCT ARCHITECTURE

Build three clearly separated layers.

## A. Senior Launcher

Installed on the senior user's Android phone.

Must remain useful when completely offline.

Responsible for:

* launcher experience
* simplified senior UX
* apps
* contacts
* calling/messaging entry
* emergency/SOS surface
* reminders
* essential device/status information
* accessibility
* local caregiver configuration
* local safety settings

## B. Caregiver Companion

Separate Android application installed on the caregiver's phone.

Responsible for:

* pairing
* linked senior overview
* permitted remote configuration
* alerts
* check-ins
* reminders
* device status
* caregiver notifications
* privacy/permission visibility

## C. Secure Remote Service

Used only for features requiring communication between Senior Launcher and Caregiver Companion.

The Senior Launcher must not depend on the backend for its basic launcher functionality.

The service must not become a general surveillance platform.

---

# 2. REPOSITORY / PRODUCT SEPARATION

## Core

Treat the completed Core baseline as a frozen foundation.

Before product work:

1. inspect git status;
2. ensure the final baseline changes are committed;
3. run the remote CI workflow if credentials/access permit;
4. tag the baseline appropriately, e.g. `core-v0.1`;
5. record the baseline commit SHA.

Do not continue product-specific development inside the Core foundation repository.

## Product projects

Create appropriately separated product repositories/modules.

Recommended conceptual identities:

* `senior-launcher`
* `caregiver-companion`
* backend/service project if required

Final public branding may remain configurable until branding assets are supplied.

Do not let missing branding block engineering.

Use temporary neutral branding where necessary and record it as a deferred cosmetic item.

---

# 3. NON-NEGOTIABLE PRODUCT PRINCIPLES

## Senior-first

Every major UX decision must prioritize:

* readability
* predictability
* simplicity
* large touch targets
* low cognitive load
* clear wording
* minimal hidden behavior
* minimal gestures
* recoverability
* avoidance of accidental configuration changes

## Offline-first launcher

Loss of internet must never prevent:

* HOME loading
* app launching
* contacts access already available locally
* emergency UI
* flashlight
* Clock/Date
* local Note
* local configuration
* locally stored reminders where technically appropriate

## Consent-first caregiver access

Remote caregiver functionality must be:

* explicitly paired;
* visible to the senior;
* revocable;
* permission-scoped;
* auditable where appropriate.

Do not create covert monitoring behavior.

## Data minimization

Collect and transmit only data necessary for a defined caregiver feature.

Do not transmit:

* arbitrary app contents;
* SMS contents;
* call recordings;
* microphone audio;
* photographs;
* unrelated files;
* browser history;
* keyboard input;
* unrestricted device telemetry.

## Android platform truth

Do not claim or implement fake guarantees that the launcher can completely block:

* Android Settings;
* notification shade;
* system UI;
* OEM interfaces;
* hardware recovery paths;
* all other installed apps

on ordinary unmanaged Android devices.

Any restriction implemented through launcher UX must be described accurately as launcher-level protection.

---

# 4. EXECUTION RULES

This is a **single continuous implementation sprint**.

Work phase-by-phase, but do not stop between phases.

For every phase:

1. inspect the current implementation;
2. create a small implementation plan;
3. implement;
4. add/update tests;
5. build;
6. run relevant unit tests;
7. run lint/static checks;
8. perform emulator/device verification where relevant;
9. fix failures;
10. rerun the gate;
11. proceed automatically when the gate passes.

### Mandatory rule

If a quality gate fails:

**FIX → RETEST → CONTINUE**

Do not simply document a fixable failure and stop.

---

# 5. USER-INPUT DEFERRAL RULE

The user may be unavailable during this sprint.

If an item genuinely requires user preference/input, such as:

* final public name;
* logo;
* exact accent color;
* legal/company address;
* Play Store copy;
* production server domain;
* production API credentials;
* Firebase/Google account ownership;
* final emergency wording;
* commercial pricing;
* policy/legal approval;

then:

1. mark it `DEFERRED_USER_INPUT`;
2. use a safe placeholder or development value where possible;
3. isolate the dependency;
4. continue all unrelated work.

A deferred item must **never block another technically independent phase**.

Do not ask the user questions during the implementation unless continuing would be technically impossible and there is no safe placeholder or deferral path.

---

# PHASE A — FOUNDATION FREEZE AND PRODUCT BOOTSTRAP

## Objectives

* preserve Core v0.1 permanently;
* establish product repositories;
* ensure package/application isolation;
* migrate only the intended Core foundation;
* remove development-only identity.

## Tasks

* record Core baseline commit SHA;
* tag Core baseline;
* verify Core build one final time;
* create Senior Launcher product project;
* establish new application ID;
* establish versioning;
* create Caregiver Companion project;
* create remote-service project if required;
* configure CI for each repository;
* create product-specific README;
* create architecture docs;
* create `copilot_session.md`;
* create testing plan;
* create privacy/data-flow document;
* create threat model.

## Gate A

PASS only if:

* repositories/projects build;
* package IDs do not conflict;
* Core remains untouched/frozen;
* CI configurations exist;
* documentation accurately identifies project boundaries.
