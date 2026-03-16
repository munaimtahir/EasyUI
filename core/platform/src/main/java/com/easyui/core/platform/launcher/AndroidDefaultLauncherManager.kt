package com.easyui.core.platform.launcher

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.easyui.core.domain.repository.DefaultLauncherManager

class AndroidDefaultLauncherManager(
    private val context: Context,
) : DefaultLauncherManager {
    override fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolvedActivity = intent.resolveActivity(context.packageManager)
        return resolvedActivity?.packageName == context.packageName
    }

    override fun openDefaultLauncherSettings() {
        val homeSettings = Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val defaultAppsSettings = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        when {
            homeSettings.resolveActivity(context.packageManager) != null -> context.startActivity(homeSettings)
            defaultAppsSettings.resolveActivity(context.packageManager) != null -> context.startActivity(defaultAppsSettings)
        }
    }
}
