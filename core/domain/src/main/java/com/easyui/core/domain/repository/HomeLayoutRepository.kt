package com.easyui.core.domain.repository

import com.easyui.core.domain.model.HomeTile
import kotlinx.coroutines.flow.Flow

interface HomeLayoutRepository {
    fun observeTiles(): Flow<List<HomeTile>>
    suspend fun getTiles(): List<HomeTile>
    suspend fun replaceTiles(tiles: List<HomeTile>)
}
