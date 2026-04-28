package com.mirror.app.data.repository

import com.mirror.app.data.local.dao.MoodEntryDao
import com.mirror.app.data.local.dao.ScarDao
import com.mirror.app.data.local.entity.MoodEntryEntity
import com.mirror.app.data.local.entity.ScarEntity
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.Scar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class MoodRepositoryImpl(
    private val moodEntryDao: MoodEntryDao,
    private val scarDao: ScarDao
) : MoodRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeAllEntries(): Flow<List<MoodEntry>> =
        moodEntryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllEntries(): List<MoodEntry> =
        moodEntryDao.getAll().map { it.toDomain() }

    override suspend fun getEntryForDate(date: LocalDate): MoodEntry? =
        moodEntryDao.getByDate(date.toString())?.toDomain()

    override suspend fun getRecentEntries(limit: Int): List<MoodEntry> =
        moodEntryDao.getRecent(limit).map { it.toDomain() }

    override suspend fun saveEntry(entry: MoodEntry) =
        moodEntryDao.insertOrReplace(entry.toEntity())

    override suspend fun saveEntries(entries: List<MoodEntry>) =
        moodEntryDao.insertOrReplaceAll(entries.map { it.toEntity() })

    override fun observeScars(): Flow<List<Scar>> =
        scarDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getScars(): List<Scar> =
        scarDao.getAll().map { it.toDomain() }

    override suspend fun replaceScars(scars: List<Scar>) {
        scarDao.deleteAll()
        if (scars.isNotEmpty()) scarDao.insertOrReplaceAll(scars.map { it.toEntity() })
    }

    private fun MoodEntryEntity.toDomain() = MoodEntry(
        date = LocalDate.parse(date),
        score = score,
        keywords = runCatching { json.decodeFromString<List<String>>(keywords) }.getOrDefault(emptyList()),
        isBackfilled = isBackfilled
    )

    private fun MoodEntry.toEntity() = MoodEntryEntity(
        date = date.toString(),
        score = score,
        keywords = json.encodeToString(keywords),
        isBackfilled = isBackfilled,
        updatedAt = System.currentTimeMillis()
    )

    private fun ScarEntity.toDomain() = Scar(
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate)
    )

    private fun Scar.toEntity() = ScarEntity(
        startDate = startDate.toString(),
        endDate = endDate.toString()
    )
}
