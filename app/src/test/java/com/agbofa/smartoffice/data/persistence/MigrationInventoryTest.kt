package com.agbofa.smartoffice.data.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MigrationInventoryTest {
    @Test
    fun registeredPathIsOneThroughNine() {
        val migrations = listOf(
            SmartOfficeDatabase.MIGRATION_1_2,
            SmartOfficeDatabase.MIGRATION_2_3,
            SmartOfficeDatabase.MIGRATION_3_4,
            SmartOfficeDatabase.MIGRATION_4_5,
            SmartOfficeDatabase.MIGRATION_5_6,
            SmartOfficeDatabase.MIGRATION_6_7,
            SmartOfficeDatabase.MIGRATION_7_8,
            SmartOfficeDatabase.MIGRATION_8_9,
        )
        assertEquals(8, migrations.size)
        assertEquals(1, migrations[0].startVersion)
        assertEquals(9, migrations.last().endVersion)
        assertEquals(8, migrations.last().startVersion)
        migrations.forEach { assertNotNull(it) }
    }
}
