package com.agbofa.smartoffice.presentation.navigation

import org.junit.Assert.assertTrue
import org.junit.Test

class AppDestinationTest {
    @Test
    fun routesIncludeJournalDashboardDecisionAnalytics() {
        val labels = AppDestination.entries.map { it.label }.toSet()
        assertTrue(labels.containsAll(setOf("Journal", "Dashboard", "Decision", "Analytics")))
    }
}
