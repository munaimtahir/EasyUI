package com.easyui.launcher.app.caregiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.LinkedDevice
import com.easyui.core.domain.model.RemoteStatusPacket
import com.easyui.core.domain.rules.RemoteLinkRules
import com.easyui.launcher.di.AppContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemoteLinkViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val messages = MutableSharedFlow<String>()

    val linkedDevices: StateFlow<List<LinkedDevice>> =
        container.remoteLinkRepository.observeLinkedDevices()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun generateShareLink(packet: RemoteStatusPacket): String {
        val encoded = RemoteLinkRules.encodePacket(packet)
        return "easyui://status?d=$encoded"
    }

    fun importStatusFromDeepLink(deepLink: String) {
        viewModelScope.launch {
            val encoded = extractData(deepLink)
            if (encoded != null) {
                val packet = RemoteLinkRules.decodePacket(encoded)
                if (packet != null) {
                    container.remoteLinkRepository.addOrUpdateDevice(packet.deviceName, packet)
                    messages.emit("Linked device '${packet.deviceName}' updated.")
                } else {
                    messages.emit("Invalid status link.")
                }
            } else {
                messages.emit("Could not read link data.")
            }
        }
    }

    fun removeDevice(deviceId: String) {
        viewModelScope.launch {
            container.remoteLinkRepository.removeDevice(deviceId)
            messages.emit("Device removed.")
        }
    }

    private fun extractData(deepLink: String): String? {
        return try {
            val uri = android.net.Uri.parse(deepLink)
            uri.getQueryParameter("d")
        } catch (e: Exception) {
            null
        }
    }
}
