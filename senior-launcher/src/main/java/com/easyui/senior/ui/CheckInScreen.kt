package com.easyui.senior.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.senior.network.BackendClient
import com.easyui.senior.network.PairingManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CheckInScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pairingManager = remember { PairingManager(context) }

    var isCheckingIn by remember { mutableStateOf(false) }
    var lastCheckInTime by remember { mutableStateOf<Long?>(null) }
    var checkInSuccess by remember { mutableStateOf<Boolean?>(null) }
    var isPaired by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val state = pairingManager.getState()
        isPaired = state.isPaired
        if (state.isPaired) {
            BackendClient.deviceToken = state.deviceToken
        }
    }

    val buttonColor by animateColorAsState(
        targetValue = when (checkInSuccess) {
            true -> MaterialTheme.colorScheme.tertiary
            false -> MaterialTheme.colorScheme.error
            null -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(500),
        label = "checkInButtonColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("checkin_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Check-In",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.testTag("checkin_back")
            ) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Large friendly greeting
        Text(
            text = "Let your caregiver know you're OK",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Big I'm OK button
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(buttonColor)
                .clickable(enabled = !isCheckingIn && isPaired) {
                    scope.launch {
                        isCheckingIn = true
                        checkInSuccess = null
                        val success = BackendClient.postCheckIn("I'm OK")
                        checkInSuccess = success
                        if (success) {
                            lastCheckInTime = System.currentTimeMillis()
                        }
                        isCheckingIn = false
                    }
                }
                .testTag("im_ok_button"),
            contentAlignment = Alignment.Center
        ) {
            if (isCheckingIn) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (checkInSuccess) {
                            true -> "✓"
                            false -> "✗"
                            null -> "👋"
                        },
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (checkInSuccess) {
                            true -> "Sent!"
                            false -> "Failed"
                            null -> "I'm OK"
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!isPaired) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "No caregiver connected. Go to Privacy & Trust to connect a caregiver before using check-ins.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            lastCheckInTime?.let { ts ->
                val fmt = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
                Text(
                    text = "Last check-in sent: ${fmt.format(Date(ts))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("last_checkin_time")
                )
            }

            if (checkInSuccess == false) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Could not reach the server. Check your internet connection.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
