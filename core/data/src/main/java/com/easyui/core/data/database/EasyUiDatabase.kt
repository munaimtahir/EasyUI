package com.easyui.core.data.database

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [HomeTileEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class EasyUiDatabase : RoomDatabase() {
    abstract fun homeTileDao(): HomeTileDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE home_tiles ADD COLUMN phoneNumber TEXT")
                    db.execSQL("ALTER TABLE home_tiles ADD COLUMN photoUri TEXT")
                }
            }

        fun build(context: Context): EasyUiDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                EasyUiDatabase::class.java,
                "easyui.db",
            ).addMigrations(MIGRATION_1_2)
                .build()
    }
}
