package com.easyui.core.platform.launcher

import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.easyui.core.domain.DefaultLauncherManager

class AndroidDefaultLauncherManager(
    private val context: Context,
    private val fakeLauncherComponent: ComponentName? = null
) : DefaultLauncherManager {

    override fun isDefaultLauncher(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true) {
                return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            }
        }

        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolvedActivity = intent.resolveActivity(context.packageManager)
        
        // If it resolves to our package, we are default (or the only one)
        if (resolvedActivity?.packageName == context.packageName) return true
        
        // Android 11+ might resolve to "android" (resolver activity) if no default is set
        if (resolvedActivity?.packageName == "android") return false
        
        return false
    }

    override fun openDefaultLauncherSettings() {
        // 1. RoleManager (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true && !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                // Note: This requires starting an activity for result usually, 
                // but we can at least open the intent.
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        }

        // 2. Standard Settings Intents
        val homeSettings = Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val defaultAppsSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } else null

        // 3. OEM Specific Intents (Common ones)
        val oemIntents = listOf(
            Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$HomeSettingsActivity")),
            Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$DefaultAppsActivity")),
            Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.PreferredSettingsActivity")),
            Intent("android.settings.HOME_SETTINGS"),
            Intent("com.android.settings.HOME_SETTINGS")
        ).map { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        val intentToTry = when {
            homeSettings.resolveActivity(context.packageManager) != null -> homeSettings
            defaultAppsSettings?.resolveActivity(context.packageManager) != null -> defaultAppsSettings
            else -> oemIntents.find { it.resolveActivity(context.packageManager) != null }
        }

        if (intentToTry != null) {
            context.startActivity(intentToTry)
        } else {
            // 3. Fallback to Manufacturer specific or generic settings
            try {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: Exception) {
                // Absolute fallback
            }
        }
    }

    override fun triggerLauncherChooser() {
        val component = fakeLauncherComponent ?: return
        val pm = context.packageManager

        try {
            // 1. Enable the fake launcher
            pm.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            // 2. Start the HOME intent (this forces the system to ask which one to use if multiple resolve)
            val selector = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(selector)

            // 3. Disable the fake launcher immediately so it doesn't stay in the list
            pm.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            // Fallback to settings
            openDefaultLauncherSettings()
        }
    }
}
