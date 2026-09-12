package com.agbofa.smartoffice.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsNavigationTest {
    @Test
    fun settingsIsSecondaryNotAPrimaryTab() {
        assertFalse(AppDestination.SETTINGS.primary)
        assertEquals("Settings", AppDestination.SETTINGS.label)
    }

    @Test
    fun primaryTabsRemainExactlyFour() {
        val primary = AppDestination.primary.map { it.label }
        assertEquals(listOf("Home", "Journal", "Decisions", "Search"), primary)
        assertEquals(4, primary.size)
        assertFalse(primary.contains("Settings"))
    }

    @Test
    fun settingsExistsAmongDestinations() {
        assertTrue(AppDestination.entries.contains(AppDestination.SETTINGS))
    }
}
