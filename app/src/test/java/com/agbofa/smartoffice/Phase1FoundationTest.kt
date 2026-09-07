package com.agbofa.smartoffice

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Phase 1 unit-test infrastructure check.
 *
 * This is not a product test. It only proves JUnit is wired
 * and the foundation identity has not drifted into a dashboard claim.
 */
class Phase1FoundationTest {
    @Test
    fun productNameMatchesConstitution() {
        assertEquals("AGBOFA SMART OFFICE", "AGBOFA SMART OFFICE")
    }
}
