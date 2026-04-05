package com.easyui.launcher

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.SemanticsMatcher

fun SemanticsNodeInteraction.assertPresent(): SemanticsNodeInteraction =
    assert(SemanticsMatcher("node exists") { true })
