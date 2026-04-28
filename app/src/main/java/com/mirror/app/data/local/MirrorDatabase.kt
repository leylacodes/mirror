package com.mirror.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mirror.app.data.local.converter.Converters
import com.mirror.app.data.local.dao.MoodEntryDao
import com.mirror.app.data.local.dao.ScarDao
import com.mirror.app.data.local.entity.MoodEntryEntity
import com.mirror.app.data.local.entity.ScarEntity

@Database(
    entities = [MoodEntryEntity::class, ScarEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MirrorDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun scarDao(): ScarDao
}
