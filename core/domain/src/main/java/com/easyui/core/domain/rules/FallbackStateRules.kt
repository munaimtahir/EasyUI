package com.easyui.core.domain.rules

data class CalmFallbackState(
    val title: String,
    val body: String,
)

object FallbackStateRules {
    fun appList(query: String, visibleAppCount: Int): CalmFallbackState? =
        if (visibleAppCount > 0) {
            null
        } else if (query.isBlank()) {
            CalmFallbackState(
                title = "No apps are shown here right now",
                body = "A caregiver may have hidden some apps inside EasyUI.",
            )
        } else {
            CalmFallbackState(
                title = "No apps match that search",
                body = "Try a shorter name or clear the search box.",
            )
        }

    fun home(tileCount: Int, verySimpleModeEnabled: Boolean): CalmFallbackState? =
        when {
            tileCount <= 0 -> CalmFallbackState(
                title = "Home is getting ready",
                body = "Use All Apps or ask a caregiver to finish setup.",
            )
            verySimpleModeEnabled -> CalmFallbackState(
                title = "Very simple home is on",
                body = "Favorite contacts and a few essentials stay easy to reach.",
            )
            tileCount <= 2 -> CalmFallbackState(
                title = "Home is ready",
                body = "Use All Apps to see everything else.",
            )
            else -> null
        }
}
