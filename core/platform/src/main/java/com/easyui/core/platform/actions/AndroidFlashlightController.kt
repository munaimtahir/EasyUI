package com.easyui.core.platform.actions

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import com.easyui.core.domain.ActionAvailabilityResolver
import com.easyui.core.domain.FlashlightController
import com.easyui.core.domain.LauncherActionState

class AndroidFlashlightController(
    context: Context,
) : FlashlightController {
    private val packageManager = context.packageManager
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private var torchEnabled = false
    private var activeTorchCameraId: String? = null

    override suspend fun currentState(): LauncherActionState =
        ActionAvailabilityResolver.flashlight(hasFlashlight())

    override suspend fun performToggle(): LauncherActionState {
        if (!hasFlashlight()) {
            return ActionAvailabilityResolver.flashlight(false)
        }

        val desiredState = !torchEnabled
        val cameraIds: List<String> = try {
            buildList {
                activeTorchCameraId?.let(::add)
                addAll(cameraManager.cameraIdList.filterNot { it == activeTorchCameraId })
            }
        } catch (_: CameraAccessException) {
            return LauncherActionState(
                enabled = false,
                fallbackMessage = "Flashlight could not be activated.",
            )
        }

        for (cameraId in cameraIds) {
            try {
                cameraManager.setTorchMode(cameraId, desiredState)
                activeTorchCameraId = cameraId
                torchEnabled = desiredState
                return LauncherActionState(enabled = true)
            } catch (_: IllegalArgumentException) {
                // Ignore cameras that do not expose torch mode.
            } catch (_: CameraAccessException) {
                return LauncherActionState(
                    enabled = false,
                    fallbackMessage = "Flashlight could not be activated.",
                )
            } catch (_: SecurityException) {
                return LauncherActionState(
                    enabled = false,
                    fallbackMessage = "Flashlight could not be activated on this device.",
                )
            }
        }

        return LauncherActionState(enabled = false, fallbackMessage = "Flashlight could not be activated.")
    }

    private fun hasFlashlight(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
}
