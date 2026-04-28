package com.mirror.app.domain.usecase

import com.mirror.app.data.repository.MoodRepository
import com.mirror.app.domain.model.MoodEntry

class EditMoodEntryUseCase(
    private val repository: MoodRepository,
    private val computeScarUseCase: ComputeScarUseCase
) {
    suspend fun execute(entry: MoodEntry) {
        repository.saveEntry(entry.copy(isBackfilled = false))
        val allEntries = repository.getAllEntries()
        computeScarUseCase.computeAndPersist(allEntries)
    }
}
