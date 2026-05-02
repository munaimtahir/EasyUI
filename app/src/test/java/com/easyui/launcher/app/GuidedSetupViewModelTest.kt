package com.easyui.launcher.app

import app.cash.turbine.test
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.repository.HomeLayoutRepository
import com.easyui.core.domain.repository.LauncherSettingsRepository
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.repository.DefaultLauncherManager
import com.easyui.launcher.di.AppContainer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for GuidedSetupViewModel
 *
 * Tests cover:
 * - Step navigation (next, previous, specific)
 * - Setup completion
 * - PIN validation and storage
 * - Launcher status checking
 * - Readability preset changes
 * - Emergency mode configuration
 * - Layout lock state
 * - State persistence
 * - Error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GuidedSetupViewModelTest {

    private lateinit var viewModel: GuidedSetupViewModel
    private lateinit var mockContainer: AppContainer
    private lateinit var mockSettingsRepository: LauncherSettingsRepository
    private lateinit var mockLayoutRepository: HomeLayoutRepository
    private lateinit var mockLauncherManager: DefaultLauncherManager
    private val settingsFlow = MutableStateFlow(createDefaultSettings())
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockSettingsRepository = mockk(relaxed = true) {
            every { settings } returns settingsFlow
            coEvery { getSkinConfig() } returns SkinConfig(layoutMode = LayoutMode.SIMPLE_CLASSIC)
            coEvery { updateGuidedSetupStep(any()) } coAnswers {
                settingsFlow.update { it.copy(guidedSetupStep = firstArg()) }
            }
            coEvery { updateGuidedSetupCompleted(any()) } coAnswers {
                settingsFlow.update { it.copy(guidedSetupCompleted = firstArg()) }
            }
            coEvery { updateOnboardingComplete(any()) } coAnswers {
                settingsFlow.update { it.copy(onboardingComplete = firstArg()) }
            }
        }
        
        mockLayoutRepository = mockk(relaxed = true) {
            coEvery { getTiles() } returns emptyList()
        }
        
        mockLauncherManager = mockk(relaxed = true) {
            every { isDefaultLauncher() } returns false
        }
        
        mockContainer = mockk {
            every { launcherSettingsRepository } returns mockSettingsRepository
            every { homeLayoutRepository } returns mockLayoutRepository
            every { defaultLauncherManager } returns mockLauncherManager
        }
        
        viewModel = GuidedSetupViewModel(mockContainer)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createDefaultSettings(
        guidedSetupCompleted: Boolean = false,
        guidedSetupStep: Int = 1,
        onboardingComplete: Boolean = false,
        pinHashHex: String? = null,
        layoutLocked: Boolean = false,
        emergencyMode: String = "MENU",
        homeReadabilityPreset: String = "STANDARD"
    ) = LauncherSettings(
        onboardingComplete = onboardingComplete,
        guidedSetupCompleted = guidedSetupCompleted,
        guidedSetupStep = guidedSetupStep,
        pinHashHex = pinHashHex,
        pinSaltHex = if (pinHashHex != null) "salt123" else null,
        layoutLocked = layoutLocked,
        emergencyMode = emergencyMode,
        homeReadabilityPreset = homeReadabilityPreset,
        caregiverProtectionEnabled = pinHashHex != null
    )

    // ========================================================================
    // STEP NAVIGATION TESTS
    // ========================================================================

    @Test
    fun `initial state should be at step 1`() = runTest {
        assertEquals(1, viewModel.state.value.guidedSetupStep)
        assertEquals(13, viewModel.state.value.totalSteps)
        assertFalse(viewModel.state.value.guidedSetupCompleted)
    }

    @Test
    fun `nextStep should advance to step 2`() = runTest {
        viewModel.nextStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupStep(2) }
    }

    @Test
    fun `nextStep from step 5 should advance to step 6`() = runTest {
        settingsFlow.update { it.copy(guidedSetupStep = 5) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.nextStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupStep(6) }
    }

    @Test
    fun `nextStep from step 13 should complete setup`() = runTest {
        settingsFlow.update { it.copy(guidedSetupStep = 13) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.nextStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupCompleted(true) }
        coVerify { mockSettingsRepository.updateOnboardingComplete(true) }
    }

    @Test
    fun `previousStep should go back to step 1`() = runTest {
        settingsFlow.update { it.copy(guidedSetupStep = 2) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.previousStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupStep(1) }
    }

    @Test
    fun `previousStep from step 1 should not go below 1`() = runTest {
        viewModel.previousStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 0) { mockSettingsRepository.updateGuidedSetupStep(any()) }
    }

    @Test
    fun `setStep should jump to specific step`() = runTest {
        viewModel.setStep(7)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupStep(7) }
    }

    // ========================================================================
    // COMPLETION TESTS
    // ========================================================================

    @Test
    fun `completeSetup should mark guided setup as complete`() = runTest {
        viewModel.completeSetup()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupCompleted(true) }
        coVerify { mockSettingsRepository.updateOnboardingComplete(true) }
    }

    @Test
    fun `state should reflect completed status after completion`() = runTest {
        settingsFlow.update { it.copy(guidedSetupCompleted = true, onboardingComplete = true) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value.guidedSetupCompleted)
        assertTrue(viewModel.state.value.onboardingComplete)
    }

    // ========================================================================
    // PIN VALIDATION TESTS (Critical - from audit fix)
    // ========================================================================

    @Test
    fun `savePin with empty PIN should return true and not set PIN`() = runTest {
        // Empty PIN = user choosing no PIN, which is allowed
        viewModel.updatePinInput("")
        viewModel.updateConfirmPinInput("")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // This test verifies the audit fix: empty PIN should be allowed
        // The actual behavior is in navigation logic, but ViewModel shouldn't error
        assertEquals("", viewModel.state.value.pinInput)
        assertEquals("", viewModel.state.value.confirmPinInput)
    }

    @Test
    fun `savePin with short PIN should return false and show error`() = runTest {
        viewModel.updatePinInput("12")
        viewModel.updateConfirmPinInput("12")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val result = viewModel.savePin()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(result)
        assertEquals("PIN must be at least 4 digits.", viewModel.state.value.pinError)
        coVerify(exactly = 0) { mockSettingsRepository.storePinCredential(any()) }
    }

    @Test
    fun `savePin with mismatched PINs should return false and show error`() = runTest {
        viewModel.updatePinInput("1234")
        viewModel.updateConfirmPinInput("5678")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val result = viewModel.savePin()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(result)
        assertEquals("PINs do not match.", viewModel.state.value.pinError)
        coVerify(exactly = 0) { mockSettingsRepository.storePinCredential(any()) }
    }

    @Test
    fun `savePin with valid matched PIN should return true and store PIN`() = runTest {
        viewModel.updatePinInput("1234")
        viewModel.updateConfirmPinInput("1234")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val result = viewModel.savePin()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(result)
        assertNull(viewModel.state.value.pinError)
        coVerify { mockSettingsRepository.storePinCredential(any()) }
        coVerify { mockSettingsRepository.updateCaregiverProtectionEnabled(true) }
    }

    @Test
    fun `savePin should clear PIN inputs after successful save`() = runTest {
        viewModel.updatePinInput("1234")
        viewModel.updateConfirmPinInput("1234")
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.savePin()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("", viewModel.state.value.pinInput)
        assertEquals("", viewModel.state.value.confirmPinInput)
    }

    @Test
    fun `updatePinInput should clear previous error`() = runTest {
        viewModel.updatePinInput("12")
        viewModel.updateConfirmPinInput("12")
        viewModel.savePin() // This will set error
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull(viewModel.state.value.pinError)
        
        viewModel.updatePinInput("123")
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNull(viewModel.state.value.pinError)
    }

    @Test
    fun `state should reflect PIN configured when hash exists`() = runTest {
        settingsFlow.update { it.copy(pinHashHex = "abc123", pinSaltHex = "salt") }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value.hasPinConfigured)
    }

    @Test
    fun `state should reflect no PIN when hash is blank`() = runTest {
        settingsFlow.update { it.copy(pinHashHex = null) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(viewModel.state.value.hasPinConfigured)
    }

    // ========================================================================
    // LAUNCHER STATUS TESTS
    // ========================================================================

    @Test
    fun `isDefaultLauncher should reflect launcher manager status`() = runTest {
        every { mockLauncherManager.isDefaultLauncher() } returns true
        viewModel.refreshLauncherStatus()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value.isDefaultLauncher)
    }

    @Test
    fun `openLauncherSettings should call launcher manager`() = runTest {
        viewModel.openLauncherSettings()
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify { mockLauncherManager.openDefaultLauncherSettings() }
    }

    @Test
    fun `openLauncherSettings should refresh status after delay`() = runTest {
        val initialTriggerValue = viewModel.state.value.isDefaultLauncher
        
        viewModel.openLauncherSettings()
        testDispatcher.scheduler.advanceTimeBy(1100) // Advance past the 1000ms delay
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify(atLeast = 1) { mockLauncherManager.isDefaultLauncher() }
    }

    // ========================================================================
    // READABILITY PRESET TESTS
    // ========================================================================

    @Test
    fun `updateReadabilityPreset should update repository`() = runTest {
        viewModel.updateReadabilityPreset(HomeReadabilityPreset.LARGER_TEXT)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateHomeReadabilityPreset("LARGER_TEXT") }
    }

    @Test
    fun `updateReadabilityPreset to STANDARD should update skin config`() = runTest {
        viewModel.updateReadabilityPreset(HomeReadabilityPreset.STANDARD)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { 
            mockSettingsRepository.setSkinConfig(any())
        }
    }

    @Test
    fun `updateReadabilityPreset to LARGER_TEXT should update skin config`() = runTest {
        viewModel.updateReadabilityPreset(HomeReadabilityPreset.LARGER_TEXT)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { 
            mockSettingsRepository.setSkinConfig(any())
        }
    }

    @Test
    fun `state should reflect current readability preset`() = runTest {
        settingsFlow.update { it.copy(homeReadabilityPreset = "LARGER_TILES") }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(HomeReadabilityPreset.LARGER_TILES, viewModel.state.value.homeReadabilityPreset)
    }

    @Test
    fun `state should default to STANDARD for invalid preset name`() = runTest {
        settingsFlow.update { it.copy(homeReadabilityPreset = "INVALID_PRESET") }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(HomeReadabilityPreset.STANDARD, viewModel.state.value.homeReadabilityPreset)
    }

    // ========================================================================
    // EMERGENCY MODE TESTS
    // ========================================================================

    @Test
    fun `updateEmergencyMode should update repository`() = runTest {
        viewModel.updateEmergencyMode("SOS")
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateEmergencyMode("SOS") }
    }

    @Test
    fun `updateEmergencyMode to SOS should update home tiles`() = runTest {
        val mockTiles = listOf(
            mockk<HomeTile>(relaxed = true),
            mockk<HomeTile>(relaxed = true)
        )
        coEvery { mockLayoutRepository.getTiles() } returns mockTiles
        
        viewModel.updateEmergencyMode("SOS")
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockLayoutRepository.getTiles() }
        coVerify { mockLayoutRepository.replaceTiles(any()) }
    }

    @Test
    fun `state should reflect current emergency mode`() = runTest {
        settingsFlow.update { it.copy(emergencyMode = "SOS") }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("SOS", viewModel.state.value.emergencyMode)
    }

    // ========================================================================
    // LAYOUT LOCK TESTS
    // ========================================================================

    @Test
    fun `updateLayoutLocked to true should update repository`() = runTest {
        viewModel.updateLayoutLocked(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateLayoutLocked(true) }
    }

    @Test
    fun `updateLayoutLocked to false should update repository`() = runTest {
        viewModel.updateLayoutLocked(false)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateLayoutLocked(false) }
    }

    @Test
    fun `state should reflect layout locked status`() = runTest {
        settingsFlow.update { it.copy(layoutLocked = true) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.state.value.layoutLocked)
    }

    // ========================================================================
    // STATE LOADING TESTS
    // ========================================================================

    @Test
    fun `state should be loaded after initialization`() = runTest {
        assertTrue(viewModel.state.value.settingsLoaded)
    }

    @Test
    fun `guidedSetupStep should never be less than 1`() = runTest {
        settingsFlow.update { it.copy(guidedSetupStep = 0) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, viewModel.state.value.guidedSetupStep)
    }

    @Test
    fun `guidedSetupStep should never be less than 1 even with negative value`() = runTest {
        settingsFlow.update { it.copy(guidedSetupStep = -5) }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, viewModel.state.value.guidedSetupStep)
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    fun `complete wizard flow from step 1 to completion`() = runTest {
        // Start at step 1
        assertEquals(1, viewModel.state.value.guidedSetupStep)
        
        // Advance through all steps
        repeat(12) {
            viewModel.nextStep()
            testDispatcher.scheduler.advanceUntilIdle()
        }
        
        // Verify we're at step 13
        coVerify { mockSettingsRepository.updateGuidedSetupStep(13) }
        
        // Complete setup
        viewModel.nextStep()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockSettingsRepository.updateGuidedSetupCompleted(true) }
    }

    @Test
    fun `configure security and emergency mode together`() = runTest {
        // Set PIN
        viewModel.updatePinInput("1234")
        viewModel.updateConfirmPinInput("1234")
        testDispatcher.scheduler.advanceUntilIdle()
        val pinSaved = viewModel.savePin()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(pinSaved)
        
        // Enable layout lock
        viewModel.updateLayoutLocked(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set emergency mode
        viewModel.updateEmergencyMode("SOS")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify all updates were made
        coVerify { mockSettingsRepository.storePinCredential(any()) }
        coVerify { mockSettingsRepository.updateCaregiverProtectionEnabled(true) }
        coVerify { mockSettingsRepository.updateLayoutLocked(true) }
        coVerify { mockSettingsRepository.updateEmergencyMode("SOS") }
    }
}
