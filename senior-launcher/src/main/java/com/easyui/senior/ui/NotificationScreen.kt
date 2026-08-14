package com.easyui.senior.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.senior.notifications.NotificationListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isAccessGranted by remember { mutableStateOf(false) }

    fun checkAccess() {
        val cn = android.content.ComponentName(context, NotificationListener::class.java)
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        isAccessGranted = flat != null && flat.contains(cn.flattenToString())
    }

    LaunchedEffect(Unit) {
        checkAccess()
    }

    // Polling access check briefly when resuming screen
    LaunchedEffect(isAccessGranted) {
        if (!isAccessGranted) {
            while (true) {
                checkAccess()
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    val activeNotifications by NotificationListener.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("notification_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp)
                    .testTag("notification_back"),
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isAccessGranted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Notification access is required to display your messages here.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .testTag("grant_notification_access")
                ) {
                    Text("Grant Permission", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (activeNotifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("notification_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeNotifications, key = { it.key }) { notif ->
                    NotificationRow(
                        notif = notif,
                        onOpen = {
                            NotificationListener.instance?.click(notif.key)
                        },
                        onDismiss = {
                            NotificationListener.instance?.dismiss(notif.key)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notif: NotificationListener.ActiveNotification,
    onOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    val timeStr = remember(notif.postTime) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(notif.postTime))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
            .testTag("notification_item_${notif.packageName}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notif.appName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = notif.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = notif.text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onOpen,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("notification_open_${notif.packageName}"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Open", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("notification_dismiss_${notif.packageName}")
            ) {
                Text("Dismiss", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
