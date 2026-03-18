package com.easyui.core.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.VisualTheme

enum class EmphasisMode {
    BALANCED,
    CARE_FOCUSED,
    COMMUNICATION_FOCUSED,
}

data class LayoutConfig(
    val gridRows: Int,
    val gridCols: Int,
    val tileSizeScale: Float,
    val showLabels: Boolean,
    val emphasisMode: EmphasisMode,
)

data class TileStyle(
    val elevationDp: Float,
    val borderWidthDp: Float,
    val shadowAlpha: Float,
)

class SkinManager(
    initialConfig: SkinConfig,
) {
    private var activeConfig: SkinConfig = initialConfig

    fun setSkinConfig(config: SkinConfig) {
        activeConfig = config
    }

    fun getSkinConfig(): SkinConfig = activeConfig

    fun getColors(): ColorPalette = resolve(activeConfig).colors

    fun getTypography(): TypographySet = resolve(activeConfig).typography

    fun getSpacing(): SpacingSet = resolve(activeConfig).spacing

    fun getTileStyle(): TileStyle = resolve(activeConfig).tileStyle

    fun getLayoutConfig(): LayoutConfig = resolve(activeConfig).layout

    private data class SkinResolved(
        val colors: ColorPalette,
        val typography: TypographySet,
        val spacing: SpacingSet,
        val tileStyle: TileStyle,
        val layout: LayoutConfig,
    )

    private fun resolve(config: SkinConfig): SkinResolved {
        val base = SkinResolved(
            colors = ColorPalette(
                background = androidx.compose.ui.graphics.Color(0xFFF7F8FA),
                primaryText = androidx.compose.ui.graphics.Color(0xFF111827),
                secondaryText = androidx.compose.ui.graphics.Color(0xFF475467),
                tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                accent = androidx.compose.ui.graphics.Color(0xFF2E5BFF),
                sosColor = androidx.compose.ui.graphics.Color(0xFFD92D20),
            ),
            typography = TypographySet(
                headingSize = 34.sp,
                bodySize = 20.sp,
                labelSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            spacing = SpacingSet(
                padding = 24.dp,
                tileSpacing = 16.dp,
                cornerRadius = 20.dp,
            ),
            tileStyle = TileStyle(
                elevationDp = 4f,
                borderWidthDp = 1f,
                shadowAlpha = 0.14f,
            ),
            layout = LayoutConfig(
                gridRows = 3,
                gridCols = 2,
                tileSizeScale = 1f,
                showLabels = true,
                emphasisMode = EmphasisMode.BALANCED,
            ),
        )

        val visual = when (config.visualTheme) {
            VisualTheme.LIGHT_PREMIUM -> base
            VisualTheme.DARK_COMFORT -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFF121417),
                    primaryText = androidx.compose.ui.graphics.Color(0xFFE6E8EC),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFFB8BFCB),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFF1A1F29),
                    accent = androidx.compose.ui.graphics.Color(0xFF89B4FF),
                ),
                tileStyle = base.tileStyle.copy(elevationDp = 2f, shadowAlpha = 0.08f),
            )
            VisualTheme.CLINICAL_PROFESSIONAL -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFFFDFEFF),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF0F172A),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF334155),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFF8FAFC),
                    accent = androidx.compose.ui.graphics.Color(0xFF2563EB),
                ),
                tileStyle = base.tileStyle.copy(elevationDp = 1f, shadowAlpha = 0.05f),
            )
            VisualTheme.SOFT_CALM -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFFF9F6FB),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF1F2937),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF5B6472),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFCFE),
                    accent = androidx.compose.ui.graphics.Color(0xFF8B5CF6),
                ),
            )
        }

        val withLayout = when (config.layoutMode) {
            LayoutMode.SIMPLE_CLASSIC -> visual
            LayoutMode.VERY_SIMPLE -> visual.copy(
                layout = visual.layout.copy(tileSizeScale = 1.12f, showLabels = false, emphasisMode = EmphasisMode.BALANCED),
                typography = visual.typography.copy(headingSize = visual.typography.headingSize * 1.08f, bodySize = visual.typography.bodySize * 1.08f),
                spacing = visual.spacing.copy(tileSpacing = visual.spacing.tileSpacing + 2.dp),
            )
            LayoutMode.CARE_MODE -> visual.copy(
                layout = visual.layout.copy(emphasisMode = EmphasisMode.CARE_FOCUSED),
            )
            LayoutMode.COMMUNICATION_MODE -> visual.copy(
                layout = visual.layout.copy(emphasisMode = EmphasisMode.COMMUNICATION_FOCUSED),
            )
        }

        return when (config.accessibilityMode) {
            AccessibilityMode.NONE -> withLayout
            AccessibilityMode.HIGH_CONTRAST -> withLayout.copy(
                colors = withLayout.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF000000),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF111111),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    accent = androidx.compose.ui.graphics.Color(0xFF0033CC),
                    sosColor = androidx.compose.ui.graphics.Color(0xFFB00020),
                ),
                tileStyle = withLayout.tileStyle.copy(borderWidthDp = 2f),
            )
            AccessibilityMode.BOLD_ACCESSIBILITY -> withLayout.copy(
                typography = withLayout.typography.copy(
                    headingSize = withLayout.typography.headingSize * 1.14f,
                    bodySize = withLayout.typography.bodySize * 1.16f,
                    labelSize = withLayout.typography.labelSize * 1.16f,
                    fontWeight = FontWeight.Bold,
                ),
                spacing = withLayout.spacing.copy(
                    padding = withLayout.spacing.padding + 2.dp,
                    tileSpacing = withLayout.spacing.tileSpacing + 2.dp,
                ),
            )
        }
    }
}
