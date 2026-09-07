package com.agbofa.smartoffice.presentation

import com.agbofa.smartoffice.presentation.dashboard.DashboardUiState
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardMappingTest {
    @Test
    fun defaultDashboardIsEmptyNotComputed() {
        val state = DashboardUiState()
        assertTrue(state.empty)
        assertTrue(state.analytics == null)
    }
}
