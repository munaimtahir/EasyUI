# Test Strategy

Launcher software sits on a critical path. Favor reliability and clear behavior over feature breadth.

## Unit tests

Cover:

- home tile ordering and validation
- hidden app filtering
- caregiver lock rules
- PIN verification
- backup payload validation
- premium feature gating
- theme and settings persistence rules

## Integration tests

Cover:

- reading installed apps into visible list
- restoring home layout from backup
- import rejection on malformed payload
- premium fallback when billing is unavailable

## UI tests

Cover:

- onboarding flow
- home rendering with large tiles
- app list search and alphabetical display
- caregiver PIN gate
- hide and unhide app flow
- add and edit photo contact flow
- layout lock behavior
- premium upgrade screen state

## Manual device tests

1. Set app as default launcher.
2. Reboot device and verify launcher continuity.
3. Install and uninstall apps; confirm app list refresh.
4. Lock layout and verify accidental move prevention.
5. Use the senior daily flow without caregiver steps.
6. Backup configuration and restore it.
7. Test on a device with unsupported torch.
8. Test without optional permissions granted.
9. Confirm readability at different font scales.
10. Confirm safe behavior when a target app is removed.
