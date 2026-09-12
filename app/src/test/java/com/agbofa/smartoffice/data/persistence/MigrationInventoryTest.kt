package com.agbofa.smartoffice.data.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MigrationInventoryTest {
    private val all = listOf(
        SmartOfficeDatabase.MIGRATION_1_2,
        SmartOfficeDatabase.MIGRATION_2_3,
        SmartOfficeDatabase.MIGRATION_3_4,
        SmartOfficeDatabase.MIGRATION_4_5,
        SmartOfficeDatabase.MIGRATION_5_6,
        SmartOfficeDatabase.MIGRATION_6_7,
        SmartOfficeDatabase.MIGRATION_7_8,
        SmartOfficeDatabase.MIGRATION_8_9,
        SmartOfficeDatabase.MIGRATION_9_10,
    )

    @Test
    fun registeredPathIsOneThroughTen() {
        assertEquals(9, all.size)
        assertEquals(1, all.first().startVersion)
        assertEquals(10, all.last().endVersion)
        all.forEach { assertNotNull(it) }
        for (i in 0 until all.lastIndex) {
            assertEquals(all[i].endVersion, all[i + 1].startVersion)
        }
    }

    @Test
    fun companionDeclaresNineToTen() {
        val names = SmartOfficeDatabase.Companion::class.java.declaredFields.map { it.name }
        assertTrue(names.any { it.contains("MIGRATION_9_10") })
    }

    @Test
    fun sourceForbidsDestructiveFallbackAndIntelligencePersistence() {
        val candidates = listOf(
            File("src/main/java/com/agbofa/smartoffice/data/persistence/SmartOfficeDatabase.kt"),
            File("app/src/main/java/com/agbofa/smartoffice/data/persistence/SmartOfficeDatabase.kt"),
        )
        val text = candidates.first { it.exists() }.readText()
        assertFalse(text.contains("fallbackToDestructiveMigration"))
        assertTrue(text.contains("MIGRATION_9_10"))
        assertFalse(text.contains("IntelligenceEntity"))
        assertFalse(text.contains("AnalyticsEntity"))
        assertTrue(text.contains("SearchIndexEntity"))
        assertTrue(text.contains("PersonEntity"))
        assertTrue(text.contains("version = 10"))
        assertTrue(text.contains("exportSchema = true"))
    }
}
