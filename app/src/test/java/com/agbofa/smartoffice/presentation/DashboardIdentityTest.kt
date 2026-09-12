package com.agbofa.smartoffice.presentation

import com.agbofa.smartoffice.presentation.settings.OfficePalette
import com.agbofa.smartoffice.presentation.settings.OfficePaletteMapping
import com.agbofa.smartoffice.presentation.settings.OfficePreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DashboardIdentityTest {
    @Test
    fun headerFieldsMapFromSavedPreferences() {
        val prefs = OfficePreferences.normalize(
            officeName = "Clinic Desk",
            officeSubtitle = "Today at a glance",
            monogram = "CD",
            palette = OfficePalette.Slate,
        )
        assertEquals("Clinic Desk", prefs.officeName)
        assertEquals("Today at a glance", prefs.officeSubtitle)
        assertEquals("CD", prefs.monogram)
        assertEquals(OfficePalette.Slate, prefs.palette)
    }

    @Test
    fun blankIdentityFallsBackBeforeDashboardRender() {
        val prefs = OfficePreferences.normalize("", "", "", OfficePalette.Agbofa)
        assertEquals(OfficePreferences.DEFAULT_OFFICE_NAME, prefs.officeName)
        assertEquals(OfficePreferences.DEFAULT_OFFICE_SUBTITLE, prefs.officeSubtitle)
        assertEquals(OfficePreferences.DEFAULT_MONOGRAM, prefs.monogram)
        assertEquals(OfficePalette.Agbofa, prefs.palette)
    }

    @Test
    fun paletteTokensRemainDistinctForIdentitySurfaces() {
        val agbofa = OfficePaletteMapping.tokens(OfficePalette.Agbofa).primary
        val slate = OfficePaletteMapping.tokens(OfficePalette.Slate).primary
        val forest = OfficePaletteMapping.tokens(OfficePalette.Forest).primary
        assertNotEquals(agbofa, slate)
        assertNotEquals(agbofa, forest)
    }
}
