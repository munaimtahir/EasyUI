# Play Store Submission Pack

## Product basis

These materials are for `EasyUI Senior Launcher`, the app contained in this repository.

## App title suggestion

EasyUI Senior Launcher

## Short description

Simple Android home screen with large buttons and caregiver setup tools.

## Full description

EasyUI Senior Launcher is a simpler Android home screen built for seniors and the people who help set up their phones.

It replaces a cluttered launcher with large tiles, clear labels, and a steadier layout that is easier to understand day to day. Caregivers can set up a starter layout, keep the home screen simple, and reduce accidental changes without claiming full Android lockdown.

EasyUI focuses on practical everyday use:

- Large home tiles for the apps and actions that matter most
- App list screen with search is present, but the senior-facing entry point is still being wired
- Optional favorite contact tiles that open the dialer
- Optional flashlight tile when the device supports it
- Caregiver PIN and layout lock options
- Hidden-app filtering inside EasyUI
- Offline-first local storage with no account required

EasyUI does not depend on a cloud account, does not include ads, and does not promise enterprise device control or full system lockdown. It is designed to make the daily home-screen experience calmer, clearer, and easier to maintain.

## Key feature bullets

- Large, readable home screen tiles
- App list screen and search scaffold
- Caregiver setup and layout protection
- Favorite contact tiles with photo support
- Optional flashlight shortcut
- Local-only setup with no account required

## Reviewer notes draft

- No login or account is required.
- Reviewer can launch the app directly and complete onboarding without credentials.
- The app is a consumer launcher, not kiosk or device-owner software.
- Favorite contact tiles open the dialer with `ACTION_DIAL`; they do not place calls automatically.
- The flashlight tile is optional and only appears functional on devices with flash hardware.

## Suggested store tags and category

- Category: Personalization
- Tags: launcher, seniors, accessibility, simple phone, caregiver

## Content rating recommendation

- Audience: General audience
- App type: Utility / launcher
- No gambling, user-generated social content, or mature themes were found in the repo.

## Data Safety draft

Based on the repository contents:

- Data collected: none observed leaving the device
- Data shared: none observed
- Local processing only:
  - home layout
  - caregiver protection settings
  - hidden-app preferences
  - favorite contact labels, phone numbers, and selected photo URI references
- Analytics: none observed
- Crash reporting: none observed
- Ads: none observed

Final Play Console answers must be completed by the release owner, but the repo currently supports a low-data/no-sharing posture.
