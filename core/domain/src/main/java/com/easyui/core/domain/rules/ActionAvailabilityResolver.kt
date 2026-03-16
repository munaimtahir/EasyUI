package com.easyui.core.domain.rules

import com.easyui.core.domain.model.LauncherActionState

object ActionAvailabilityResolver {
    fun flashlight(isSupported: Boolean): LauncherActionState =
        if (isSupported) {
            LauncherActionState(enabled = true)
        } else {
            LauncherActionState(enabled = false, fallbackMessage = "Flashlight is not available on this device.")
        }

    fun dialer(hasDialer: Boolean): LauncherActionState =
        if (hasDialer) {
            LauncherActionState(enabled = true)
        } else {
            LauncherActionState(enabled = false, fallbackMessage = "No dialer app is available on this device.")
        }

    fun emergency(hasDialer: Boolean, phoneNumber: String): LauncherActionState =
        if (!hasDialer) {
            LauncherActionState(enabled = false, fallbackMessage = "No dialer app is available on this device.")
        } else if (phoneNumber.isBlank()) {
            LauncherActionState(enabled = false, fallbackMessage = "No emergency number is configured yet.")
        } else {
            LauncherActionState(enabled = true)
        }
}
