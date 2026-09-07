package com.agbofa.smartoffice

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase 1 scope guard. Does not test product behavior because none exists.
 *
 * Do not Class.forName Android subclasses here — JVM unit tests have no SDK.
 */
class Phase1ScopeGuardTest {

    @Test
    fun forbiddenProductTypesAreAbsent() {
        val forbidden = listOf(
            "com.agbofa.smartoffice.domain.journal.Journal",
            "com.agbofa.smartoffice.domain.journal.JournalEvent",
            "com.agbofa.smartoffice.domain.operations.Task",
            "com.agbofa.smartoffice.domain.finance.Finance",
            "com.agbofa.smartoffice.domain.workflow.Workflow",
            "com.agbofa.smartoffice.domain.intelligence.AiEngine",
            "com.agbofa.smartoffice.infrastructure.NotificationWorker",
        )
        val leaked = forbidden.filter { classExists(it) }
        assertTrue(
            "Phase 1 must not contain product types: $leaked",
            leaked.isEmpty(),
        )
    }

    private fun classExists(name: String): Boolean =
        try {
            Class.forName(name)
            true
        } catch (_: ClassNotFoundException) {
            false
        }
}
