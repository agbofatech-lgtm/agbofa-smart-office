package com.agbofa.smartoffice.presentation

import com.agbofa.smartoffice.presentation.settings.OfficePalette
import com.agbofa.smartoffice.presentation.settings.OfficePreferences
import org.junit.Assert.assertEquals
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
    }

    @Test
    fun blankIdentityFallsBackBeforeDashboardRender() {
        val prefs = OfficePreferences.normalize("", "", "", OfficePalette.Agbofa)
        assertEquals(OfficePreferences.DEFAULT_OFFICE_NAME, prefs.officeName)
        assertEquals(OfficePreferences.DEFAULT_OFFICE_SUBTITLE, prefs.officeSubtitle)
        assertEquals(OfficePreferences.DEFAULT_MONOGRAM, prefs.monogram)
    }
}
