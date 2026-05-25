package com.easyui.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Ensures EasyUI starts or is ready as soon as the device boots.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            // Just starting the activity is enough to ensure we are the first thing shown
            // if we are the default launcher.
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launchIntent)
        }
    }
}
