package com.easyui.core.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.AccessibilityMode
import com.easyui.core.domain.LayoutMode
import com.easyui.core.domain.SkinConfig
import com.easyui.core.domain.VisualTheme

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
    val glowAlpha: Float,
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
                background = androidx.compose.ui.graphics.Color(0xFFF6F1E8),
                backgroundAccent = androidx.compose.ui.graphics.Color(0xFFE7DDD0),
                surface = androidx.compose.ui.graphics.Color(0xFFFFFCF7),
                surfaceMuted = androidx.compose.ui.graphics.Color(0xFFF1E8DB),
                primaryText = androidx.compose.ui.graphics.Color(0xFF1F2624),
                secondaryText = androidx.compose.ui.graphics.Color(0xFF5F6663),
                tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFBF5),
                tileBackgroundMuted = androidx.compose.ui.graphics.Color(0xFFF0E7DA),
                accent = androidx.compose.ui.graphics.Color(0xFF1B8A7A),
                accentMuted = androidx.compose.ui.graphics.Color(0xFFD7EEE8),
                outline = androidx.compose.ui.graphics.Color(0xFFD8CCBD),
                statusCard = androidx.compose.ui.graphics.Color(0xFFF0EADF),
                sosColor = androidx.compose.ui.graphics.Color(0xFFD92D20),
                successColor = androidx.compose.ui.graphics.Color(0xFF2D6A4F),
            ),
            typography = TypographySet(
                displaySize = 42.sp,
                headingSize = 34.sp,
                titleSize = 24.sp,
                bodySize = 20.sp,
                labelSize = 17.sp,
                supportSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            spacing = SpacingSet(
                padding = 24.dp,
                sectionSpacing = 20.dp,
                tileSpacing = 16.dp,
                cornerRadius = 24.dp,
                panelCornerRadius = 32.dp,
            ),
            tileStyle = TileStyle(
                elevationDp = 8f,
                borderWidthDp = 1f,
                shadowAlpha = 0.12f,
                glowAlpha = 0.18f,
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
            VisualTheme.LIGHT_PREMIUM, VisualTheme.AUTO -> base
            VisualTheme.DARK_COMFORT -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFF161A1B),
                    backgroundAccent = androidx.compose.ui.graphics.Color(0xFF102625),
                    surface = androidx.compose.ui.graphics.Color(0xFF202526),
                    surfaceMuted = androidx.compose.ui.graphics.Color(0xFF2A3132),
                    primaryText = androidx.compose.ui.graphics.Color(0xFFF1EEE7),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFFC5C9C5),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFF252D2E),
                    tileBackgroundMuted = androidx.compose.ui.graphics.Color(0xFF1E2526),
                    accent = androidx.compose.ui.graphics.Color(0xFF7ED6C8),
                    accentMuted = androidx.compose.ui.graphics.Color(0xFF193B38),
                    outline = androidx.compose.ui.graphics.Color(0xFF374141),
                    statusCard = androidx.compose.ui.graphics.Color(0xFF1D2324),
                    successColor = androidx.compose.ui.graphics.Color(0xFF6ECF9E),
                ),
                tileStyle = base.tileStyle.copy(elevationDp = 3f, shadowAlpha = 0.08f, glowAlpha = 0.12f),
            )
            VisualTheme.CLINICAL_PROFESSIONAL -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFFF9FCFD),
                    backgroundAccent = androidx.compose.ui.graphics.Color(0xFFDCEAF0),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    surfaceMuted = androidx.compose.ui.graphics.Color(0xFFF0F7FA),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF0F172A),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF334155),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFF7FBFD),
                    tileBackgroundMuted = androidx.compose.ui.graphics.Color(0xFFEAF4F8),
                    accent = androidx.compose.ui.graphics.Color(0xFF2563EB),
                    accentMuted = androidx.compose.ui.graphics.Color(0xFFD9E7FF),
                    outline = androidx.compose.ui.graphics.Color(0xFFD6E3EA),
                    statusCard = androidx.compose.ui.graphics.Color(0xFFF0F6F8),
                    successColor = androidx.compose.ui.graphics.Color(0xFF2F855A),
                ),
                tileStyle = base.tileStyle.copy(elevationDp = 2f, shadowAlpha = 0.05f, glowAlpha = 0.08f),
            )
            VisualTheme.SOFT_CALM -> base.copy(
                colors = base.colors.copy(
                    background = androidx.compose.ui.graphics.Color(0xFFF7F1EE),
                    backgroundAccent = androidx.compose.ui.graphics.Color(0xFFEAD8D1),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFA),
                    surfaceMuted = androidx.compose.ui.graphics.Color(0xFFF4E9E4),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF2F2730),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF665D67),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFBFA),
                    tileBackgroundMuted = androidx.compose.ui.graphics.Color(0xFFF6ECE8),
                    accent = androidx.compose.ui.graphics.Color(0xFF9C6B5C),
                    accentMuted = androidx.compose.ui.graphics.Color(0xFFF2E0D9),
                    outline = androidx.compose.ui.graphics.Color(0xFFE1CFC8),
                    statusCard = androidx.compose.ui.graphics.Color(0xFFF4E8E3),
                    successColor = androidx.compose.ui.graphics.Color(0xFF537A5A),
                ),
            )
        }

        val withLayout = when (config.layoutMode) {
            LayoutMode.SIMPLE_CLASSIC -> visual
            LayoutMode.VERY_SIMPLE -> visual.copy(
                layout = visual.layout.copy(tileSizeScale = 1.12f, showLabels = false, emphasisMode = EmphasisMode.BALANCED),
                typography = visual.typography.copy(
                    displaySize = visual.typography.displaySize * 1.05f,
                    headingSize = visual.typography.headingSize * 1.08f,
                    bodySize = visual.typography.bodySize * 1.08f,
                ),
                spacing = visual.spacing.copy(
                    sectionSpacing = visual.spacing.sectionSpacing + 2.dp,
                    tileSpacing = visual.spacing.tileSpacing + 4.dp,
                ),
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
                    backgroundAccent = androidx.compose.ui.graphics.Color(0xFFE5E5E5),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    surfaceMuted = androidx.compose.ui.graphics.Color(0xFFF2F2F2),
                    primaryText = androidx.compose.ui.graphics.Color(0xFF000000),
                    secondaryText = androidx.compose.ui.graphics.Color(0xFF111111),
                    tileBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                    tileBackgroundMuted = androidx.compose.ui.graphics.Color(0xFFF2F2F2),
                    accent = androidx.compose.ui.graphics.Color(0xFF0033CC),
                    accentMuted = androidx.compose.ui.graphics.Color(0xFFDCE6FF),
                    outline = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
                    statusCard = androidx.compose.ui.graphics.Color(0xFFF4F4F4),
                    sosColor = androidx.compose.ui.graphics.Color(0xFFB00020),
                    successColor = androidx.compose.ui.graphics.Color(0xFF006B3C),
                ),
                tileStyle = withLayout.tileStyle.copy(borderWidthDp = 2f),
            )
            AccessibilityMode.BOLD_ACCESSIBILITY -> withLayout.copy(
                typography = withLayout.typography.copy(
                    displaySize = withLayout.typography.displaySize * 1.1f,
                    headingSize = withLayout.typography.headingSize * 1.14f,
                    titleSize = withLayout.typography.titleSize * 1.12f,
                    bodySize = withLayout.typography.bodySize * 1.16f,
                    labelSize = withLayout.typography.labelSize * 1.16f,
                    supportSize = withLayout.typography.supportSize * 1.12f,
                    fontWeight = FontWeight.Bold,
                ),
                spacing = withLayout.spacing.copy(
                    padding = withLayout.spacing.padding + 2.dp,
                    sectionSpacing = withLayout.spacing.sectionSpacing + 2.dp,
                    tileSpacing = withLayout.spacing.tileSpacing + 2.dp,
                ),
            )
        }
    }
}
