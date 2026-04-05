package com.easyui.core.data.database

import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EasyUiDatabaseMigrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            EasyUiDatabase::class.java,
            emptyList(),
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    fun migrate1To2AddsContactColumns() {
        context.deleteDatabase("migration-test")
        val database = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("migration-test"), null)
        database.execSQL("PRAGMA user_version = 1")
        database.apply {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS home_tiles (
                    id TEXT NOT NULL PRIMARY KEY,
                    position INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    type TEXT NOT NULL,
                    packageName TEXT,
                    action TEXT
                )
                """.trimIndent(),
            )
            close()
        }

        helper.runMigrationsAndValidate("migration-test", 2, true, EasyUiDatabase.MIGRATION_1_2)
    }
}
