package com.easyui.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

fun easyUiIconForLabel(label: String): ImageVector =
    when (label.trim().lowercase()) {
        "phone", "call shortcuts" -> Icons.Outlined.Call
        "flashlight" -> Icons.Outlined.FlashlightOn
        "camera" -> Icons.Outlined.PhotoCamera
        "emergency", "emergency number" -> Icons.Outlined.Campaign
        "health info" -> Icons.Outlined.HealthAndSafety
        "sos" -> Icons.Outlined.Shield
        "layout / pages", "layout and pages" -> Icons.Outlined.ViewCarousel
        "home apps" -> Icons.Outlined.Home
        "backup and restore" -> Icons.Outlined.Schedule
        "hidden apps" -> Icons.Outlined.Widgets
        "lock / protection" -> Icons.Outlined.Lock
        else -> Icons.Outlined.FavoriteBorder
    }
