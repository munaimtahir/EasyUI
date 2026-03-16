package com.easyui.core.platform.actions

import android.content.Context
import android.content.Intent
import com.easyui.core.domain.repository.AppLauncher

class AndroidAppLauncher(
    private val context: Context,
) : AppLauncher {
    override suspend fun launch(packageName: String, activityName: String): Boolean {
        val launchIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(packageName, activityName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (launchIntent.resolveActivity(context.packageManager) == null) return false
        context.startActivity(launchIntent)
        return true
    }
}
