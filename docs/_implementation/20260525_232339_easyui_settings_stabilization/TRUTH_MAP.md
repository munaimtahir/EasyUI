# EasyUI Settings Stabilization — Truth Map

Timestamp: 2026-05-25T23:23:39Z  
Scope: Onboarding, caregiver settings, launcher settings storage, home rendering, caregiver access, layout, theme, font/readability, contacts/emergency shortcuts.

## Settings/Feature Map

| Setting / Feature | Written By | Stored In | Read By | Rendered In | Bug Risk | Fix Needed |
| --- | --- | --- | --- | --- | --- | --- |
| selectedTheme (SkinConfig.visualTheme) | GuidedSetupViewModel.updateVisualTheme; CaregiverViewModel.updateSkinVisualTheme | DataStore `skin_visual_theme` via `LauncherSettingsDataStore.setSkinConfig` | AppViewModel -> EasyUiNavGraph (EasyUiTheme) | Global MaterialTheme colors | **High**: theme changes revert due to racing separate writers (theme vs accessibility) | **Yes**: consolidate theme+accessibility updates |
| accessibilityMode (SkinConfig.accessibilityMode) | GuidedSetupViewModel.updateAccessibilityMode; CaregiverViewModel.updateSkinAccessibilityMode | DataStore `skin_accessibility_mode` | AppViewModel -> EasyUiNavGraph | Global MaterialTheme colors (High Contrast) | **High**: same race can overwrite theme | **Yes** |
| fontScale/fontSize (HomeReadabilityPreset + SkinConfig.readabilityPreset) | GuidedSetupViewModel.updateReadabilityPreset; CaregiverViewModel.updateHomeReadabilityPreset | DataStore `home_readability_preset` and SkinConfig | HomeViewModel -> HomeScreen (via SkinConfig) | SeniorHomeTokens (time/date/labels/icon sizes) | **High**: caregiver lacks control; onboarding may not impact home if pages rendering is wrong | **Yes**: add caregiver control + ensure home uses correct tiles/pages |
| layoutMode/gridMode (SkinConfig.layoutMode) | GuidedSetupViewModel.updateReadabilityPreset; CaregiverViewModel.updateSkinLayoutMode | DataStore `skin_layout_mode` | AppViewModel, Caregiver screens | No meaningful runtime effect in Home UI | **High**: caregiver exposes unsupported options | **Yes**: remove/disable unsupported layout modes |
| home pages / tile slots | CaregiverViewModel.updateHomePageCount; GuidedSetup (HomeLayoutSetupScreen via CaregiverViewModel) | DataStore `home_page_count` + Room `home_tiles` | HomeViewModel | HomeScreen pager | **Critical**: HomeScreen uses only primary tiles; extra pages not visible | **Yes**: render using page model |
| selected/allowed apps | CaregiverViewModel.assignAllowedApp/removeAllowedApp; GuidedSetup AllowedApps | Room `home_tiles` | HomeViewModel | HomeScreen grid | **Critical**: not visible due to page rendering bug | **Yes** |
| hidden apps | CaregiverViewModel.toggleAppHidden | DataStore `hidden_packages` | AppListViewModel (not yet mapped) | App list | **Medium**: not evaluated for home | **TBD** (verify app list usage) |
| layoutLocked | GuidedSetupViewModel.updateLayoutLocked; CaregiverViewModel.toggleLayoutLock | DataStore `layout_locked` | HomeViewModel, Caregiver screens | HomeScreen tiles (lock icon), caregiver toggles | **High**: per-tile lock icon visual clutter | **Yes**: replace with single indicator |
| caregiverPinHash / pinConfigured | GuidedSetupViewModel.savePin; CaregiverViewModel.submitPinSetup | DataStore `pin_salt_hex`, `pin_hash_hex` | CaregiverViewModel.requestProtectedRoute, RequireCaregiverSession | PinEntryScreen, caregiver gate | **Critical**: clock 5-tap flow blocks when no PIN due to session gate | **Yes**: allow create PIN flow without session |
| onboardingCompleted | GuidedSetupViewModel.completeSetup | DataStore `onboarding_complete`, `guided_setup_completed` | AppViewModel -> EasyUiNavGraph | Start destination (GuidedSetup vs Home) | **Low** | **No** |
| emergencyContact(s) | GuidedSetupViewModel.updateEmergencyPhoneNumber + updateEmergencyMode; CaregiverViewModel.updateEmergencyNumbers / updateEmergencyNumber | DataStore `emergency_phone_number`, `emergency_numbers`, `emergency_mode` | HomeViewModel, EmergencyCallScreen | HomeScreen emergency tile + EmergencyCallScreen | **High**: emergency/contact tiles not visible due to page rendering | **Yes** |
| directCallShortcut(s) / favorite contacts | CaregiverViewModel.saveContactTile/removeTile; GuidedSetup ContactsSetupScreen | Room `home_tiles` (type CONTACT) | HomeViewModel.renderPages | HomeScreen grid, PhoneContactsScreen | **Critical**: not visible due to page rendering | **Yes** |
| shortcut/contact list mode | GuidedSetupViewModel.updateEmergencyMode | DataStore `emergency_mode` + HomeLayoutRules.updateEmergencyAction | HomeViewModel | HomeScreen emergency tile behavior | **Medium**: tied to page rendering issue | **Yes** |

## Files Inspected (Phase 1)

- core/domain/model/LauncherSettings.kt
- core/data/datastore/LauncherSettingsDataStore.kt
- core/domain/repository/LauncherSettingsRepository.kt
- core/domain/rules/HomeLayoutRules.kt
- core/domain/rules/CaregiverProtectionRules.kt
- core/domain/rules/LayoutLockRules.kt
- core/domain/rules/ContactTileRules.kt
- app/app/AppViewModel.kt
- app/app/HomeViewModel.kt
- app/app/HomeUiState.kt
- app/app/GuidedSetupViewModel.kt
- app/app/caregiver/CaregiverViewModel.kt
- app/navigation/EasyUiNavGraph.kt
- feature/home/HomeScreen.kt
- feature/home/SeniorHomeTokens.kt
- feature/onboarding/GuidedSetupScreens.kt
- feature/onboarding/OnboardingScreens.kt
- feature/caregiver/CaregiverDashboard.kt
- feature/caregiver/CaregiverScreens.kt
- feature/caregiver/GuardianSettingsScreen.kt
