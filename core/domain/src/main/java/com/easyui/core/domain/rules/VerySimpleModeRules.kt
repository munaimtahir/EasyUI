package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType

object VerySimpleModeRules {
    fun simplify(tiles: List<HomeTile>, enabled: Boolean): List<HomeTile> {
        if (!enabled) return tiles
        val normalized = HomeLayoutRules.ensureRequiredActions(tiles)
        val contacts = normalized.filter { it.type == HomeTileType.CONTACT }.take(2)
        val phone = normalized.firstOrNull { it.action == HomeTileAction.OPEN_DIALER }
        val emergency = normalized.firstOrNull { it.action == HomeTileAction.EMERGENCY }
        val fallbackApp = normalized.firstOrNull { it.type == HomeTileType.APP }
        val flashlight = normalized.firstOrNull { it.action == HomeTileAction.FLASHLIGHT }

        val selected = buildList {
            if (phone != null) add(phone)
            if (emergency != null) add(emergency)
            addAll(contacts)
            if (contacts.isEmpty() && fallbackApp != null) add(fallbackApp)
            if (size < 4 && flashlight != null) add(flashlight)
        }
        return selected
            .distinctBy { it.id }
            .take(4)
            .mapIndexed { index, tile -> tile.copy(position = index) }
    }
}
