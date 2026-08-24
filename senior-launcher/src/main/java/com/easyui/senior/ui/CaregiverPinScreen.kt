package com.easyui.senior.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.easyui.senior.storage.CaregiverRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [Auto] verifies the existing PIN (or sets one if none exists) and calls [CaregiverPinScreen.onSuccess].
 * [ChangePin] always requires the current PIN to be re-entered first (when one is set) before letting
 * the user choose a new one, so a PIN can never be replaced without proving the old one.
 */
enum class CaregiverPinMode {
    Auto,
    ChangePin
}

@Composable
fun CaregiverPinScreen(
    caregiverRepo: CaregiverRepository,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    mode: CaregiverPinMode = CaregiverPinMode.Auto
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by caregiverRepo.stateFlow.collectAsState(initial = null)

    var pinInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    // ChangePin mode: true once the user has re-entered their existing PIN correctly.
    var oldPinVerified by remember { mutableStateOf(false) }

    // Lockout countdown state
    var remainingLockoutSeconds by remember { mutableStateOf(0L) }

    LaunchedEffect(state) {
        state?.let {
            val now = System.currentTimeMillis()
            if (it.lockoutUntil > now) {
                remainingLockoutSeconds = (it.lockoutUntil - now) / 1000L
            }
        }
    }

    LaunchedEffect(remainingLockoutSeconds) {
        if (remainingLockoutSeconds > 0) {
            delay(1000L)
            remainingLockoutSeconds--
            if (remainingLockoutSeconds == 0L) {
                caregiverRepo.resetPinAttempts()
                errorText = ""
            }
        }
    }

    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isPinSet = state!!.isPinSet
    // Whether we're currently collecting a brand-new PIN (vs. verifying an existing one).
    val settingNewPin = if (mode == CaregiverPinMode.ChangePin) (!isPinSet || oldPinVerified) else !isPinSet

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("caregiver_pin_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Caregiver Lock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                settingNewPin && isConfirming -> "Confirm your new 4-digit Caregiver PIN"
                settingNewPin -> "Set up a new 4-digit Caregiver PIN"
                mode == CaregiverPinMode.ChangePin -> "Enter your current PIN to continue"
                else -> "Enter Caregiver PIN to access settings"
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bullet representation of entered PIN digits
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayInput = if (isConfirming) confirmPinInput else pinInput
            for (i in 0 until 4) {
                val filled = i < displayInput.length
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (remainingLockoutSeconds > 0) {
            Text(
                text = "Locked out. Try again in $remainingLockoutSeconds seconds.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("pin_lockout_message")
            )
        } else if (errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.testTag("pin_error_message")
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Giant Numpad Grid
        if (remainingLockoutSeconds <= 0) {
            val onKeyClick: (String) -> Unit = { key ->
                val currentInput = if (isConfirming) confirmPinInput else pinInput
                if (currentInput.length < 4) {
                    val newInput = currentInput + key
                    if (isConfirming) {
                        confirmPinInput = newInput
                    } else {
                        pinInput = newInput
                    }

                    if (newInput.length == 4) {
                        scope.launch {
                            if (settingNewPin) {
                                if (!isConfirming) {
                                    isConfirming = true
                                } else {
                                    if (pinInput == confirmPinInput) {
                                        caregiverRepo.setPin(pinInput)
                                        onSuccess()
                                    } else {
                                        errorText = "PINs do not match. Try again."
                                        pinInput = ""
                                        confirmPinInput = ""
                                        isConfirming = false
                                    }
                                }
                            } else {
                                val success = caregiverRepo.verifyPin(newInput)
                                if (success) {
                                    if (mode == CaregiverPinMode.ChangePin) {
                                        // Old PIN confirmed — move on to collecting the new one.
                                        // No PIN is cleared here; setPin() below atomically
                                        // replaces the old PIN only once a new one is chosen.
                                        oldPinVerified = true
                                        pinInput = ""
                                        errorText = ""
                                    } else {
                                        onSuccess()
                                    }
                                } else {
                                    errorText = "Incorrect PIN."
                                    pinInput = ""
                                }
                            }
                        }
                    }
                }
            }

            val onDeleteClick: () -> Unit = {
                if (isConfirming) {
                    if (confirmPinInput.isNotEmpty()) {
                        confirmPinInput = confirmPinInput.dropLast(1)
                    }
                } else {
                    if (pinInput.isNotEmpty()) {
                        pinInput = pinInput.dropLast(1)
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rowModifier = Modifier.fillMaxWidth(0.85f)
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("Cancel", "0", "Del")
                )

                for (row in rows) {
                    Row(
                        modifier = rowModifier,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (key in row) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.2f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        when (key) {
                                            "Cancel", "Del" -> MaterialTheme.colorScheme.surfaceVariant
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                    .clickable {
                                        when (key) {
                                            "Cancel" -> onCancel()
                                            "Del" -> onDeleteClick()
                                            else -> onKeyClick(key)
                                        }
                                    }
                                    .testTag("pin_key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (key) {
                                        "Cancel", "Del" -> MaterialTheme.colorScheme.onSurfaceVariant
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Lockout state cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
                    .testTag("pin_lockout_cancel")
            ) {
                Text("Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
