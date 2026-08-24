package com.easyui.senior.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.TextUnit
import com.easyui.senior.theme.TextSize
import com.easyui.senior.theme.ThemeSettings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Regression coverage for the text-scale drift: CoreTheme used to hardcode its own
 * Large/Larger scale factors (1.18x/1.32x) instead of reading TextSize.scaleFactor
 * (1.20x/1.40x), so the accessibility settings UI could claim a larger text scale than
 * what was actually rendered.
 */
class CoreThemeTest {

    @get:Rule
    val compose = createComposeRule()

    private fun renderedBodyLargeSize(textSize: TextSize): TextUnit {
        var size: TextUnit = TextUnit.Unspecified
        compose.setContent {
            CoreTheme(settings = ThemeSettings(textSize = textSize)) {
                size = MaterialTheme.typography.bodyLarge.fontSize
                Text("probe")
            }
        }
        compose.waitForIdle()
        return size
    }

    @Test
    fun typographyScale_matchesTextSizeScaleFactor_forEveryOption() {
        val normalSize = renderedBodyLargeSize(TextSize.Normal)

        for (option in TextSize.entries) {
            if (option == TextSize.Normal) continue
            val actual = renderedBodyLargeSize(option)
            val expected = normalSize.value * option.scaleFactor
            assertEquals(
                "Rendered scale for $option should match TextSize.scaleFactor",
                expected,
                actual.value,
                0.01f
            )
        }
    }
}
