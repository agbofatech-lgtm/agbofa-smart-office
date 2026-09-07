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
        OperationalStateTransitionEntity::class,
        OperationalTemporalRecordEntity::class,
        OperationalDependencyEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class SmartOfficeDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun classificationDao(): ClassificationDao
    abstract fun operationalRecordDao(): OperationalRecordDao
    abstract fun operationalStateTransitionDao(): OperationalStateTransitionDao
    abstract fun operationalTemporalRecordDao(): OperationalTemporalRecordDao
    abstract fun operationalDependencyDao(): OperationalDependencyDao

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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS operational_state_transitions (
                        id TEXT NOT NULL,
                        operationalRecordId TEXT NOT NULL,
                        fromState TEXT NOT NULL,
                        toState TEXT NOT NULL,
                        transitionedAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        ruleVersion TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(operationalRecordId) REFERENCES operational_records(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_state_transitions_operationalRecordId ON operational_state_transitions(operationalRecordId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_state_transitions_transitionedAt ON operational_state_transitions(transitionedAt)",
                )
            }
        }


        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS operational_temporal_records (
                        id TEXT NOT NULL,
                        operationalRecordId TEXT NOT NULL,
                        resolution TEXT NOT NULL,
                        referenceExpression TEXT,
                        dueInstant TEXT,
                        civilDateTime TEXT,
                        civilZone TEXT,
                        basis TEXT NOT NULL,
                        ruleVersion TEXT,
                        assignedAt TEXT NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(operationalRecordId) REFERENCES operational_records(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_temporal_records_operationalRecordId ON operational_temporal_records(operationalRecordId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_temporal_records_assignedAt ON operational_temporal_records(assignedAt)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS operational_dependencies (
                        id TEXT NOT NULL,
                        dependentOperationalRecordId TEXT NOT NULL,
                        prerequisiteOperationalRecordId TEXT NOT NULL,
                        type TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        ruleVersion TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(dependentOperationalRecordId) REFERENCES operational_records(id) ON DELETE RESTRICT,
                        FOREIGN KEY(prerequisiteOperationalRecordId) REFERENCES operational_records(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_operational_dependencies_pair ON operational_dependencies(dependentOperationalRecordId, prerequisiteOperationalRecordId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_dependencies_dependentOperationalRecordId ON operational_dependencies(dependentOperationalRecordId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_operational_dependencies_prerequisiteOperationalRecordId ON operational_dependencies(prerequisiteOperationalRecordId)",
                )
            }
        }

        fun create(context: Context): SmartOfficeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmartOfficeDatabase::class.java,
                NAME,
            )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
