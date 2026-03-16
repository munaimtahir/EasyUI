package com.easyui.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType

@Entity(tableName = "home_tiles")
data class HomeTileEntity(
    @PrimaryKey val id: String,
    val position: Int,
    val title: String,
    val type: String,
    val packageName: String?,
    val action: String?,
    val phoneNumber: String?,
    val photoUri: String?,
)

fun HomeTileEntity.toDomain(): HomeTile =
    HomeTile(
        id = id,
        position = position,
        title = title,
        type = HomeTileType.valueOf(type),
        packageName = packageName,
        action = action?.let(HomeTileAction::valueOf),
        phoneNumber = phoneNumber,
        photoUri = photoUri,
    )

fun HomeTile.toEntity(): HomeTileEntity =
    HomeTileEntity(
        id = id,
        position = position,
        title = title,
        type = type.name,
        packageName = packageName,
        action = action?.name,
        phoneNumber = phoneNumber,
        photoUri = photoUri,
    )
