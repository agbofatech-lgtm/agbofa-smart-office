package com.agbofa.smartoffice.data.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Source-level inventory of registered migrations.
 * Does not execute Room against a device database.
 */
class MigrationInventoryTest {
    @Test
    fun registeredPathIsOneThroughSeven() {
        val migrations = listOf(
            SmartOfficeDatabase.MIGRATION_1_2,
            SmartOfficeDatabase.MIGRATION_2_3,
            SmartOfficeDatabase.MIGRATION_3_4,
            SmartOfficeDatabase.MIGRATION_4_5,
            SmartOfficeDatabase.MIGRATION_5_6,
            SmartOfficeDatabase.MIGRATION_6_7,
        )
        assertEquals(6, migrations.size)
        assertEquals(1, migrations[0].startVersion)
        assertEquals(2, migrations[0].endVersion)
        assertEquals(6, migrations[5].startVersion)
        assertEquals(7, migrations[5].endVersion)
        migrations.forEach { assertNotNull(it) }
    }
}
