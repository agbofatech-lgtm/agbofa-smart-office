package com.agbofa.smartoffice.data.persistence

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Runtime migration certification. Requires Android instrumentation + SDK.
 *
 * Historical schema JSON was never exported at versions 1–8. Tests therefore
 * create v1 tables with the same CREATE statements as the current Capture and
 * Journal entities (those tables were never altered) and apply the registered
 * Migration objects in order. They do not fabricate Room schema JSON.
 */
@RunWith(AndroidJUnit4::class)
class RoomMigrationChainTest {
    private val dbName = "migration-certification"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SmartOfficeDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    @Throws(IOException::class)
    fun stepwiseOneThroughNinePreservesCaptureAndJournal() {
        val db = openVersionOne()
        insertCanonical(db)
        applyAll(db)
        assertCanonical(db)
        assertTrue(tableExists(db, "search_index"))
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun eightToNineCreatesEmptySearchIndex() {
        val db = openVersionOne()
        insertCanonical(db)
        applyThrough(db, throughEnd = 8)
        assertFalse(tableExists(db, "search_index"))
        SmartOfficeDatabase.MIGRATION_8_9.migrate(db)
        assertTrue(tableExists(db, "search_index"))
        val count = db.query("SELECT COUNT(*) FROM search_index").use {
            it.moveToFirst()
            it.getInt(0)
        }
        assertEquals(0, count)
        assertCanonical(db)
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun droppingSearchIndexDoesNotDeleteCanonicalRows() {
        val db = openVersionOne()
        insertCanonical(db)
        applyAll(db)
        db.execSQL("DELETE FROM search_index")
        db.execSQL("DROP TABLE search_index")
        assertFalse(tableExists(db, "search_index"))
        assertCanonical(db)
        db.close()
    }

    private fun openVersionOne(): SupportSQLiteDatabase {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(dbName)
        val openHelper = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS captures (
                                id TEXT NOT NULL,
                                originalExpression TEXT NOT NULL,
                                capturedAt TEXT NOT NULL,
                                source TEXT NOT NULL,
                                PRIMARY KEY(id)
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS journal_entries (
                                id TEXT NOT NULL,
                                captureId TEXT NOT NULL,
                                admittedAt TEXT NOT NULL,
                                PRIMARY KEY(id),
                                FOREIGN KEY(captureId) REFERENCES captures(id) ON DELETE RESTRICT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE UNIQUE INDEX IF NOT EXISTS index_journal_entries_captureId ON journal_entries(captureId)",
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS index_journal_entries_admittedAt ON journal_entries(admittedAt)",
                        )
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )
        val db = openHelper.writableDatabase
        db.setVersion(1)
        return db
    }

    private fun insertCanonical(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO captures(id, originalExpression, capturedAt, source) VALUES(?,?,?,?)",
            arrayOf("cap-1", "exact original expression", "2026-01-01T00:00:00Z", "TYPED"),
        )
        db.execSQL(
            "INSERT INTO journal_entries(id, captureId, admittedAt) VALUES(?,?,?)",
            arrayOf("jnl-1", "cap-1", "2026-01-01T00:00:01Z"),
        )
    }

    private fun applyThrough(db: SupportSQLiteDatabase, throughEnd: Int) {
        val all = listOf(
            SmartOfficeDatabase.MIGRATION_1_2,
            SmartOfficeDatabase.MIGRATION_2_3,
            SmartOfficeDatabase.MIGRATION_3_4,
            SmartOfficeDatabase.MIGRATION_4_5,
            SmartOfficeDatabase.MIGRATION_5_6,
            SmartOfficeDatabase.MIGRATION_6_7,
            SmartOfficeDatabase.MIGRATION_7_8,
            SmartOfficeDatabase.MIGRATION_8_9,
        )
        for (m in all) {
            if (m.endVersion <= throughEnd) m.migrate(db)
        }
        db.setVersion(throughEnd)
    }

    private fun applyAll(db: SupportSQLiteDatabase) = applyThrough(db, 9)

    private fun assertCanonical(db: SupportSQLiteDatabase) {
        db.query("SELECT id, originalExpression, capturedAt, source FROM captures WHERE id = 'cap-1'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("cap-1", c.getString(0))
            assertEquals("exact original expression", c.getString(1))
            assertEquals("2026-01-01T00:00:00Z", c.getString(2))
            assertEquals("TYPED", c.getString(3))
        }
        db.query("SELECT id, captureId, admittedAt FROM journal_entries WHERE id = 'jnl-1'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("jnl-1", c.getString(0))
            assertEquals("cap-1", c.getString(1))
            assertEquals("2026-01-01T00:00:01Z", c.getString(2))
        }
    }

    private fun tableExists(db: SupportSQLiteDatabase, name: String): Boolean {
        db.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(name),
        ).use { return it.moveToFirst() }
    }
}

private fun SupportSQLiteDatabase.setVersion(version: Int) {
    execSQL("PRAGMA user_version = $version")
}
