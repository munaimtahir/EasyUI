package com.easyui.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.easyui.core.domain.model.GuardianCheckResult
import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.LinkedDevice
import com.easyui.core.domain.model.PhoneHealthState
import com.easyui.core.domain.model.RemoteStatusPacket
import com.easyui.core.domain.model.SetupCompleteness
import com.easyui.core.domain.model.SetupCompletenessItem
import com.easyui.core.domain.repository.RemoteLinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class DataStoreRemoteLinkRepository(
    context: Context,
) : RemoteLinkRepository {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("remote_links") }
    )

    private object Keys {
        val LINKED_DEVICES = stringPreferencesKey("linked_devices")
    }

    override fun observeLinkedDevices(): Flow<List<LinkedDevice>> =
        dataStore.data.map { preferences ->
            val jsonStr = preferences[Keys.LINKED_DEVICES] ?: "[]"
            decodeDevices(jsonStr)
        }

    override suspend fun addOrUpdateDevice(deviceName: String, packet: RemoteStatusPacket) {
        dataStore.edit { preferences ->
            val currentJson = preferences[Keys.LINKED_DEVICES] ?: "[]"
            val devices = decodeDevices(currentJson).toMutableList()
            
            val deviceId = deviceName.lowercase().replace(" ", "_")
            val existingIndex = devices.indexOfFirst { it.id == deviceId }
            
            val newDevice = LinkedDevice(
                id = deviceId,
                name = deviceName,
                lastReceivedPacket = packet,
                lastReceivedAt = System.currentTimeMillis()
            )
            
            if (existingIndex >= 0) {
                devices[existingIndex] = newDevice
            } else {
                devices.add(newDevice)
            }
            
            preferences[Keys.LINKED_DEVICES] = encodeDevices(devices)
        }
    }

    override suspend fun removeDevice(deviceId: String) {
        dataStore.edit { preferences ->
            val currentJson = preferences[Keys.LINKED_DEVICES] ?: "[]"
            val devices = decodeDevices(currentJson).filterNot { it.id == deviceId }
            preferences[Keys.LINKED_DEVICES] = encodeDevices(devices)
        }
    }

    private fun encodeDevices(devices: List<LinkedDevice>): String {
        val array = JSONArray()
        devices.forEach { device ->
            array.put(JSONObject().apply {
                put("id", device.id)
                put("n", device.name)
                put("t", device.lastReceivedAt)
                device.lastReceivedPacket?.let { packet ->
                    put("p", encodePacket(packet))
                }
            })
        }
        return array.toString()
    }

    private fun decodeDevices(jsonStr: String): List<LinkedDevice> {
        val array = JSONArray(jsonStr)
        val devices = mutableListOf<LinkedDevice>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            devices.add(LinkedDevice(
                id = obj.getString("id"),
                name = obj.getString("n"),
                lastReceivedAt = if (obj.has("t")) obj.getLong("t") else null,
                lastReceivedPacket = if (obj.has("p")) decodePacket(obj.getJSONObject("p")) else null
            ))
        }
        return devices
    }

    private fun encodePacket(packet: RemoteStatusPacket): JSONObject {
        return JSONObject().apply {
            put("v", packet.version)
            put("t", packet.timestamp)
            put("n", packet.deviceName)
            put("h", encodeHealthState(packet.healthState))
            put("s", encodeSetupCompleteness(packet.setupCompleteness))
        }
    }

    private fun decodePacket(json: JSONObject): RemoteStatusPacket {
        return RemoteStatusPacket(
            version = json.getInt("v"),
            timestamp = json.getLong("t"),
            deviceName = json.getString("n"),
            healthState = decodeHealthState(json.getJSONObject("h")),
            setupCompleteness = decodeSetupCompleteness(json.getJSONObject("s"))
        )
    }

    private fun encodeHealthState(state: PhoneHealthState): JSONObject {
        return JSONObject().apply {
            put("s", state.overallStatus.name)
            put("m", state.primaryMessage)
            val checksArray = JSONArray()
            state.checks.forEach { check ->
                checksArray.put(JSONObject().apply {
                    put("t", check.type.name)
                    put("s", check.status.name)
                    put("m", check.message)
                })
            }
            put("c", checksArray)
        }
    }

    private fun decodeHealthState(json: JSONObject): PhoneHealthState {
        val checksArray = json.getJSONArray("c")
        val checks = mutableListOf<GuardianCheckResult>()
        for (i in 0 until checksArray.length()) {
            val obj = checksArray.getJSONObject(i)
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.valueOf(obj.getString("t")),
                    status = GuardianCheckStatus.valueOf(obj.getString("s")),
                    message = obj.getString("m")
                )
            )
        }
        return PhoneHealthState(
            checks = checks,
            overallStatus = GuardianCheckStatus.valueOf(json.getString("s")),
            primaryMessage = json.getString("m")
        )
    }

    private fun encodeSetupCompleteness(setup: SetupCompleteness): JSONObject {
        return JSONObject().apply {
            put("sc", setup.score)
            val itemsArray = JSONArray()
            setup.items.forEach { item ->
                itemsArray.put(JSONObject().apply {
                    put("id", item.id)
                    put("l", item.label)
                    put("c", item.isComplete)
                    put("r", item.isRequired)
                })
            }
            put("i", itemsArray)
        }
    }

    private fun decodeSetupCompleteness(json: JSONObject): SetupCompleteness {
        val itemsArray = json.getJSONArray("i")
        val items = mutableListOf<SetupCompletenessItem>()
        for (i in 0 until itemsArray.length()) {
            val obj = itemsArray.getJSONObject(i)
            items.add(
                SetupCompletenessItem(
                    id = obj.getString("id"),
                    label = obj.getString("l"),
                    isComplete = obj.getBoolean("c"),
                    isRequired = obj.getBoolean("r")
                )
            )
        }
        return SetupCompleteness(
            items = items,
            score = json.getDouble("sc").toFloat()
        )
    }
}
