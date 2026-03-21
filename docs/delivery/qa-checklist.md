# QA Checklist

## Product truth

- [ ] Store listing does not claim full device lockdown
- [ ] Copy does not imply enterprise kiosk behavior
- [ ] Caregiver-focused language is clear

## Accessibility

- [ ] Large text remains legible
- [ ] Touch targets are comfortably large
- [ ] Color contrast is sufficient
- [ ] Navigation is predictable
- [ ] Essential actions are visually prominent

## Launcher behavior

- [ ] App can be set as default launcher
- [ ] Default launcher guidance is clear
- [ ] Caregiver Help can complete onboarding on smaller-height screens
- [ ] Home screen loads reliably after reboot
- [ ] Missing app targets do not break home screen

## Senior daily use

- [ ] Home is uncluttered
- [ ] Main actions are obvious
- [ ] Returning home is simple
- [ ] No accidental edit path is exposed in locked mode

## Caregiver flow

- [ ] Edit mode requires PIN when enabled
- [ ] Hidden apps stay hidden
- [ ] Layout lock prevents accidental movement
- [ ] Restore and reset behavior is understandable
- [ ] Emergency and SOS settings are understandable

## Premium

- [ ] Current build does not advertise unavailable premium purchase flows
- [ ] Billing scaffolding does not leak broken UI into release paths

## Reliability

- [ ] Declared permissions are justified in store copy and reviewer notes
- [ ] No network dependency for core use
- [ ] No obvious OEM-specific failure in the core flow
- [ ] Crash-free smoke test passed on multiple devices
