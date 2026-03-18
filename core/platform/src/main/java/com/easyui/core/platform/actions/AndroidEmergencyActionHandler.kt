package com.easyui.core.platform.actions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
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

    override suspend fun sendSms(phoneNumber: String, message: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return runCatching {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
            true
        }.getOrDefault(false)
    }

    override suspend fun callPhone(phoneNumber: String): Boolean {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val canResolve = callIntent.resolveActivity(context.packageManager) != null
        if (!canResolve) return false
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return runCatching {
                context.startActivity(callIntent)
                true
            }.getOrDefault(false)
        }
        return launchDialer(phoneNumber)
    }

    private fun dialIntent(phoneNumber: String?): Intent =
        if (phoneNumber.isNullOrBlank()) {
            Intent(Intent.ACTION_DIAL)
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        }
}
