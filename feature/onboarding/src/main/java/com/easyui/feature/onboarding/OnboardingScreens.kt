package com.easyui.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun IntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SetupScene(
        modifier = modifier.testTag("intro_screen"),
        eyebrow = "Setup that feels like EasyUI",
        title = "EasyUI Senior Launcher",
        subtitle = "A calmer phone start with large colorful actions, clearer labels, and a setup path caregivers can finish with confidence.",
        badges = listOf("Offline-first", "Large targets", "Simple by default"),
        footer = "EasyUI simplifies the launcher experience. It does not lock down Android or take control of the device.",
        primaryActionLabel = "Start Setup",
        onPrimaryAction = onContinue,
        hero = {
            ColorPreviewRow(
                items = listOf(
                    PreviewTile("Phone", OnboardingTokens.setupCardGreen),
                    PreviewTile("Messages", OnboardingTokens.setupCardOrange),
                    PreviewTile("Photos", OnboardingTokens.setupCardBlue),
                ),
            )
        },
    ) {
        TrustBullet(
            icon = Icons.Outlined.PhoneAndroid,
            title = "Calm on day one",
            body = "The home screen starts with only the most important actions, using the same bright tile language seniors see every day.",
            accentColor = OnboardingTokens.setupCardBlue,
        )
        TrustBullet(
            icon = Icons.Outlined.CloudOff,
            title = "Stays on this phone",
            body = "Setup works without an account. Your settings remain local unless you export a backup yourself.",
            accentColor = OnboardingTokens.setupCardGreen,
        )
        TrustBullet(
            icon = Icons.Outlined.WifiOff,
            title = "Works offline",
            body = "The launcher stays useful without internet access, so the phone remains dependable in normal daily use.",
            accentColor = OnboardingTokens.setupCardPurple,
        )
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
    val statusTitle = if (isDefaultLauncher) "EasyUI is ready as Home" else "Set EasyUI as Home"
    val statusBody =
        if (isDefaultLauncher) {
            "This phone is already set to open EasyUI when Home is pressed."
        } else {
            "Open your phone's default apps or home settings and choose EasyUI Senior Launcher as the home app."
        }

    SetupScene(
        modifier = modifier.testTag("default_launcher_screen"),
        eyebrow = "First-time phone setup",
        title = statusTitle,
        subtitle = statusBody,
        badges = listOf(
            if (isDefaultLauncher) "Home selected" else "One device setting",
            "No account required",
            "Safe to check again",
        ),
        footer = if (isDefaultLauncher) {
            "You can continue with setup. EasyUI should now appear when the Home button or gesture is used."
        } else {
            "Some phones place this under Default Apps, Home App, or Launcher. If the screen looks different, keep the instruction honest and continue when you are done."
        },
        primaryActionLabel = if (isDefaultLauncher) "Continue" else "Continue Anyway",
        onPrimaryAction = onContinue,
        secondaryActionLabel = if (isDefaultLauncher) null else "Open Default App Settings",
        onSecondaryAction = if (isDefaultLauncher) null else onOpenSettings,
        tertiaryActionLabel = if (isDefaultLauncher) null else "Check Again",
        onTertiaryAction = if (isDefaultLauncher) null else onRefreshStatus,
        hero = {
            StatusHeroCard(
                icon = if (isDefaultLauncher) Icons.Outlined.CheckCircle else Icons.Outlined.PhoneAndroid,
                title = if (isDefaultLauncher) "Default launcher confirmed" else "One step outside EasyUI",
                body = if (isDefaultLauncher) {
                    "The colorful EasyUI home can now become the main landing place after setup."
                } else {
                    "Android keeps this choice in system settings. EasyUI explains the step clearly, but it does not force the device to switch launchers."
                },
                accentColor = if (isDefaultLauncher) OnboardingTokens.setupCardGreen else OnboardingTokens.setupCardOrange,
            )
        },
    ) {
        TrustBullet(
            icon = Icons.Outlined.PhoneAndroid,
            title = "What to look for",
            body = "Choose EasyUI Senior Launcher anywhere your phone asks which app should open Home.",
            accentColor = OnboardingTokens.setupCardBlue,
        )
        TrustBullet(
            icon = Icons.Outlined.CheckCircle,
            title = "Why this matters",
            body = "Without this step, seniors may return to the stock home screen and lose the simpler colorful layout you set up here.",
            accentColor = OnboardingTokens.setupCardGreen,
        )
    }
}

@Composable
fun CaregiverHelpScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SetupScene(
        modifier = modifier.testTag("caregiver_help_screen"),
        eyebrow = "Caregiver handoff",
        title = "Keep the phone simple",
        subtitle = "Finish setup with a stable, colorful home that is easy to understand and difficult to disturb by accident.",
        badges = listOf("Local protection", "Caregiver-only entry", "No fake lockdown claims"),
        footer = "EasyUI only simplifies this launcher. Android settings and other apps outside EasyUI still behave like a normal consumer phone.",
        primaryActionLabel = "Finish Setup",
        onPrimaryAction = onContinue,
        hero = {
            ColorPreviewRow(
                items = listOf(
                    PreviewTile("Contacts", OnboardingTokens.setupCardRed),
                    PreviewTile("Camera", OnboardingTokens.setupCardPurple),
                    PreviewTile("Emergency", Color(0xFFD92D20)),
                ),
            )
        },
    ) {
        TrustBullet(
            icon = Icons.Outlined.Lock,
            title = "Local protection only",
            body = "Caregiver PIN and layout protection stay inside EasyUI. They do not take ownership of Android itself.",
            accentColor = OnboardingTokens.setupCardPurple,
        )
        TrustBullet(
            icon = Icons.Outlined.CheckCircle,
            title = "Hidden access path",
            body = "Caregiver tools stay off the senior home screen. Re-entry later uses the deliberate top-bar long-press or the clock-tap fallback.",
            accentColor = OnboardingTokens.setupCardBlue,
        )
        TrustBullet(
            icon = Icons.Outlined.PhoneAndroid,
            title = "Stable first page",
            body = "The first home page keeps the fixed essentials visible first, with separate caregiver controls for additional apps and visibility choices.",
            accentColor = OnboardingTokens.setupCardOrange,
        )
    }
}

@Composable
private fun SetupScene(
    eyebrow: String,
    title: String,
    subtitle: String,
    badges: List<String>,
    footer: String,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    tertiaryActionLabel: String? = null,
    onTertiaryAction: (() -> Unit)? = null,
    hero: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingTokens.backgroundBrush),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = OnboardingTokens.pagePadding,
                    end = OnboardingTokens.pagePadding,
                    top = safeDrawingPadding.calculateTopPadding() + OnboardingTokens.bottomSpacing,
                    bottom = safeDrawingPadding.calculateBottomPadding() + OnboardingTokens.bottomSpacing,
                ),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(OnboardingTokens.sectionSpacing),
            ) {
                HeroCard(
                    eyebrow = eyebrow,
                    title = title,
                    subtitle = subtitle,
                    badges = badges,
                    hero = hero,
                )
                content()
                Text(
                    text = footer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnboardingTokens.screenTextMuted,
                )
                Spacer(modifier = Modifier.height(EasyUiSpacing.xs))
            }

            Spacer(modifier = Modifier.height(OnboardingTokens.bottomSpacing))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
            ) {
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_primary_action"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnboardingTokens.primaryButton,
                        contentColor = OnboardingTokens.primaryButtonText,
                    ),
                ) {
                    Text(primaryActionLabel, style = MaterialTheme.typography.titleLarge)
                }
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_secondary_action"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnboardingTokens.secondaryButtonText),
                        border = BorderStroke(1.dp, OnboardingTokens.secondaryButtonBorder),
                    ) {
                        Text(secondaryActionLabel, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (tertiaryActionLabel != null && onTertiaryAction != null) {
                    OutlinedButton(
                        onClick = onTertiaryAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_tertiary_action"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnboardingTokens.secondaryButtonText),
                        border = BorderStroke(1.dp, OnboardingTokens.secondaryButtonBorder),
                    ) {
                        Text(tertiaryActionLabel, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    eyebrow: String,
    title: String,
    subtitle: String,
    badges: List<String>,
    hero: @Composable (() -> Unit)?,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = OnboardingTokens.heroCard),
        shape = RoundedCornerShape(OnboardingTokens.cornerRadius),
        border = BorderStroke(1.dp, OnboardingTokens.heroCardOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(OnboardingTokens.heroSpacing),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (badges.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    badges.forEach { badge ->
                        SetupChip(label = badge)
                    }
                }
            }
            if (hero != null) {
                hero()
            }
        }
    }
}

@Composable
private fun SetupChip(label: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = OnboardingTokens.chipSurface),
        shape = RoundedCornerShape(OnboardingTokens.chipCornerRadius),
    ) {
        Text(
            text = label,
            color = OnboardingTokens.chipText,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun StatusHeroCard(
    icon: ImageVector,
    title: String,
    body: String,
    accentColor: Color,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = accentColor),
        shape = RoundedCornerShape(OnboardingTokens.cardCornerRadius),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.92f),
            )
        }
    }
}
}

private data class PreviewTile(
    val title: String,
    val color: Color,
)

@Composable
private fun ColorPreviewRow(items: List<PreviewTile>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(OnboardingTokens.miniTileHeight),
                colors = CardDefaults.cardColors(containerColor = item.color),
                shape = RoundedCornerShape(OnboardingTokens.cardCornerRadius),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(modifier = Modifier.width(1.dp))
                    Text(
                        text = item.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TrustBullet(
    icon: ImageVector,
    title: String,
    body: String,
    accentColor: Color,
) {
    Card(
        shape = RoundedCornerShape(OnboardingTokens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(EasyUiSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }
        }
    }
}
