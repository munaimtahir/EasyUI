package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType

object VerySimpleModeRules {
    fun simplify(tiles: List<HomeTile>, enabled: Boolean): List<HomeTile> {
        if (!enabled) return tiles
        val normalized = HomeLayoutRules.normalize(tiles)
        val contacts = normalized.filter { it.type == HomeTileType.CONTACT }.take(2)
        val actionApps = normalized.firstOrNull { it.action == HomeTileAction.OPEN_APP_LIST }
        val emergency = normalized.firstOrNull { it.action == HomeTileAction.EMERGENCY }
        val fallbackApp = normalized.firstOrNull { it.type == HomeTileType.APP }
        val flashlight = normalized.firstOrNull { it.action == HomeTileAction.FLASHLIGHT }

        val selected = buildList {
            if (actionApps != null) add(actionApps)
            addAll(contacts)
            if (emergency != null) add(emergency)
            if (contacts.isEmpty() && fallbackApp != null) add(fallbackApp)
            if (size < 4 && flashlight != null) add(flashlight)
        }
        return HomeLayoutRules.normalize(selected.distinctBy { it.id }.take(4))
    }
}
