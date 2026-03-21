# Release Checklist

## Pre-upload

- [ ] Confirm `versionCode` and `versionName` for the exact release build
- [ ] Configure the Play upload keystore locally or in CI before building the bundle
- [ ] Build a release App Bundle with `./gradlew :app:bundleRelease`
- [ ] Sign the release artifact with the production upload key
- [ ] Verify launcher cold start on a fresh install
- [ ] Verify default-launcher guidance flow on a real device
- [ ] Verify caregiver help CTA remains reachable on smaller-height devices
- [ ] Verify home screen survives reboot
- [ ] Verify app list refresh after install and uninstall
- [ ] Verify flashlight tile on a device with flash hardware
- [ ] Verify favorite contact tiles open the expected phone flow safely
- [ ] Verify caregiver PIN and layout lock flows
- [ ] Verify SOS behavior with and without `SEND_SMS` / `CALL_PHONE`
- [ ] Verify hidden apps only affect EasyUI, not Android system visibility
- [ ] Verify no placeholder branding or debug UI remains

## Play listing

- [ ] Upload final app icon in Play Console assets as needed
- [ ] Upload phone screenshots from the asset capture plan
- [ ] Upload feature graphic
- [ ] Add short description
- [ ] Add full description
- [ ] Add privacy policy URL
- [ ] Add reviewer notes

## Policy and Console

- [ ] Complete Data Safety form using the repo-backed draft
- [ ] Complete content rating questionnaire
- [ ] Select category and tags
- [ ] Configure Play App Signing
- [ ] Create internal testing release
- [ ] Validate install/update on internal track before broader rollout
