package com.agbofa.smartoffice.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CaptureEntity::class,
        JournalEntryEntity::class,
        ClassificationEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class SmartOfficeDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun classificationDao(): ClassificationDao

    companion object {
        const val NAME = "smart-office.db"

        fun create(context: Context): SmartOfficeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmartOfficeDatabase::class.java,
                NAME,
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
