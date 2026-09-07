package com.agbofa.smartoffice.data.persistence

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomMigrationCertificationTest {
    private val dbName = "migration-certification.db"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SmartOfficeDatabase::class.java,
    )

    private val all = arrayOf(
        SmartOfficeDatabase.MIGRATION_1_2,
        SmartOfficeDatabase.MIGRATION_2_3,
        SmartOfficeDatabase.MIGRATION_3_4,
        SmartOfficeDatabase.MIGRATION_4_5,
        SmartOfficeDatabase.MIGRATION_5_6,
        SmartOfficeDatabase.MIGRATION_6_7,
        SmartOfficeDatabase.MIGRATION_7_8,
        SmartOfficeDatabase.MIGRATION_8_9,
    )

    @Test fun migrate1To2() = step(1, 2, SmartOfficeDatabase.MIGRATION_1_2)
    @Test fun migrate2To3() = step(2, 3, SmartOfficeDatabase.MIGRATION_2_3)
    @Test fun migrate3To4() = step(3, 4, SmartOfficeDatabase.MIGRATION_3_4)
    @Test fun migrate4To5() = step(4, 5, SmartOfficeDatabase.MIGRATION_4_5)
    @Test fun migrate5To6() = step(5, 6, SmartOfficeDatabase.MIGRATION_5_6)
    @Test fun migrate6To7() = step(6, 7, SmartOfficeDatabase.MIGRATION_6_7)
    @Test fun migrate7To8() = step(7, 8, SmartOfficeDatabase.MIGRATION_7_8)
    @Test fun migrate8To9() = step(8, 9, SmartOfficeDatabase.MIGRATION_8_9)

    @Test
    fun migrate1To9PreservesCaptureAndJournal() {
        Assume.assumeTrue("Room schema 1.json not exported; skip helper path", schemaExists(1))
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                "INSERT INTO captures (id, originalExpression, capturedAt, source) VALUES (?, ?, ?, ?)",
                arrayOf("cap-1", "exact original expression", "2026-01-01T00:00:00Z", "TYPED"),
            )
            execSQL(
                "INSERT INTO journal_entries (id, captureId, admittedAt) VALUES (?, ?, ?)",
                arrayOf("jnl-1", "cap-1", "2026-01-01T00:00:01Z"),
            )
            close()
        }
        helper.runMigrationsAndValidate(dbName, 9, true, *all).apply {
            val capture = query("SELECT id, originalExpression, capturedAt, source FROM captures WHERE id = 'cap-1'")
            assertTrue(capture.moveToFirst())
            assertEquals("cap-1", capture.getString(0))
            assertEquals("exact original expression", capture.getString(1))
            assertEquals("2026-01-01T00:00:00Z", capture.getString(2))
            capture.close()
            val journal = query("SELECT id, captureId FROM journal_entries WHERE id = 'jnl-1'")
            assertTrue(journal.moveToFirst())
            assertEquals("jnl-1", journal.getString(0))
            assertEquals("cap-1", journal.getString(1))
            journal.close()
            close()
        }
    }

    @Test
    fun searchIndexDropDoesNotDeleteCanonicalRows() {
        Assume.assumeTrue("Room schema 8.json not exported; skip helper path", schemaExists(8))
        helper.createDatabase(dbName, 8).apply {
            execSQL(
                "INSERT INTO captures (id, originalExpression, capturedAt, source) VALUES (?, ?, ?, ?)",
                arrayOf("cap-keep", "keep me", "2026-01-02T00:00:00Z", "TYPED"),
            )
            close()
        }
        val migrated: SupportSQLiteDatabase =
            helper.runMigrationsAndValidate(dbName, 9, true, SmartOfficeDatabase.MIGRATION_8_9)
        migrated.execSQL("DELETE FROM search_index")
        val capture = migrated.query("SELECT id FROM captures WHERE id = 'cap-keep'")
        assertTrue(capture.moveToFirst())
        capture.close()
        migrated.close()
    }

    private fun schemaExists(version: Int): Boolean {
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        return try {
            assets.open("com.agbofa.smartoffice.data.persistence.SmartOfficeDatabase/$version.json").close()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun step(from: Int, to: Int, migration: androidx.room.migration.Migration) {
        Assume.assumeTrue("Room schema $from.json not exported; skip helper path", schemaExists(from))
        helper.createDatabase(dbName, from).close()
        helper.runMigrationsAndValidate(dbName, to, true, migration).close()
    }
}
