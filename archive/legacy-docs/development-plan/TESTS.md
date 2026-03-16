# Tests

## Test strategy
Launcher software sits on a critical path. Favor reliability and clear behavior over fancy edge features.

## Unit tests
Cover:
- home tile ordering and validation
- hidden app filtering
- caregiver lock rules
- PIN verification flow
- backup payload validation
- premium feature gating
- theme/settings persistence rules

## Integration tests
Cover:
- reading installed apps into visible list
- restoring home layout from backup
- import rejection on malformed payload
- premium state fallback when billing unavailable

## UI tests
Cover:
- onboarding flow
- home screen rendering with large tiles
- app list search and alphabetical display
- caregiver PIN gate
- hide/unhide app flow
- add/edit photo contact flow
- layout lock behavior
- premium upgrade screen state

## Manual device tests
1. Set app as default launcher
2. Reboot device and verify launcher continuity
3. Install and uninstall apps; confirm app list refresh
4. Lock layout and verify accidental move prevention
5. Try senior daily flow without caregiver steps
6. Backup configuration and restore it
7. Test on device with torch unsupported
8. Test on device without granted optional permissions
9. Confirm text readability at different system font scales
10. Confirm crash-free behavior when target app removed

## Acceptance tests
### MVP acceptance
- app can function as launcher
- user can see and launch core apps from a simple interface
- senior flow is visually clear
- no account or network required
- core actions work or fail gracefully

### v1 acceptance
- caregiver can PIN-lock editing
- caregiver can hide selected apps
- photo contact shortcuts work
- layout can be backed up and restored
- premium unlock reliably gates premium features
