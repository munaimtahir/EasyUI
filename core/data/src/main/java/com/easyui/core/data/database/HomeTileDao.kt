package com.easyui.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeTileDao {
    @Query("SELECT * FROM home_tiles ORDER BY position ASC")
    fun observeAll(): Flow<List<HomeTileEntity>>

    @Query("SELECT * FROM home_tiles ORDER BY position ASC")
    suspend fun getAll(): List<HomeTileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiles: List<HomeTileEntity>)

    @Query("DELETE FROM home_tiles")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(tiles: List<HomeTileEntity>) {
        clear()
        insertAll(tiles)
    }
}
