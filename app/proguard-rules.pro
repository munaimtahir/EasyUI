# EasyUI Senior Launcher — ProGuard / R8 rules

# ─────────────────────────────────────────────────
# Room: keep entity and DAO classes
# ─────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract ** *();
}
# Room-generated Kotlin code uses intrinsics
-dontwarn javax.annotation.processing.**
-dontwarn androidx.room.paging.**

# ─────────────────────────────────────────────────
# DataStore / Preferences
# ─────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# ─────────────────────────────────────────────────
# Kotlin coroutines & serialization
# ─────────────────────────────────────────────────
-keep class kotlinx.coroutines.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─────────────────────────────────────────────────
# Jetpack Navigation
# ─────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ─────────────────────────────────────────────────
# Compose: UI test infrastructure (only in debug)
# ─────────────────────────────────────────────────
-dontwarn androidx.compose.ui.test.**

# ─────────────────────────────────────────────────
# EasyUI domain and data models
# Keep all model and entity classes so Room
# can reconstruct them from the DB schema.
# ─────────────────────────────────────────────────
-keep class com.easyui.core.domain.model.** { *; }
-keep class com.easyui.core.data.database.** { *; }

# ─────────────────────────────────────────────────
# Security: keep PBKDF2 / MessageDigest names so
# PinHasher reflects the algorithm string correctly
# ─────────────────────────────────────────────────
-keepnames class javax.crypto.** { *; }
-keepnames class java.security.** { *; }

# ─────────────────────────────────────────────────
# Common: suppress irrelevant warnings
# ─────────────────────────────────────────────────
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# Retain debug info for crash-reporting stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
