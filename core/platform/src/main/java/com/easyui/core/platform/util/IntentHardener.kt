package com.easyui.core.platform.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

/**
 * Utility for robustly resolving and launching intents across various Android devices/OEMs.
 */
object IntentHardener {
    private const val TAG = "IntentHardener"

    /**
     * Tries to resolve and start the given intent.
     * Returns true if successful, false otherwise.
     */
    fun attemptLaunch(context: Context, intent: Intent): Boolean {
        val pm = context.packageManager
        
        // Ensure FLAG_ACTIVITY_NEW_TASK is set if not already
        if (intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK == 0) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val resolved = intent.resolveActivity(pm)
        if (resolved == null) {
            Log.w(TAG, "No activity resolved for intent: $intent")
            return false
        }

        // On some devices, resolveActivity might return the ResolverActivity ("android")
        // if no default is set. We still want to try launching it so the user can choose.
        
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start activity for intent: $intent", e)
            false
        }
    }

    /**
     * Tries multiple intents in order until one succeeds.
     */
    fun attemptLaunchAny(context: Context, intents: List<Intent>): Boolean {
        for (intent in intents) {
            if (attemptLaunch(context, intent)) return true
        }
        return false
    }

    /**
     * Safe search for an app package if standard intent fails.
     */
    fun findAlternativePackage(context: Context, query: String): String? {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return apps.firstOrNull { 
            it.packageName.contains(query, ignoreCase = true) || 
            pm.getApplicationLabel(it).toString().contains(query, ignoreCase = true)
        }?.packageName
    }
}
