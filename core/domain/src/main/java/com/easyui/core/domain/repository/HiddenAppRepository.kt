package com.easyui.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface HiddenAppRepository {
    fun observeHiddenPackages(): Flow<Set<String>>
    suspend fun getHiddenPackages(): Set<String>
    suspend fun setHidden(packageName: String, hidden: Boolean)
    suspend fun clearHiddenPackages()
    suspend fun replaceHiddenPackages(packages: Set<String>)
}
