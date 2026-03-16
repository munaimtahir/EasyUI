package com.easyui.core.domain.repository

import com.easyui.core.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppCatalogRepository {
    fun observeInstalledApps(): Flow<List<InstalledApp>>
    suspend fun getInstalledApps(): List<InstalledApp>
}
