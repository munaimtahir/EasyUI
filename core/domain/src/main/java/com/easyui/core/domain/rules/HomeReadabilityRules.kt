package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeReadabilityPreset

data class HomeReadabilityConfig(
    val columns: Int,
    val outerPaddingScale: Float,
    val tileSpacingScale: Float,
    val timeScale: Float,
    val titleScale: Float,
    val subtitleScale: Float,
)

object HomeReadabilityRules {
    fun config(
        preset: HomeReadabilityPreset,
        verySimpleModeEnabled: Boolean,
    ): HomeReadabilityConfig {
        if (verySimpleModeEnabled) {
            return HomeReadabilityConfig(
                columns = 1,
                outerPaddingScale = 1.35f,
                tileSpacingScale = 1.35f,
                timeScale = 1.1f,
                titleScale = 1.15f,
                subtitleScale = 1.1f,
            )
        }
        return when (preset) {
            HomeReadabilityPreset.STANDARD -> HomeReadabilityConfig(2, 1f, 1f, 1f, 1f, 1f)
            HomeReadabilityPreset.LARGER_TEXT -> HomeReadabilityConfig(2, 1f, 1f, 1.12f, 1.14f, 1.12f)
            HomeReadabilityPreset.LARGER_TILES -> HomeReadabilityConfig(1, 1.05f, 1.15f, 1.02f, 1.05f, 1.05f)
            HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> HomeReadabilityConfig(2, 1.2f, 1.3f, 1.03f, 1.02f, 1.02f)
        }
    }
}
