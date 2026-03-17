package com.easyui.core.data.backup

import com.easyui.core.domain.model.BackupData
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
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
        put("use24HourClock", s.use24HourClock)
        put("caregiverProtectionEnabled", s.caregiverProtectionEnabled)
        put("layoutLocked", s.layoutLocked)
        put("appVisibilityPreset", s.appVisibilityPreset)
        put("homeReadabilityPreset", s.homeReadabilityPreset)
        put("verySimpleModeEnabled", s.verySimpleModeEnabled)
        put("showBatteryInfo", s.showBatteryInfo)
        put("homePageCount", s.homePageCount)
        // PIN credentials are intentionally excluded from backup for security.
        // Onboarding state is excluded — it will be inferred as complete on restore.
    }

    private fun jsonToSettings(obj: JSONObject): LauncherSettings = LauncherSettings(
        onboardingComplete = true,
        emergencyPhoneNumber = obj.optString("emergencyPhoneNumber", "911"),
        use24HourClock = obj.optBoolean("use24HourClock", false),
        caregiverProtectionEnabled = obj.optBoolean("caregiverProtectionEnabled", false),
        layoutLocked = obj.optBoolean("layoutLocked", false),
        appVisibilityPreset = obj.optString("appVisibilityPreset", "CUSTOM"),
        homeReadabilityPreset = obj.optString("homeReadabilityPreset", "STANDARD"),
        verySimpleModeEnabled = obj.optBoolean("verySimpleModeEnabled", false),
        showBatteryInfo = obj.optBoolean("showBatteryInfo", false),
        homePageCount = obj.optInt("homePageCount", 1).coerceIn(1, 3),
        // PIN is not restored from backup.
        pinSaltHex = null,
        pinHashHex = null,
    )

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
