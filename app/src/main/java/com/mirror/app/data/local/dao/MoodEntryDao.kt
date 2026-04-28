package com.mirror.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mirror.app.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entry: MoodEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(entries: List<MoodEntryEntity>)

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    suspend fun getAll(): List<MoodEntryEntity>

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): MoodEntryEntity?

    @Query("SELECT * FROM mood_entries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<MoodEntryEntity>

    @Query("SELECT * FROM mood_entries WHERE date >= :fromDate ORDER BY date ASC")
    suspend fun getFrom(fromDate: String): List<MoodEntryEntity>
}
