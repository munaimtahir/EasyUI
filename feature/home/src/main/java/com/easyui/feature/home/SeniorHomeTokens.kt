package com.easyui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.TileDisplayKind

object SeniorHomeTokens {
    val backgroundTop = Color(0xFF0D1238)
    val backgroundMid = Color(0xFF1A1550)
    val backgroundBottom = Color(0xFF3A156A)

    val headerBackground = Color(0xFF5D9BFF)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondaryOnHeader = Color(0xFFECF3FF)

    val tilePhone = Color(0xFF57D64E)
    val tileMessages = Color(0xFFF29A3A)
    val tileContacts = Color(0xFFE84D64)
    val tilePhotos = Color(0xFF4F83F1)
    val tileCamera = Color(0xFF9B5DE5)
    val tileEmergency = Color(0xFFD92D20)
    val focusRing = Color(0xCCFFFFFF)

    val pageHorizontalPadding = 16.dp
    val topSafeSpacing = 12.dp
    val bottomSpacing = 16.dp
    val sectionGap = 12.dp
    val gridGap = 12.dp
    val headerHorizontalPadding = 16.dp
    val headerVerticalPadding = 18.dp
    val tileHorizontalPadding = 12.dp
    val tileVerticalPadding = 16.dp
    val cornerRadius = 16.dp
    val focusRingWidth = 2.dp
    val iconSize = 32.dp
    val minimumTargetSize = 64.dp

    val timeTextSize = 36.sp
    val timeTextSizeLarge = 40.sp
    val dateTextSize = 15.sp
    val dateTextSizeLarge = 16.sp
    val labelTextSize = 15.sp
    val labelTextSizeLarge = 16.sp

    val backgroundBrush: Brush
        get() = Brush.verticalGradient(
            colors = listOf(backgroundTop, backgroundMid, backgroundBottom),
        )

    fun tileColor(kind: TileDisplayKind): Color =
        when (kind) {
            TileDisplayKind.PHONE -> tilePhone
            TileDisplayKind.MESSAGES -> tileMessages
            TileDisplayKind.CONTACTS -> tileContacts
            TileDisplayKind.PHOTOS -> tilePhotos
            TileDisplayKind.CAMERA -> tileCamera
            TileDisplayKind.EMERGENCY -> tileEmergency
        }

    fun tileIcon(kind: TileDisplayKind): ImageVector =
        when (kind) {
            TileDisplayKind.PHONE -> Icons.Filled.Call
            TileDisplayKind.MESSAGES -> Icons.Filled.ChatBubble
            TileDisplayKind.CONTACTS -> Icons.Filled.Contacts
            TileDisplayKind.PHOTOS -> Icons.Filled.PhotoLibrary
            TileDisplayKind.CAMERA -> Icons.Filled.PhotoCamera
            TileDisplayKind.EMERGENCY -> Icons.Filled.Warning
        }
}
