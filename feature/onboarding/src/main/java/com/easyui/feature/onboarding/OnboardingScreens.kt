package com.easyui.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
            Text(
                "This app works offline and does not lock the phone down. It gives you a clearer home screen and an easier setup path.",
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
                .testTag("caregiver_help_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Caregiver Help", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Keep the home screen simple. Choose EasyUI as the default launcher, then hand the phone over with only the most important apps visible.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "If something changes later, you can reopen EasyUI and use the All Apps list to find anything that is not on the home screen.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "This first version keeps one starter layout only. There are no folders, widgets, or advanced controls yet.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(EasyUiSpacing.md))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
        }
    }
}
