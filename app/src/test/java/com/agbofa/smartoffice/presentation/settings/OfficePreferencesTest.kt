package com.agbofa.smartoffice.presentation.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OfficePreferencesTest {
    @Test
    fun defaultsMatchAuthorizedCopy() {
        val prefs = OfficePreferences()
        assertEquals("AGBOFA Smart Office", prefs.officeName)
        assertEquals("A calm view of your office operations.", prefs.officeSubtitle)
        assertEquals("A", prefs.monogram)
        assertEquals(OfficePalette.Agbofa, prefs.palette)
    }

    @Test
    fun emptyOfficeNameFallsBack() {
        val prefs = OfficePreferences.normalize("   ", "Office desk", "RV", OfficePalette.Slate)
        assertEquals(OfficePreferences.DEFAULT_OFFICE_NAME, prefs.officeName)
        assertEquals("Office desk", prefs.officeSubtitle)
        assertEquals("RV", prefs.monogram)
        assertEquals(OfficePalette.Slate, prefs.palette)
    }

    @Test
    fun monogramTruncatesToTwoCharacters() {
        val prefs = OfficePreferences.normalize("Rev Office Assistant", "Subtitle", "REV", OfficePalette.Forest)
        assertEquals("RE", prefs.monogram)
        assertEquals("Rev Office Assistant", prefs.officeName)
    }

    @Test
    fun blankMonogramFallsBack() {
        val prefs = OfficePreferences.normalize("Headteacher Smart Assistant", "Help at the desk", "  ", OfficePalette.Agbofa)
        assertEquals("A", prefs.monogram)
    }
}
