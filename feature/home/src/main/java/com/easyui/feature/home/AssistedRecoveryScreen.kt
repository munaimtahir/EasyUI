package com.easyui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.RecoveryGuidance
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun AssistedRecoveryScreen(
    guidance: RecoveryGuidance,
    onExecuteAction: () -> Unit,
    onAlertCaregiver: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = SeniorHomeTokens.backgroundBottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SeniorHomeTokens.backgroundBrush)
                .testTag("assisted_recovery_screen"),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(EasyUiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Help & Support",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SeniorHomeTokens.textPrimary,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
                    colors = CardDefaults.cardColors(containerColor = SeniorHomeTokens.headerBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = SeniorHomeTokens.tileEmergency,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = guidance.label,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = SeniorHomeTokens.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = guidance.description,
                            fontSize = 20.sp,
                            color = SeniorHomeTokens.textSecondaryOnHeader,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }

                val actionLabel = guidance.actionButtonLabel
                if (actionLabel != null) {
                    Button(
                        onClick = onExecuteAction,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = actionLabel,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = onAlertCaregiver,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SeniorHomeTokens.tileEmergency)
                ) {
                    Text(
                        text = "Alert Caregiver",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Back to Home",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
