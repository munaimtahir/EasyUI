package com.easyui.feature.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun IntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("intro_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("EasyUI Senior Launcher", style = MaterialTheme.typography.headlineLarge)
            Text(
                "A simpler home screen for seniors. Large buttons, clear labels, and a calmer setup for caregivers.",
                style = MaterialTheme.typography.bodyLarge,
            )
            TrustBullet(
                icon = Icons.Outlined.PhoneAndroid,
                title = "Minimal by default",
                body = "Only the most important actions stay visible so daily use feels calm and predictable.",
            )
            TrustBullet(
                icon = Icons.Outlined.CloudOff,
                title = "Offline and device-first",
                body = "EasyUI works without an account. Your setup stays on this phone unless you export it.",
            )
            Text(
                "This app does not lock the phone down. It gives you a clearer home screen and an easier setup path while keeping ownership of setup data on the device.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Start Setup")
            }
        }
    }
}

@Composable
fun DefaultLauncherGuidanceScreen(
    isDefaultLauncher: Boolean,
    onOpenSettings: () -> Unit,
    onRefreshStatus: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("default_launcher_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Set EasyUI as Home", style = MaterialTheme.typography.headlineLarge)
            Text(
                if (isDefaultLauncher) {
                    "EasyUI is already set as the home launcher on this device."
                } else {
                    "Open your phone's default apps or home settings and choose EasyUI Senior Launcher as the home app."
                },
                style = MaterialTheme.typography.bodyLarge,
            )
            if (!isDefaultLauncher) {
                Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                    Text("Open Default App Settings")
                }
                OutlinedButton(onClick = onRefreshStatus, modifier = Modifier.fillMaxWidth()) {
                    Text("Check Again")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text(if (isDefaultLauncher) "Continue" else "Continue Anyway")
            }
        }
    }
}

@Composable
fun CaregiverHelpScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .navigationBarsPadding()
                .testTag("caregiver_help_screen"),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            ) {
                Text("Caregiver Help", style = MaterialTheme.typography.headlineLarge)
                TrustBullet(
                    icon = Icons.Outlined.Lock,
                    title = "Local protection only",
                    body = "Caregiver PIN and launcher changes stay in EasyUI. They do not take ownership of Android itself.",
                )
                Text(
                    "Keep the home screen simple. Choose EasyUI as the default launcher, then hand the phone over with only the most important apps visible.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    "To re-enter caregiver settings later, long-press the top status bar or use the clock-tap fallback. If PIN protection is on, the caregiver PIN is required.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Home shows the fixed essentials first, and All Apps stays available as a separate screen. Hidden Apps are only hidden inside EasyUI, not Android system-wide.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "EasyUI simplifies this launcher only. It does not lock down Android settings or other apps outside EasyUI, and it does not send setup data away from the device in normal use.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(EasyUiSpacing.sm))
            }
            Spacer(modifier = Modifier.height(EasyUiSpacing.md))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
        }
    }
}

@Composable
private fun TrustBullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
