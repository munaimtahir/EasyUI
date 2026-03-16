# Data Safety Final Draft

## Current build facts

- No app account or sign-in flow
- No backend or cloud sync
- No analytics SDK found
- No ad SDK found
- No crash-reporting SDK found
- No app-managed server transmission found
- App data is stored locally on the device

## Likely Play Console answer path

- Does the app collect or share any required user data types?
  - Draft answer: `No`
- Does data leave the device to the developer or third parties?
  - Draft answer: `No`
- Is all app functionality available without creating an account?
  - Draft answer: `Yes`
- Is data deletion handled on a server?
  - Draft answer: `No`

## Local-only data used by the app

- home tile layout and ordering
- caregiver settings
- caregiver PIN hash and salt
- hidden-app preferences
- favorite contact names, phone numbers, and selected photo URI references
- onboarding completion state
- installed app list derived from `PackageManager`

## Operator confirmations required

- confirm the production build does not add analytics, ads, crash reporting, or remote config
- confirm no other signing flavor or manifest overlay adds network data transfer
- confirm the submitted AAB matches the verified repo build
