package com.easyui.core.data.backup;

/**
 * Serializes and deserializes launcher state to/from a JSON string.
 *
 * Format version 1 structure:
 * {
 *  "version": 1,
 *  "app": "com.easyui.launcher",
 *  "exportedAt": "<ISO-8601>",
 *  "settings": { ... },
 *  "tiles": [ ... ]
 * }
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J2\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u000b2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u00172\u0006\u0010\u0018\u001a\u00020\u0004J\u0010\u0010\u0019\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u000bH\u0002J\u0010\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u0010H\u0002J\u0016\u0010\u001d\u001a\u00020\u00122\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0002J\u000e\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\t\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/easyui/core/data/backup/BackupSerializer;", "", "()V", "APP_ID", "", "BACKUP_VERSION", "", "deserialize", "Lcom/easyui/core/domain/model/BackupData;", "json", "jsonToSettings", "Lcom/easyui/core/domain/model/LauncherSettings;", "obj", "Lorg/json/JSONObject;", "jsonToTiles", "", "Lcom/easyui/core/domain/model/HomeTile;", "arr", "Lorg/json/JSONArray;", "serialize", "settings", "tiles", "hiddenPackages", "", "exportedAt", "settingsToJson", "s", "tileToJson", "t", "tilesToJson", "validate", "Lcom/easyui/core/domain/model/ValidationResult;", "data_debug"})
public final class BackupSerializer {
    private static final int BACKUP_VERSION = 1;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String APP_ID = "com.easyui.launcher";
    @org.jetbrains.annotations.NotNull()
    public static final com.easyui.core.data.backup.BackupSerializer INSTANCE = null;
    
    private BackupSerializer() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String serialize(@org.jetbrains.annotations.NotNull()
    com.easyui.core.domain.model.LauncherSettings settings, @org.jetbrains.annotations.NotNull()
    java.util.List<com.easyui.core.domain.model.HomeTile> tiles, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> hiddenPackages, @org.jetbrains.annotations.NotNull()
    java.lang.String exportedAt) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.easyui.core.domain.model.BackupData deserialize(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.easyui.core.domain.model.ValidationResult validate(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    private final org.json.JSONObject settingsToJson(com.easyui.core.domain.model.LauncherSettings s) {
        return null;
    }
    
    private final com.easyui.core.domain.model.LauncherSettings jsonToSettings(org.json.JSONObject obj) {
        return null;
    }
    
    private final org.json.JSONArray tilesToJson(java.util.List<com.easyui.core.domain.model.HomeTile> tiles) {
        return null;
    }
    
    private final org.json.JSONObject tileToJson(com.easyui.core.domain.model.HomeTile t) {
        return null;
    }
    
    private final java.util.List<com.easyui.core.domain.model.HomeTile> jsonToTiles(org.json.JSONArray arr) {
        return null;
    }
}