# Test Results

All commands successfully passed.

```bash
./gradlew clean :app:assembleDebug :app:testDebugUnitTest
```
- App module and submodules successfully compiled.
- Debug build successfully assembled.
- Unit tests all passed successfully.

```bash
./gradlew lint
```
- Linting ran and successfully completed with actionable issues suppressed or resolved.

### Connected Device Testing

Connected device testing must be performed locally since there is no physical device tethered here.
You can run this manually via:
```bash
./gradlew connectedDebugAndroidTest --stacktrace
```
Or execute the local ADB smoke test script generated in `scripts/device_dry_run.sh`

```bash
bash scripts/device_dry_run.sh 08357252AE006901
```
