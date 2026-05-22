package com.easyui.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.easyui.core.ui.theme.EasyUiSpacing
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing

@Composable
fun WizardShell(
    title: String,
    subtitle: String,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    nextLabel: String = "Next",
    isNextEnabled: Boolean = true,
    showProgress: Boolean = true,
    currentStep: Int = 1,
    totalSteps: Int = 10,
    scrollMode: WizardScrollMode = WizardScrollMode.ParentScroll,
    content: @Composable (androidx.compose.foundation.layout.ColumnScope.() -> Unit)
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                tonalElevation = NavigationBarDefaults.Elevation,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = EasyUiSpacing.md, vertical = EasyUiSpacing.sm)
                        .padding(bottom = safeDrawingPadding.calculateBottomPadding()),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
                ) {
                    if (showProgress) {
                        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                            LinearProgressIndicator(
                                progress = { (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Text(
                                text = "Step $currentStep of $totalSteps",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)
                    ) {
                        if (onBack != null) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back")
                            }
                        }
                        if (onSkip != null) {
                            TextButton(
                                onClick = onSkip,
                                modifier = if (onBack == null) Modifier.weight(1f) else Modifier
                            ) {
                                Text("Skip")
                            }
                        }
                        Button(
                            onClick = onNext,
                            enabled = isNextEnabled,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(nextLabel)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = safeDrawingPadding.calculateTopPadding())
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = EasyUiSpacing.lg,
                        end = EasyUiSpacing.lg,
                        top = EasyUiSpacing.md,
                        bottom = EasyUiSpacing.md
                    )
            ) {
                Text(title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(EasyUiSpacing.xs))
                Text(subtitle, style = MaterialTheme.typography.bodyLarge)
            }
            
            val contentModifier = if (scrollMode == WizardScrollMode.ParentScroll) {
                Modifier.verticalScroll(rememberScrollState())
            } else {
                Modifier
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .then(contentModifier)
                    .padding(
                        start = EasyUiSpacing.lg,
                        end = EasyUiSpacing.lg,
                        bottom = EasyUiSpacing.xl // Extra bottom padding to avoid cutting off last card
                    ),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
                content = content
            )
        }
    }
}
