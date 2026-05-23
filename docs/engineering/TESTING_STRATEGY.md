# EasyUI Testing Strategy

**Version:** 1.0  
**Date:** 2026-04-07  
**Status:** Implementation In Progress

---

## Executive Summary

This document defines the comprehensive testing strategy for EasyUI, an offline-first Android launcher for seniors. The strategy covers unit tests, integration tests, UI tests, instrumentation tests, and end-to-end tests across all application layers.

**Current State:**
- ✅ Domain layer: 14 rule test files (comprehensive)
- ✅ Security layer: PIN hasher tests
- ❌ App module: NO TESTS
- ❌ Feature modules: NO TESTS  
- ❌ Data layer: NO REPOSITORY TESTS
- ❌ UI layer: NO COMPOSE TESTS
- ❌ Integration: NO TESTS
- ❌ Instrumentation: NO TESTS

**Target Coverage:**
- Unit tests: 80%+ for business logic
- Integration tests: Critical paths covered
- UI tests: All major user flows
- E2E tests: 10+ critical journeys

---

## Testing Pyramid

```
           ╱╲
          ╱E2E╲          (10 tests) - Critical user journeys
         ╱──────╲
        ╱  UI    ╲       (50 tests) - Screen interactions
       ╱──────────╲
      ╱Integration╲      (30 tests) - Feature-to-feature
     ╱──────────────╲
    ╱  Unit Tests    ╲   (200+ tests) - Business logic
   ╱──────────────────╲
```

---

## 1. Unit Testing Strategy

### 1.1 What to Test

**ViewModels** (Priority: CRITICAL)
- State management logic
- User action handling
- Validation logic
- Error handling
- State transformations

**Repository Implementations** (Priority: HIGH)
- Data persistence
- Data retrieval
- Error scenarios
- Cache behavior

**Use Cases / Business Rules** (Priority: HIGH)
- ✅ DONE: Domain rules (14 test files)
- App-specific business logic

**Utilities** (Priority: MEDIUM)
- ✅ DONE: PIN hasher
- Data transformations
- Validators

### 1.2 Testing Approach

**Tools:**
- JUnit 5 (or JUnit 4 if already configured)
- Kotlin Test
- MockK for mocking
- Turbine for Flow testing
- Coroutines Test

**Patterns:**
- Arrange-Act-Assert (AAA)
- Given-When-Then for behavior tests
- Test data builders for complex objects
- Fake implementations for repositories

**Naming Convention:**
```kotlin
fun `<method name> <scenario> should <expected outcome>()`
```

Example:
```kotlin
fun `savePin with empty PIN should allow progression`()
fun `savePin with mismatched PINs should show error`()
```

### 1.3 Coverage Targets

| Module | Target | Current | Priority |
|--------|--------|---------|----------|
| core/domain | 80% | ~80% ✅ | DONE |
| core/data | 70% | 0% ❌ | HIGH |
| core/platform | 60% | 0% ❌ | MEDIUM |
| app/viewmodels | 80% | 0% ❌ | CRITICAL |
| feature/* | 70% | 0% ❌ | HIGH |

---

## 2. Integration Testing Strategy

### 2.1 What to Test

**Repository + DataStore**
- Settings persistence
- Data migration
- Concurrent access

**ViewModel + Repository**
- Full state flow
- Side effects
- Error propagation

**Navigation + State**
- Route transitions
- State preservation
- Back stack handling

**Feature-to-Feature**
- Guided Setup → Home
- Caregiver Dashboard → Settings
- Home → App List

### 2.2 Testing Approach

**Tools:**
- Robolectric for Android components
- Fake implementations
- Test DataStore
- Test dispatchers

**Scope:**
- Focus on boundaries between layers
- Test happy path + 2-3 error scenarios
- Verify state synchronization

---

## 3. UI Testing Strategy (Compose)

### 3.1 What to Test

**Screen Rendering**
- All screens render without crash
- Correct initial state display
- Loading states
- Error states

**User Interactions**
- Button clicks
- Text input
- Scrolling
- Navigation

**State Updates**
- UI reflects ViewModel state
- Validation feedback
- Dynamic content updates

### 3.2 Testing Approach

**Tools:**
- Compose Testing framework
- ComposeTestRule
- Semantics matchers
- Screenshot testing (optional)

**Pattern:**
```kotlin
@Test
fun welcomeScreen_displaysCorrectContent() {
    composeTestRule.setContent {
        WelcomeScreen(onStartSetup = {})
    }
    
    composeTestRule.onNodeWithText("Welcome to EasyUI").assertExists()
    composeTestRule.onNodeWithText("Start Setup").assertExists()
}
```

### 3.3 Coverage Targets

**Must Test:**
- All Guided Setup screens (10)
- Home screen variants
- Caregiver dashboard
- App list
- Settings screens

**Nice to Have:**
- Edge cases
- Accessibility
- Different screen sizes

---

## 4. Instrumentation Testing Strategy

### 4.1 What to Test

**Device-Specific Behavior**
- Launcher activation
- PackageManager integration
- Permission handling
- System intents (dial, flashlight)

**Hardware Integration**
- Torch availability
- Battery info
- Phone capabilities

**DataStore on Real Device**
- Persistence across restarts
- Process death recovery
- File system operations

### 4.2 Testing Approach

**Tools:**
- AndroidX Test
- Espresso (if needed)
- UiAutomator for system interactions
- Test orchestrator

**Execution:**
- Run on emulator + physical device
- Test different Android versions (26-35)
- Test with/without features (torch, phone)

### 4.3 Critical Tests

1. Default launcher selection
2. App install/uninstall detection
3. Emergency dial intent
4. Torch on/off
5. Settings persistence after force-stop
6. Guided Setup completion persistence

---

## 5. End-to-End Testing Strategy

### 5.1 Critical User Journeys

**Journey 1: First-Time Setup**
1. Install app
2. Launch app
3. Complete Guided Setup (all 12 steps)
4. Verify home screen configured correctly
5. Restart app → no re-prompt

**Journey 2: Senior Daily Use**
1. Open app (home screen)
2. Tap phone contact
3. Verify dial intent
4. Tap emergency
5. Verify emergency behavior
6. Open flashlight
7. Verify torch on/off

**Journey 3: Caregiver Configuration**
1. Enter caregiver mode (PIN if set) - triggered via 5 rapid clock taps or status-bar long-press
2. Navigate to settings
3. Change readability preset
4. Add hidden app
5. Exit caregiver mode
6. Verify changes reflected on home

**Journey 4: Layout Modification**
1. Enter caregiver mode
2. Edit home layout
3. Reorder tiles
4. Add new contact
5. Save changes
6. Verify persistence after restart

**Journey 5: Protection Features**
1. Set caregiver PIN
2. Enable layout lock
3. Exit caregiver mode
4. Attempt edit → blocked
5. Re-enter with PIN
6. Verify edit allowed

### 5.2 Testing Approach

**Tools:**
- UI Automator for cross-app flows
- Playwright (e2e/ directory) for web-like flows
- Custom test harness

**Execution:**
- Automated where possible
- Manual checkli for complex flows
- Record video evidence

---

## 6. Test Organization

### 6.1 Directory Structure

```
app/
  src/
    test/                    # Unit tests
      java/com/easyui/launcher/
        app/
          HomeViewModelTest.kt
          GuidedSetupViewModelTest.kt
        navigation/
          EasyUiNavGraphTest.kt
    androidTest/             # Instrumentation tests
      java/com/easyui/launcher/
        integration/
          GuidedSetupFlowTest.kt
        device/
          LauncherActivationTest.kt

core/domain/
  src/test/                 # ✅ Already exists (14 tests)

core/data/
  src/test/                 # Unit tests (repositories)
    java/com/easyui/core/data/
      repository/
        LauncherSettingsRepositoryTest.kt

feature/onboarding/
  src/test/                 # Unit tests
  src/androidTest/          # UI tests
    java/com/easyui/feature/onboarding/
      GuidedSetupScreensTest.kt

e2e/                        # ✅ Already exists (Playwright)
  tests/
    guided-setup.spec.ts
```

### 6.2 Naming Conventions

**Test Classes:**
- `{ClassName}Test.kt` for unit tests
- `{FeatureName}IntegrationTest.kt` for integration
- `{ScreenName}UiTest.kt` for Compose UI tests
- `{Journey}E2ETest.kt` for end-to-end

**Test Methods:**
- Descriptive, behavior-focused names
- Use backticks for readability
- Include scenario and expected outcome

---

## 7. Test Data Management

### 7.1 Fixtures

Create test data builders:

```kotlin
object LauncherSettingsFixtures {
    fun default() = LauncherSettings(
        guidedSetupCompleted = false,
        guidedSetupStep = 1,
        // ...
    )
    
    fun withGuidedSetupComplete() = default().copy(
        guidedSetupCompleted = true,
        guidedSetupStep = 12
    )
}
```

### 7.2 Fakes

Implement fake repositories:

```kotlin
class FakeLauncherSettingsRepository : LauncherSettingsRepository {
    private val settings = MutableStateFlow(LauncherSettingsFixtures.default())
    
    override fun getSettings() = settings.asStateFlow()
    override suspend fun updateSettings(update: (LauncherSettings) -> LauncherSettings) {
        settings.value = update(settings.value)
    }
}
```

### 7.3 Test Utilities

Create in `core/testing/`:
- Test dispatchers
- Test DataStore
- Turbine extensions
- Common assertions

---

## 8. Continuous Integration

### 8.1 Pre-Merge Requirements

**Must Pass:**
- All unit tests
- All integration tests
- Critical UI tests
- Lint checks

**Nice to Have:**
- Full UI test suite
- Instrumentation tests on emulator
- E2E smoke tests

### 8.2 Nightly/Weekly

- Full instrumentation suite
- E2E tests on multiple devices
- Performance tests
- Coverage reports

---

## 9. Implementation Plan

### Phase 1: Foundation (Week 1)
- [x] Define testing strategy (this document)
- [ ] Set up app module test infrastructure
- [ ] Create test utilities and fixtures
- [ ] Configure dependencies

### Phase 2: Critical Unit Tests (Week 1-2)
- [ ] GuidedSetupViewModel tests
- [ ] HomeViewModel tests
- [ ] Navigation validation tests
- [ ] Repository tests

### Phase 3: UI Tests (Week 2-3)
- [ ] Guided Setup screen tests (all 10)
- [ ] Home screen tests
- [ ] Caregiver dashboard tests

### Phase 4: Integration (Week 3-4)
- [ ] Feature-to-feature integration
- [ ] Repository + ViewModel integration
- [ ] Navigation integration

### Phase 5: Instrumentation (Week 4-5)
- [ ] Launcher activation tests
- [ ] System integration tests
- [ ] Persistence tests

### Phase 6: E2E (Week 5-6)
- [ ] First-time setup journey
- [ ] Senior daily use journey
- [ ] Caregiver configuration journey
- [ ] Protection features journey

---

## 10. Success Metrics

**Quantitative:**
- Unit test coverage: 80%+
- All ViewModels tested: 100%
- UI tests: 50+ screens/components
- Integration tests: 30+ scenarios
- E2E tests: 10+ journeys
- Test execution time: <5 min for unit, <15 min for all

**Qualitative:**
- Tests catch regressions before merge
- Confident refactoring
- Clear test failures guide debugging
- Tests serve as documentation

---

## 11. Best Practices

1. **Test Behavior, Not Implementation**
   - Focus on observable outcomes
   - Don't test private methods directly
   - Use public APIs

2. **Keep Tests Isolated**
   - No shared mutable state
   - Independent test cases
   - Deterministic results

3. **Make Tests Readable**
   - Clear arrange-act-assert sections
   - Descriptive names
   - Minimal assertions per test

4. **Fast Feedback**
   - Unit tests run in <1s
   - Avoid Thread.sleep
   - Use test dispatchers

5. **Maintain Tests**
   - Update when code changes
   - Remove obsolete tests
   - Refactor test code like production code

---

## 12. Risk Mitigation

**Risk:** Low test coverage delays catching bugs  
**Mitigation:** Prioritize critical paths first (Guided Setup, Home, Caregiver)

**Risk:** Flaky tests reduce confidence  
**Mitigation:** Avoid timing dependencies, use deterministic test doubles

**Risk:** Slow test suite blocks development  
**Mitigation:** Optimize test execution, parallelize where possible

**Risk:** Tests become maintenance burden  
**Mitigation:** Follow best practices, refactor regularly, delete obsolete tests

---

## Appendix A: Test Coverage by Feature

| Feature | Unit | Integration | UI | E2E | Priority |
|---------|------|-------------|----|----|----------|
| Guided Setup | ❌ | ❌ | ❌ | ❌ | CRITICAL |
| Home Screen | ❌ | ❌ | ❌ | ❌ | CRITICAL |
| Caregiver Dashboard | ❌ | ❌ | ❌ | ❌ | HIGH |
| App List | ❌ | ❌ | ❌ | ❌ | HIGH |
| Settings | ❌ | ❌ | ❌ | ❌ | MEDIUM |
| Emergency Action | ❌ | ❌ | ❌ | ❌ | HIGH |
| Contact Tiles | ❌ | ❌ | ❌ | ❌ | MEDIUM |
| Readability Presets | ❌ | ❌ | ❌ | ❌ | MEDIUM |
| Hidden Apps | ❌ | ❌ | ❌ | ❌ | MEDIUM |
| Layout Lock | ❌ | ❌ | ❌ | ❌ | MEDIUM |
| Caregiver PIN | ❌ | ❌ | ❌ | ❌ | HIGH |
| Domain Rules | ✅ | N/A | N/A | N/A | DONE |

---

## Appendix B: Dependencies

Add to relevant `build.gradle.kts` files:

```kotlin
dependencies {
    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("io.mockk:mockk:1.13.8")
    
    // Compose UI testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Instrumentation testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    
    // Test utilities
    testImplementation("org.robolectric:robolectric:4.11")
}
```

---

**Document Owner:** Engineering Team  
**Review Cycle:** Quarterly  
**Last Updated:** 2026-04-07
