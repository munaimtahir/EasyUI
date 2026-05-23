package com.easyui.core.domain.rules

import java.util.Base64
import com.easyui.core.domain.model.GuardianCheckResult
import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.PhoneHealthState
import com.easyui.core.domain.model.RemoteStatusPacket
import com.easyui.core.domain.model.SetupCompleteness
import com.easyui.core.domain.model.SetupCompletenessItem
import org.json.JSONArray
import org.json.JSONObject

object RemoteLinkRules {

    fun encodePacket(packet: RemoteStatusPacket): String {
        val json = JSONObject().apply {
            put("v", packet.version)
            put("t", packet.timestamp)
            put("n", packet.deviceName)
            put("h", encodeHealthState(packet.healthState))
            put("s", encodeSetupCompleteness(packet.setupCompleteness))
        }
        return Base64.getEncoder().encodeToString(json.toString().toByteArray())
    }

    fun decodePacket(encoded: String): RemoteStatusPacket? {
        return try {
            val jsonStr = String(Base64.getDecoder().decode(encoded))
            val json = JSONObject(jsonStr)
            RemoteStatusPacket(
                version = json.getInt("v"),
                timestamp = json.getLong("t"),
                deviceName = json.getString("n"),
                healthState = decodeHealthState(json.getJSONObject("h")),
                setupCompleteness = decodeSetupCompleteness(json.getJSONObject("s"))
            )
        } catch (e: Exception) {
            null
        }
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
