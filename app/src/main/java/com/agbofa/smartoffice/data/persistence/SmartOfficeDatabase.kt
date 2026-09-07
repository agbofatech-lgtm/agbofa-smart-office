package com.agbofa.smartoffice.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CaptureEntity::class,
        JournalEntryEntity::class,
        ClassificationEntity::class,
        OperationalRecordEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class SmartOfficeDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun classificationDao(): ClassificationDao
    abstract fun operationalRecordDao(): OperationalRecordDao

    companion object {
        const val NAME = "smart-office.db"

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS operational_records (
                        id TEXT NOT NULL,
                        journalEntryId TEXT NOT NULL,
                        classificationId TEXT NOT NULL,
                        type TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        creationBasis TEXT NOT NULL,
                        ruleVersion TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(journalEntryId) REFERENCES journal_entries(id) ON DELETE RESTRICT,
                        FOREIGN KEY(classificationId) REFERENCES classifications(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_operational_records_journalEntryId ON operational_records(journalEntryId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_records_classificationId ON operational_records(classificationId)",
                )
            }
        }

        fun create(context: Context): SmartOfficeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmartOfficeDatabase::class.java,
                NAME,
            )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
