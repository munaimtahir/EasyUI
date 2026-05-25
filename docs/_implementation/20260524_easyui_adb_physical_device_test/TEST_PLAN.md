# Test Plan - ADB Physical Device Validation
1. **Repository Setup**: Verify environment and physical device connection (Done).
2. **Smoke Test**: Run `adb_smoke_test.sh` to check basic connectivity and app presence.
3. **Connected Tests**: Run `./gradlew connectedDebugAndroidTest` on the physical device.
4. **ADB Scripts**: Run `adb_collect_evidence.sh` if available and applicable.
5. **Documentation**: Capture failures, attempt fixes for test-related issues, and generate final report.
