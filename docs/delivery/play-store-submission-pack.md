# Play Store Submission Pack

## Product basis

These materials are for `EasyUI Senior Launcher`, the app contained in this repository.

## App title suggestion

EasyUI Senior Launcher

## Short description

Simple senior-friendly Android launcher with large tiles and caregiver setup tools.

## Full description

EasyUI Senior Launcher is a simpler Android home screen built for seniors and the people who help set up their phones.

It replaces a cluttered launcher with large tiles, clear labels, and a steadier layout that is easier to understand every day. Caregivers can set up the home screen, reduce accidental changes, and keep important actions easy to find without claiming full Android lockdown.

EasyUI currently includes:

- A fixed large-tile home screen for everyday essentials
- Clear onboarding with default-launcher guidance
- Caregiver PIN and layout lock options
- Hidden-app filtering inside EasyUI
- Favorite contacts and emergency-focused actions
- Health Info and SOS support
- Local backup export and import
- Offline-first setup with no account required

EasyUI does not require a cloud account, does not include ads, and does not promise kiosk mode, enterprise management, or full system control. It is designed to make Android feel calmer, clearer, and easier to maintain for seniors and caregivers.

## Key feature bullets

- Large, readable home screen tiles
- Caregiver setup and layout protection
- Hidden apps, favorite contacts, and emergency tools
- Health Info and SOS support
- Local backup and restore
- Local-only setup with no account required

## Reviewer notes draft

- No login or account is required.
- Reviewer can launch the app directly and complete onboarding without credentials.
- The app is a consumer launcher, not kiosk or device-owner software.
- The app requests `SEND_SMS` and `CALL_PHONE` because the SOS flow can send messages and attempt a direct emergency call when permissions are granted.
- Favorite contact and phone flows are local launcher actions, not remote communication features.
- The flashlight tile is optional and only appears functional on devices with flash hardware.
- The app-list surface exists internally, but the current senior-facing home flow centers on the fixed essentials grid.

## Suggested store tags and category

- Category: Personalization
- Tags: launcher, seniors, accessibility, simple phone, caregiver

## Content rating recommendation

- Audience: General audience
- App type: Utility / launcher
- No gambling, user-generated social content, or mature themes were found in the repo.

## Data Safety draft

Based on the repository contents:

- Data collected by the developer backend: none observed
- Data shared: none observed
- Local processing only:
  - home layout
  - caregiver protection settings
  - hidden-app preferences
  - emergency and SOS numbers
  - health information entered by the caregiver
  - favorite contact labels, phone numbers, and selected photo URI references
- Analytics: none observed in the repo
- Crash reporting: none observed in the repo
- Ads: none observed in the repo

Final Play Console answers must be completed by the release owner, but the repo currently supports a low-data/no-sharing posture.
