package com.easyui.core.platform.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.easyui.core.domain.model.LauncherActionState
import com.easyui.core.domain.repository.EmergencyActionHandler
import com.easyui.core.domain.rules.ActionAvailabilityResolver

class AndroidEmergencyActionHandler(
    private val context: Context,
) : EmergencyActionHandler {
    override suspend fun currentState(phoneNumber: String?): LauncherActionState {
        val dialIntent = dialIntent(phoneNumber)
        val canResolve = dialIntent.resolveActivity(context.packageManager) != null
        return if (phoneNumber.isNullOrBlank()) {
            ActionAvailabilityResolver.dialer(canResolve)
        } else {
            ActionAvailabilityResolver.emergency(canResolve, phoneNumber)
        }
    }

    override suspend fun launchDialer(phoneNumber: String?): Boolean {
        val dialIntent = dialIntent(phoneNumber)
        val activity = dialIntent.resolveActivity(context.packageManager) ?: return false
        dialIntent.setClassName(activity.packageName, activity.className)
        context.startActivity(dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return true
    }

    private fun dialIntent(phoneNumber: String?): Intent =
        if (phoneNumber.isNullOrBlank()) {
            Intent(Intent.ACTION_DIAL)
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        }
}
