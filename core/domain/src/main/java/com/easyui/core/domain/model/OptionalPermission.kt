package com.easyui.core.domain.model

/**
 * Optional, user-controllable permission preferences shown during guided setup.
 *
 * These represent caregiver intent (what features they want enabled), not the actual Android
 * runtime permission grant state.
 */
enum class OptionalPermission {
    PHONE_DIALER,
    CONTACTS,
    CAMERA,
    PHOTOS_MEDIA,
    BACKUP_RESTORE_FILES,
    NOTIFICATIONS,
}

