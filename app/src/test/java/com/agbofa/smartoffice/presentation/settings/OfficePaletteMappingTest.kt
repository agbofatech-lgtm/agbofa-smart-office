package com.agbofa.smartoffice.presentation.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfficePaletteMappingTest {
    @Test
    fun everyPaletteHasPrimaryToken() {
        OfficePalette.entries.forEach { palette ->
            val tokens = OfficePaletteMapping.tokens(palette)
            assertTrue(tokens.primary.alpha > 0f)
        }
    }

    @Test
    fun tokenSetsArePopulated() {
        OfficePalette.entries.forEach { palette ->
            val tokens = OfficePaletteMapping.tokens(palette)
            assertTrue(tokens.secondary.alpha > 0f)
            assertTrue(tokens.background.alpha > 0f)
            assertTrue(tokens.surface.alpha > 0f)
            assertTrue(tokens.muted.alpha > 0f)
        }
    }

    @Test
    fun primaryTokensAreNotIdenticalAcrossPalettes() {
        val agbofa = OfficePaletteMapping.tokens(OfficePalette.Agbofa).primary
        val slate = OfficePaletteMapping.tokens(OfficePalette.Slate).primary
        val forest = OfficePaletteMapping.tokens(OfficePalette.Forest).primary
        assertNotEquals(agbofa, slate)
        assertNotEquals(agbofa, forest)
        assertNotEquals(slate, forest)
        assertEquals(3, setOf(agbofa, slate, forest).size)
    }
}
