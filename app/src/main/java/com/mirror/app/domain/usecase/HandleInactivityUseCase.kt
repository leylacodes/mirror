package com.mirror.app.domain.usecase

import com.mirror.app.data.repository.MoodRepository
import com.mirror.app.domain.model.MoodEntry
import java.time.LocalDate

class HandleInactivityUseCase(
    private val repository: MoodRepository,
    private val computeScarUseCase: ComputeScarUseCase
) {
    suspend fun execute(newScore: Int) {
        val allEntries = repository.getAllEntries()
        val lastDate = allEntries.maxOfOrNull { it.date } ?: return
        val today = LocalDate.now()

        val backfilledEntries = mutableListOf<MoodEntry>()
        var current = lastDate.plusDays(1)
        while (!current.isAfter(today.minusDays(1))) {
            backfilledEntries.add(
                MoodEntry(date = current, score = newScore, isBackfilled = true)
            )
            current = current.plusDays(1)
        }

        if (backfilledEntries.isNotEmpty()) {
            repository.saveEntries(backfilledEntries)
        }

        repository.saveEntry(MoodEntry(date = today, score = newScore))

        val updated = repository.getAllEntries()
        computeScarUseCase.computeAndPersist(updated)
    }
}
