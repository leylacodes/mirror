package com.mirror.app.domain.usecase

import com.mirror.app.data.repository.MoodRepository
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.Scar
import java.time.LocalDate

class ComputeScarUseCase(private val repository: MoodRepository) {

    suspend fun computeAndPersist(entries: List<MoodEntry>) {
        val scars = compute(entries)
        repository.replaceScars(scars)
    }

    fun compute(entries: List<MoodEntry>): List<Scar> {
        if (entries.size < 14) return emptyList()

        val sorted = entries.sortedBy { it.date }
        val scars = mutableListOf<Scar>()

        var scarStart: LocalDate? = null
        var consecutiveLowDays = 0

        for (i in sorted.indices) {
            val windowStart = sorted[i].date.minusDays(6)
            val window = sorted.filter { !it.date.isBefore(windowStart) && !it.date.isAfter(sorted[i].date) }
            val avg = window.map { it.score }.average()

            if (avg < 4.0) {
                if (scarStart == null) scarStart = sorted[i].date
                consecutiveLowDays++

                if (consecutiveLowDays >= 14) {
                    val existingScar = scars.lastOrNull()
                    if (existingScar != null && existingScar.startDate == scarStart) {
                        scars[scars.lastIndex] = existingScar.copy(endDate = sorted[i].date)
                    } else if (existingScar == null || existingScar.endDate != sorted[i].date) {
                        if (consecutiveLowDays == 14) {
                            scars.add(Scar(startDate = scarStart!!, endDate = sorted[i].date))
                        } else {
                            val last = scars.lastOrNull()
                            if (last != null) scars[scars.lastIndex] = last.copy(endDate = sorted[i].date)
                        }
                    }
                }
            } else {
                scarStart = null
                consecutiveLowDays = 0
            }
        }

        return scars
    }
}
