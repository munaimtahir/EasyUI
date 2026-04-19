package com.easyui.core.platform.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.easyui.core.domain.AppCatalogRepository
import com.easyui.core.domain.AppCatalogRules
import com.easyui.core.domain.InstalledApp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidAppCatalogRepository(
    private val context: Context,
) : AppCatalogRepository {
    private val packageManager: PackageManager = context.packageManager

    override fun observeInstalledApps(): Flow<List<InstalledApp>> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(loadApps())
            }
        }

        trySend(loadApps())

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    override suspend fun getInstalledApps(): List<InstalledApp> = loadApps()

    private fun loadApps(): List<InstalledApp> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val packageName = activityInfo.packageName
                if (packageName == context.packageName) return@mapNotNull null
                InstalledApp(
                    packageName = packageName,
                    activityName = activityInfo.name,
                    label = resolveInfo.loadLabel(packageManager)?.toString().orEmpty().ifBlank { packageName },
                )
            }
            .let(AppCatalogRules::sortAlphabetically)
    }
}
