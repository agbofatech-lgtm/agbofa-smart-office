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
        WorkflowEntity::class,
        WorkflowStepEntity::class,
        WorkflowStepTransitionEntity::class,
        RuleEntity::class,
        DecisionEntity::class,
        DecisionTransitionEntity::class,
        AuthorizedActionRequestEntity::class,
        AuthorizedActionExecutionEntity::class,
        SearchIndexEntity::class,
    ],
    version = 9,
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
    abstract fun workflowDao(): WorkflowDao
    abstract fun workflowStepDao(): WorkflowStepDao
    abstract fun workflowStepTransitionDao(): WorkflowStepTransitionDao
    abstract fun ruleDao(): RuleDao
    abstract fun decisionDao(): DecisionDao
    abstract fun decisionTransitionDao(): DecisionTransitionDao
    abstract fun authorizedActionRequestDao(): AuthorizedActionRequestDao
    abstract fun authorizedActionExecutionDao(): AuthorizedActionExecutionDao
    abstract fun searchIndexDao(): SearchIndexDao

    companion object {
        const val NAME = "smart-office.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS classifications (
                        id TEXT NOT NULL,
                        journalEntryId TEXT NOT NULL,
                        type TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        classifiedAt TEXT NOT NULL,
                        revision INTEGER NOT NULL,
                        ruleVersion TEXT,
                        supersedesId TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(journalEntryId) REFERENCES journal_entries(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_classifications_journalEntryId_revision ON classifications(journalEntryId, revision)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_classifications_journalEntryId ON classifications(journalEntryId)",
                )
            }
        }

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

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS workflows (
                        id TEXT NOT NULL,
                        operationalRecordId TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        ruleVersion TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(operationalRecordId) REFERENCES operational_records(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_workflows_operationalRecordId ON workflows(operationalRecordId)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS workflow_steps (
                        id TEXT NOT NULL,
                        workflowId TEXT NOT NULL,
                        ordinal INTEGER NOT NULL,
                        key TEXT NOT NULL,
                        label TEXT NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(workflowId) REFERENCES workflows(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_workflow_steps_workflowId_ordinal ON workflow_steps(workflowId, ordinal)",
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_workflow_steps_workflowId_key ON workflow_steps(workflowId, key)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_workflow_steps_workflowId ON workflow_steps(workflowId)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS workflow_step_transitions (
                        id TEXT NOT NULL,
                        workflowStepId TEXT NOT NULL,
                        fromStatus TEXT NOT NULL,
                        toStatus TEXT NOT NULL,
                        transitionedAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        ruleVersion TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(workflowStepId) REFERENCES workflow_steps(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_workflow_step_transitions_workflowStepId ON workflow_step_transitions(workflowStepId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_workflow_step_transitions_transitionedAt ON workflow_step_transitions(transitionedAt)",
                )
            }
        }


        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS rules (
                        id TEXT NOT NULL,
                        version TEXT NOT NULL,
                        key TEXT NOT NULL,
                        condition TEXT NOT NULL,
                        decision TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        PRIMARY KEY(id, version)
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_rules_key ON rules(key)",
                )
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS human_decisions (
                        id TEXT NOT NULL,
                        subjectKind TEXT NOT NULL,
                        subjectTargetId TEXT NOT NULL,
                        actionType TEXT NOT NULL,
                        rationale TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_human_decisions_createdAt ON human_decisions(createdAt)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_human_decisions_subjectKind_subjectTargetId ON human_decisions(subjectKind, subjectTargetId)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS human_decision_transitions (
                        id TEXT NOT NULL,
                        decisionId TEXT NOT NULL,
                        fromStatus TEXT NOT NULL,
                        toStatus TEXT NOT NULL,
                        transitionedAt TEXT NOT NULL,
                        basis TEXT NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(decisionId) REFERENCES human_decisions(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_human_decision_transitions_decisionId ON human_decision_transitions(decisionId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_human_decision_transitions_transitionedAt ON human_decision_transitions(transitionedAt)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS authorized_action_requests (
                        id TEXT NOT NULL,
                        decisionId TEXT NOT NULL,
                        actionType TEXT NOT NULL,
                        targetId TEXT NOT NULL,
                        requestedAt TEXT NOT NULL,
                        toStateName TEXT,
                        completeTransitionId TEXT,
                        activateTransitionId TEXT,
                        PRIMARY KEY(id),
                        FOREIGN KEY(decisionId) REFERENCES human_decisions(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_authorized_action_requests_decisionId ON authorized_action_requests(decisionId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_authorized_action_requests_requestedAt ON authorized_action_requests(requestedAt)",
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS authorized_action_executions (
                        id TEXT NOT NULL,
                        requestId TEXT NOT NULL,
                        decisionId TEXT NOT NULL,
                        outcome TEXT NOT NULL,
                        detail TEXT NOT NULL,
                        executedAt TEXT NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(requestId) REFERENCES authorized_action_requests(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_authorized_action_executions_requestId ON authorized_action_executions(requestId)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_authorized_action_executions_decisionId ON authorized_action_executions(decisionId)",
                )
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS search_index (
                        id TEXT NOT NULL,
                        type TEXT NOT NULL,
                        entityId TEXT NOT NULL,
                        content TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent(),
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_search_index_type ON search_index(type)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_search_index_entityId ON search_index(entityId)")
            }
        }

        fun create(context: Context): SmartOfficeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmartOfficeDatabase::class.java,
                NAME,
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                )
                // Main-thread queries remain because current use cases are synchronous
                // and invoked from the composition-root UI thread. Removing this
                // requires an async application rewrite, which is out of Phase 13.
                .allowMainThreadQueries()
                .build()
        }
    }
}
