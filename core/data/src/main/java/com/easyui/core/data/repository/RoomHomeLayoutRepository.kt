package com.easyui.core.data.repository

import com.easyui.core.data.database.HomeTileDao
import com.easyui.core.data.database.toDomain
import com.easyui.core.data.database.toEntity
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.repository.HomeLayoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomHomeLayoutRepository(
    private val homeTileDao: HomeTileDao,
) : HomeLayoutRepository {
    override fun observeTiles(): Flow<List<HomeTile>> =
        homeTileDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTiles(): List<HomeTile> =
        homeTileDao.getAll().map { it.toDomain() }

    override suspend fun replaceTiles(tiles: List<HomeTile>) {
        homeTileDao.replaceAll(tiles.map { it.toEntity() })
    }
}
