package com.easyui.core.platform.actions

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.easyui.core.domain.repository.CameraActionHandler
import com.easyui.core.domain.rules.ActionAvailabilityResolver

class AndroidCameraActionHandler(
    private val context: Context,
) : CameraActionHandler {
    override suspend fun currentState() =
        ActionAvailabilityResolver.camera(cameraIntent().resolveActivity(context.packageManager) != null)

    override suspend fun launchCamera(): Boolean {
        val intent = cameraIntent()
        if (intent.resolveActivity(context.packageManager) == null) {
            return false
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return true
    }

    private fun cameraIntent(): Intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
}
