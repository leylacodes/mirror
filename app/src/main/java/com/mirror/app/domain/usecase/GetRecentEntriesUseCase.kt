package com.mirror.app.domain.usecase

import com.mirror.app.data.repository.MoodRepository
import com.mirror.app.domain.model.MoodEntry

class GetRecentEntriesUseCase(private val repository: MoodRepository) {
    suspend fun execute(limit: Int = 7): List<MoodEntry> =
        repository.getRecentEntries(limit)
}
