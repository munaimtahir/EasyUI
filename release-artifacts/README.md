# Release Artifacts

Current signed bundle:

- `easyui-senior-launcher-v1.0.0-release-signed.aab`
- SHA-256 checksum file:
  - `easyui-senior-launcher-v1.0.0-release-signed.aab.sha256`
- local signature verification output:
  - `jarsigner-verify.txt`

Signing setup:

- release signing values are now read from ignored local `keystore.properties`, environment variables, or Gradle project properties
- use `keystore.properties.example` as the template for another machine or CI runner

Build status:

- release signing, code shrinking, and resource shrinking are all working on the current build
- the current signed AAB was rebuilt after upgrading the Android Gradle Plugin from `8.5.2` to `8.6.1`, which cleared the local `R8` crash
