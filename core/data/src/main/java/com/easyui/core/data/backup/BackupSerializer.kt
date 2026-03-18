package com.easyui.core.data.backup

import com.easyui.core.domain.model.BackupData
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.ValidationResult
import org.json.JSONArray
import org.json.JSONObject

/**
 * Serializes and deserializes launcher state to/from a JSON string.
 *
 * Format version 1 structure:
 * {
 *   "version": 1,
 *   "app": "com.easyui.launcher",
 *   "exportedAt": "<ISO-8601>",
 *   "settings": { ... },
 *   "tiles": [ ... ]
 * }
 */
object BackupSerializer {
    private const val BACKUP_VERSION = 1
    private const val APP_ID = "com.easyui.launcher"

    fun serialize(
        settings: LauncherSettings,
        tiles: List<HomeTile>,
        hiddenPackages: Set<String>,
        exportedAt: String,
    ): String {
        val root = JSONObject()
        root.put("version", BACKUP_VERSION)
        root.put("app", APP_ID)
        root.put("exportedAt", exportedAt)

        root.put("settings", settingsToJson(settings))
        root.put("tiles", tilesToJson(tiles))
        root.put("hiddenPackages", JSONArray(hiddenPackages.toList()))

        return root.toString(2)
    }

    fun deserialize(json: String): BackupData {
        val root = JSONObject(json)
        val version = root.optInt("version", 0)
        if (version < 1 || version > BACKUP_VERSION) {
            throw IllegalArgumentException("Unsupported backup version: $version")
        }
        if (root.optString("app") != APP_ID) {
            throw IllegalArgumentException("This backup is not from EasyUI Senior Launcher.")
        }
        val settings = jsonToSettings(root.getJSONObject("settings"))
        val tiles = jsonToTiles(root.getJSONArray("tiles"))
        val hiddenPackages = root.optJSONArray("hiddenPackages")
            ?.let { arr -> (0 until arr.length()).map { arr.getString(it) }.toSet() }
            ?: emptySet()
        return BackupData(
            version = version,
            settings = settings,
            tiles = tiles,
            hiddenPackages = hiddenPackages,
        )
    }

    fun validate(json: String): ValidationResult {
        return try {
            deserialize(json)
            ValidationResult.Valid
        } catch (e: Exception) {
            ValidationResult.Invalid(e.message ?: "Unknown error")
        }
    }

    // ── Settings ──────────────────────────────────────────────────────────

    private fun settingsToJson(s: LauncherSettings): JSONObject = JSONObject().apply {
        put("emergencyPhoneNumber", s.emergencyPhoneNumber)
        put(
            "emergencyNumbers",
            JSONArray().apply {
                s.emergencyNumbers.forEach { number ->
                    put(
                        JSONObject().apply {
                            put("label", number.label)
                            put("phoneNumber", number.phoneNumber)
                        },
                    )
                }
            },
        )
        put("sosNumbers", JSONArray(s.sosNumbers))
        put("use24HourClock", s.use24HourClock)
        put("caregiverProtectionEnabled", s.caregiverProtectionEnabled)
        put("layoutLocked", s.layoutLocked)
        put("easyUiLockEnabled", s.easyUiLockEnabled)
        put("easyUiLockTimeoutSeconds", s.easyUiLockTimeoutSeconds)
        put("appVisibilityPreset", s.appVisibilityPreset)
        put("homeReadabilityPreset", s.homeReadabilityPreset)
        put("verySimpleModeEnabled", s.verySimpleModeEnabled)
        put("showBatteryInfo", s.showBatteryInfo)
        put("homePageCount", s.homePageCount)
        put("healthInfo", JSONObject().apply {
            put("fullName", s.healthInfo.fullName)
            put("age", s.healthInfo.age)
            put("bloodGroup", s.healthInfo.bloodGroup)
            put("allergies", s.healthInfo.allergies)
            put("medicalConditions", s.healthInfo.medicalConditions)
            put("medicines", s.healthInfo.medicines)
            put("doctorOrEmergencyContact", s.healthInfo.doctorOrEmergencyContact)
            put("notes", s.healthInfo.notes)
        })
        // PIN credentials are intentionally excluded from backup for security.
        // Onboarding state is excluded — it will be inferred as complete on restore.
    }

    private fun jsonToSettings(obj: JSONObject): LauncherSettings {
        val health = obj.optJSONObject("healthInfo")
        val emergencyNumbers = obj.optJSONArray("emergencyNumbers")?.let { numbers ->
            buildList {
                for (index in 0 until numbers.length()) {
                    val entry = numbers.optJSONObject(index) ?: continue
                    val label = entry.optString("label", "").trim()
                    val phoneNumber = entry.optString("phoneNumber", "").trim()
                    if (label.isNotBlank() && phoneNumber.isNotBlank()) {
                        add(EmergencyNumber(label = label, phoneNumber = phoneNumber))
                    }
                }
            }
        } ?: listOf(
            EmergencyNumber(label = "Ambulance", phoneNumber = "911"),
            EmergencyNumber(label = "Police", phoneNumber = "911"),
            EmergencyNumber(label = "Fire", phoneNumber = "911"),
        )
        val sosNumbers = obj.optJSONArray("sosNumbers")?.let { values ->
            buildList {
                for (index in 0 until values.length()) {
                    val number = values.optString(index, "").trim()
                    if (number.isNotBlank()) add(number)
                }
            }.take(3)
        } ?: emptyList()
        return LauncherSettings(
            onboardingComplete = true,
            emergencyPhoneNumber = obj.optString("emergencyPhoneNumber", "911"),
            emergencyNumbers = emergencyNumbers,
            sosNumbers = sosNumbers,
            use24HourClock = obj.optBoolean("use24HourClock", false),
            caregiverProtectionEnabled = obj.optBoolean("caregiverProtectionEnabled", false),
            layoutLocked = obj.optBoolean("layoutLocked", false),
            easyUiLockEnabled = obj.optBoolean("easyUiLockEnabled", false),
            easyUiLockTimeoutSeconds = obj.optInt("easyUiLockTimeoutSeconds", 60).coerceIn(15, 300),
            appVisibilityPreset = obj.optString("appVisibilityPreset", "CUSTOM"),
            homeReadabilityPreset = obj.optString("homeReadabilityPreset", "STANDARD"),
            verySimpleModeEnabled = obj.optBoolean("verySimpleModeEnabled", false),
            showBatteryInfo = obj.optBoolean("showBatteryInfo", false),
            homePageCount = obj.optInt("homePageCount", 2).coerceIn(1, 3),
            healthInfo = HealthInfo(
                fullName = health?.optString("fullName", "").orEmpty(),
                age = health?.optString("age", "").orEmpty(),
                bloodGroup = health?.optString("bloodGroup", "").orEmpty(),
                allergies = health?.optString("allergies", "").orEmpty(),
                medicalConditions = health?.optString("medicalConditions", "").orEmpty(),
                medicines = health?.optString("medicines", "").orEmpty(),
                doctorOrEmergencyContact = health?.optString("doctorOrEmergencyContact", "").orEmpty(),
                notes = health?.optString("notes", "").orEmpty(),
            ),
            // PIN is not restored from backup.
            pinSaltHex = null,
            pinHashHex = null,
        )
    }

    // ── Tiles ─────────────────────────────────────────────────────────────

    private fun tilesToJson(tiles: List<HomeTile>): JSONArray {
        val arr = JSONArray()
        for (tile in tiles) {
            arr.put(tileToJson(tile))
        }
        return arr
    }

    private fun tileToJson(t: HomeTile): JSONObject = JSONObject().apply {
        put("id", t.id)
        put("position", t.position)
        put("title", t.title)
        put("type", t.type.name)
        t.action?.let { put("action", it.name) }
        t.packageName?.let { put("packageName", it) }
        t.phoneNumber?.let { put("phoneNumber", it) }
        // photoUri references a device-local content URI; omit from export
        // since it will be invalid on a different device or after a factory reset.
    }

    private fun jsonToTiles(arr: JSONArray): List<HomeTile> {
        val tiles = mutableListOf<HomeTile>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val type = runCatching { HomeTileType.valueOf(obj.optString("type", "APP")) }
                .getOrDefault(HomeTileType.APP)
            val action = obj.optString("action", "").takeIf { it.isNotBlank() }
                ?.let { runCatching { HomeTileAction.valueOf(it) }.getOrNull() }
            tiles.add(
                HomeTile(
                    id = obj.getString("id"),
                    position = obj.getInt("position"),
                    title = obj.getString("title"),
                    type = type,
                    action = action,
                    packageName = obj.optString("packageName").takeIf { it.isNotBlank() },
                    phoneNumber = obj.optString("phoneNumber").takeIf { it.isNotBlank() },
                    photoUri = null, // not restored; see note in tileToJson
                ),
            )
        }
        return tiles
    }
}
