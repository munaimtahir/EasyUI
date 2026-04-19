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
    content: @Composable (androidx.compose.foundation.layout.ColumnScope.() -> Unit)
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                tonalElevation = NavigationBarDefaults.Elevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(EasyUiSpacing.md)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
                ) {
                    if (showProgress) {
                        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                            LinearProgressIndicator(
                                progress = { (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth()
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
                .padding(innerPadding)
                .padding(EasyUiSpacing.lg)
        ) {
            Text(title, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(EasyUiSpacing.xs))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(EasyUiSpacing.lg))
            content()
        }
    }
}
