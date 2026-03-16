package com.easyui.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.easyui.core.domain.repository.HiddenAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreHiddenAppRepository(
    context: Context,
) : HiddenAppRepository {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("hidden_apps.preferences_pb") },
    )

    override fun observeHiddenPackages(): Flow<Set<String>> =
        dataStore.data.map { preferences -> preferences[HIDDEN_PACKAGES] ?: emptySet() }

    override suspend fun getHiddenPackages(): Set<String> =
        observeHiddenPackages().first()

    override suspend fun setHidden(packageName: String, hidden: Boolean) {
        dataStore.edit { preferences ->
            val existing = preferences[HIDDEN_PACKAGES]?.toMutableSet() ?: mutableSetOf()
            if (hidden) {
                existing += packageName
            } else {
                existing -= packageName
            }
            preferences[HIDDEN_PACKAGES] = existing
        }
    }

    override suspend fun clearHiddenPackages() {
        dataStore.edit { preferences ->
            preferences[HIDDEN_PACKAGES] = emptySet()
        }
    }

    private companion object {
        val HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
    }
}
