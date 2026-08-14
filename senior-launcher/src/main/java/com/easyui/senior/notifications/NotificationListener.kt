package com.easyui.senior.notifications

import android.app.Notification
import android.app.PendingIntent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationListener : NotificationListenerService() {

    data class ActiveNotification(
        val key: String,
        val packageName: String,
        val appName: String,
        val title: String,
        val text: String,
        val postTime: Long,
        val pendingIntent: PendingIntent?
    )

    companion object {
        private val _notifications = MutableStateFlow<List<ActiveNotification>>(emptyList())
        val notifications: StateFlow<List<ActiveNotification>> = _notifications

        var instance: NotificationListener? = null
            private set
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        updateNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        updateNotifications()
    }

    fun dismiss(key: String) {
        try {
            cancelNotification(key)
        } catch (e: Exception) {
            // Safe fallback
        }
        updateNotifications()
    }

    fun click(key: String) {
        val sbn = activeNotifications.firstOrNull { it.key == key }
        sbn?.let {
            try {
                it.notification.contentIntent?.send()
                cancelNotification(key) // Optional: dismiss after clicking
            } catch (e: Exception) {
                // Safe fallback
            }
        }
        updateNotifications()
    }

    private fun updateNotifications() {
        try {
            val active = activeNotifications ?: return
            val list = active.mapNotNull { sbn ->
                val extras = sbn.notification.extras
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                
                // Skip empty or system notifications that are persistent
                if (title.isEmpty() && text.isEmpty()) return@mapNotNull null
                if ((sbn.notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) return@mapNotNull null

                val pm = packageManager
                val appName = try {
                    val ai = pm.getApplicationInfo(sbn.packageName, 0)
                    pm.getApplicationLabel(ai).toString()
                } catch (e: Exception) {
                    sbn.packageName
                }

                ActiveNotification(
                    key = sbn.key,
                    packageName = sbn.packageName,
                    appName = appName,
                    title = title,
                    text = text,
                    postTime = sbn.postTime,
                    pendingIntent = sbn.notification.contentIntent
                )
            }
            _notifications.value = list
        } catch (e: Exception) {
            // Keep existing state on error
        }
    }
}
