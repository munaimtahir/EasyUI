package com.easyui.core.platform.actions

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import com.easyui.core.domain.model.LauncherActionState
import com.easyui.core.domain.repository.FlashlightController
import com.easyui.core.domain.rules.ActionAvailabilityResolver

class AndroidFlashlightController(
    context: Context,
) : FlashlightController {
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private var torchEnabled = false

    override suspend fun currentState(): LauncherActionState =
        ActionAvailabilityResolver.flashlight(findTorchCameraId() != null)

    override suspend fun performToggle(): LauncherActionState {
        val torchCameraId = findTorchCameraId() ?: return ActionAvailabilityResolver.flashlight(false)
        return try {
            torchEnabled = !torchEnabled
            cameraManager.setTorchMode(torchCameraId, torchEnabled)
            LauncherActionState(enabled = true)
        } catch (_: CameraAccessException) {
            LauncherActionState(enabled = false, fallbackMessage = "Flashlight could not be activated.")
        } catch (_: SecurityException) {
            LauncherActionState(enabled = false, fallbackMessage = "Flashlight permission is unavailable on this device.")
        }
    }

    private fun findTorchCameraId(): String? =
        cameraManager.cameraIdList.firstOrNull { cameraId ->
            cameraManager.getCameraCharacteristics(cameraId)
                .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
}
