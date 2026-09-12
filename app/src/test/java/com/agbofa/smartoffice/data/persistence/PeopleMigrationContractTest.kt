package com.agbofa.smartoffice.data.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PeopleMigrationContractTest {
    @Test
    fun migrationNineToTenIsAdditiveEmptyPeopleTable() {
        val migration = SmartOfficeDatabase.MIGRATION_9_10
        assertEquals(9, migration.startVersion)
        assertEquals(10, migration.endVersion)
    }

    @Test
    fun personEntityMatchesApprovedContract() {
        val columns = PersonEntity::class.java.declaredFields.map { it.name }.toSet()
        assertTrue(columns.containsAll(setOf(
            "id", "displayName", "category", "role", "phone", "email",
            "location", "createdAt", "updatedAt", "archivedAt",
        )))
        assertFalse(columns.contains("profileKey"))
        assertFalse(columns.contains("notes"))
    }

    @Test
    fun databaseSourceDeclaresPeopleWithoutDestructiveFallback() {
        val text = databaseSource()
        assertTrue(text.contains("version = 10"))
        assertTrue(text.contains("PersonEntity"))
        assertTrue(text.contains("MIGRATION_9_10"))
        assertTrue(text.contains("CREATE TABLE IF NOT EXISTS people"))
        assertTrue(text.contains("index_people_displayName"))
        assertTrue(text.contains("index_people_category"))
        assertTrue(text.contains("index_people_role"))
        assertTrue(text.contains("index_people_archivedAt"))
        assertFalse(text.contains("fallbackToDestructiveMigration"))
        assertFalse(text.contains("profileKey"))
        assertFalse(text.contains("IntelligenceEntity"))
    }

    private fun databaseSource(): String {
        val candidates = listOf(
            File("src/main/java/com/agbofa/smartoffice/data/persistence/SmartOfficeDatabase.kt"),
            File("app/src/main/java/com/agbofa/smartoffice/data/persistence/SmartOfficeDatabase.kt"),
        )
        return candidates.first { it.exists() }.readText()
    }
}
