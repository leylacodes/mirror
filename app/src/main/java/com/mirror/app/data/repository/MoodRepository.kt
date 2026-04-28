package com.mirror.app.data.repository

import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.Scar
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MoodRepository {
    fun observeAllEntries(): Flow<List<MoodEntry>>
    suspend fun getAllEntries(): List<MoodEntry>
    suspend fun getEntryForDate(date: LocalDate): MoodEntry?
    suspend fun getRecentEntries(limit: Int): List<MoodEntry>
    suspend fun saveEntry(entry: MoodEntry)
    suspend fun saveEntries(entries: List<MoodEntry>)
    fun observeScars(): Flow<List<Scar>>
    suspend fun getScars(): List<Scar>
    suspend fun replaceScars(scars: List<Scar>)
}
