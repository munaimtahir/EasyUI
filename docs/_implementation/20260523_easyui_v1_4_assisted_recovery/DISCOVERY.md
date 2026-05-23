# EasyUI V1.4 Assisted Recovery Discovery Report

## Current Architecture Summary
- **UI**: Jetpack Compose based. Screens in `feature` modules.
- **Navigation**: `EasyUiNavGraph` in `app` module using `Compose Navigation`.
- **State Management**: `HomeViewModel` uses `StateFlow` to combine data from various repositories.
- **Logic**: `GuardianRules` (domain layer) handles health check and setup completeness calculations.
- **Persistence**: `DataStore` used for settings and remote link data.

## Current Screen Map
- `HomeScreen`: Main senior interface.
- `PhoneContactsScreen`: Senior phone shortcuts.
- `SeniorMessagesScreen`, `SeniorPhotosScreen`, `SeniorCameraScreen`: Controlled entry points.
- `SafeHandoffScreen`: Transition UI for external apps.
- `CaregiverToolsScreen`: Caregiver dashboard and configuration.
- `LinkedDevicesScreen`: Monitoring remote phones.

## Existing Guardian Check Models
- `GuardianCheckType`: Enum (BATTERY_LOW, NO_INTERNET, etc.).
- `GuardianCheckResult`: Individual check result.
- `PhoneHealthState`: Aggregated state with `shouldPromptAlert`.

## Current Test Coverage
- Unit tests for rules (`GuardianRulesTest`, `SetupCompletenessTest`, `RemoteLinkRulesTest`).
- Build and assemble verified.

## Gaps to Fix for V1.4
- `PhoneHealthCard` and `SeniorAlertBanner` are purely informational; they don't provide direct recovery actions beyond "Alert Caregiver".
- No dedicated "Assisted Recovery" or "Troubleshooting" screens.
- Intents to open system settings (Wi-Fi, Default Apps) are not centrally managed.

## Risks
- **OEM Fragmentation**: Some settings screens (like Default Apps) vary by manufacturer.
- **Senior Confusion**: Providing too many options in recovery might overwhelm the user.
- **Boundary Violation**: Must ensure recovery flows don't attempt to "lock down" the system or use forbidden APIs.

## Next Steps
- Implement `RecoveryAction` models.
- Create `AssistedRecoveryScreen`.
- Update `HomeViewModel` to handle recovery navigation.
