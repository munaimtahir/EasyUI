package com.easyui.core.platform.actions

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.easyui.core.domain.ActionAvailabilityResolver
import com.easyui.core.domain.CameraActionHandler

import com.easyui.core.platform.util.IntentHardener

class AndroidCameraActionHandler(
    private val context: Context,
) : CameraActionHandler {
    override suspend fun currentState() =
        ActionAvailabilityResolver.camera(cameraIntent().resolveActivity(context.packageManager) != null)

    override suspend fun launchCamera(): Boolean {
        return IntentHardener.attemptLaunch(context, cameraIntent())
    }

    private fun cameraIntent(): Intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
}
