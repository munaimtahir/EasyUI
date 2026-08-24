package com.easyui.senior.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.senior.storage.coreDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun EmergencyScreen(
    onBack: () -> Unit,
    onSosTriggered: suspend () -> Boolean // Will notify caregiver (if paired); returns true on success
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val contactNameKey = stringPreferencesKey("sos_name_1")
    val contactPhoneKey = stringPreferencesKey("sos_phone_1")
    val contactName2Key = stringPreferencesKey("sos_name_2")
    val contactPhone2Key = stringPreferencesKey("sos_phone_2")

    val sosNameFlow = remember(context) { context.coreDataStore.data.map { it[contactNameKey] ?: "Caregiver" } }
    val sosPhoneFlow = remember(context) { context.coreDataStore.data.map { it[contactPhoneKey] ?: "911" } }
    val sosName2Flow = remember(context) { context.coreDataStore.data.map { it[contactName2Key] ?: "Emergency Services" } }
    val sosPhone2Flow = remember(context) { context.coreDataStore.data.map { it[contactPhone2Key] ?: "112" } }

    val name1 by sosNameFlow.collectAsState(initial = "Caregiver")
    val phone1 by sosPhoneFlow.collectAsState(initial = "911")
    val name2 by sosName2Flow.collectAsState(initial = "Emergency Services")
    val phone2 by sosPhone2Flow.collectAsState(initial = "112")

    // SOS press progress state (from 0.0f to 1.0f)
    var pressProgress by remember { mutableStateOf(0f) }
    var isPressing by remember { mutableStateOf(false) }
    var alertFailed by remember { mutableStateOf(false) }
    var callFailed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressing) {
        if (isPressing) {
            val startTime = System.currentTimeMillis()
            val duration = 2000L // 2 seconds hold
            while (isPressing && pressProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                pressProgress = (elapsed.toFloat() / duration).coerceAtMost(1f)
                delay(30)
            }
            if (pressProgress >= 1f) {
                // SOS triggered!
                alertFailed = !onSosTriggered()
                callFailed = !triggerCall(context, phone1)
                pressProgress = 0f
                isPressing = false
            }
        } else {
            // Cool down quickly
            while (pressProgress > 0f) {
                pressProgress = (pressProgress - 0.1f).coerceAtLeast(0f)
                delay(30)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8B0000)) // Dark deep red for emergency context
            .padding(24.dp)
            .testTag("emergency_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EMERGENCY / SOS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("emergency_title")
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                modifier = Modifier.testTag("emergency_back")
            ) {
                Text("Back", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Press and hold the button for 2 seconds to alert your caregiver and call SOS",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (alertFailed || callFailed) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when {
                    alertFailed && callFailed -> "Could not notify your caregiver or open the phone dialer. Try again or call your emergency contact directly."
                    alertFailed -> "Could not notify your caregiver. Try again, or call your emergency contact directly."
                    else -> "Could not open the phone dialer. Try again, or call your emergency contact directly."
                },
                color = Color.Yellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("sos_failure_warning")
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // SOS HOLD BUTTON
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.Red)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressing = true
                            tryAwaitRelease()
                            isPressing = false
                        }
                    )
                }
                .testTag("sos_hold_button"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { pressProgress },
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 12.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "HOLD",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "SOS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Emergency Contacts info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Emergency Contacts",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            EmergencyContactRow(name = name1, phone = phone1, onCall = { triggerCall(context, phone1) })
            Spacer(modifier = Modifier.height(12.dp))
            EmergencyContactRow(name = name2, phone = phone2, onCall = { triggerCall(context, phone2) })
        }
    }
}

@Composable
private fun EmergencyContactRow(
    name: String,
    phone: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable { onCall() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(phone, color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "CALL",
            color = Color.Yellow,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun triggerCall(context: Context, phone: String): Boolean {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return try {
        context.startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}
