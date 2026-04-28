package com.mirror.app.domain.usecase

import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.Scar
import java.time.LocalDate

class ComputeAvatarStateUseCase {

    fun compute(entries: List<MoodEntry>, scars: List<Scar>, today: LocalDate = LocalDate.now()): AvatarState {
        if (entries.isEmpty()) return AvatarState.Empty

        val sorted = entries.sortedBy { it.date }
        val latest = sorted.last()

        val isHibernating = java.time.temporal.ChronoUnit.DAYS.between(latest.date, today) > 14

        val healthScore = computeHealthScore(sorted)

        val recentAvg = run {
            val cutoff = today.minusDays(7)
            val recent = sorted.filter { !it.date.isBefore(cutoff) }
            if (recent.isEmpty()) 0.5f else recent.map { it.score }.average().toFloat() / 5f
        }

        val (lowMoodDays, hasLowMoodCounter) = computeLowMoodDays(sorted, today)

        val todayScore = entries.find { it.date == today }?.score ?: 0

        return AvatarState(
            moodScore = todayScore,
            healthScore = healthScore,
            recentAvg = recentAvg,
            isHibernating = isHibernating,
            scars = scars,
            hasLowMoodCounter = hasLowMoodCounter,
            lowMoodDays = lowMoodDays
        )
    }

    private fun computeHealthScore(sortedEntries: List<MoodEntry>): Float {
        val initialScore = sortedEntries.first().score
        var health = (initialScore - 1) / 4f

        for (entry in sortedEntries.drop(1)) {
            val delta = when (entry.score) {
                1 -> -0.25f
                2 -> -0.12f
                3 -> +0.01f
                4 -> +0.08f
                5 -> +0.18f
                else -> 0f
            }
            health = (health + delta).coerceIn(0f, 1f)
        }
        return health
    }

    private fun computeLowMoodDays(sortedEntries: List<MoodEntry>, today: LocalDate): Pair<Int, Boolean> {
        if (sortedEntries.isEmpty()) return Pair(0, false)

        var consecutiveLowDays = 0
        var maxConsecutiveLowDays = 0

        for (i in sortedEntries.indices) {
            val windowStart = sortedEntries[i].date.minusDays(6)
            val window = sortedEntries.filter { !it.date.isBefore(windowStart) && !it.date.isAfter(sortedEntries[i].date) }
            val avg = window.map { it.score }.average()

            if (avg < 4.0) {
                consecutiveLowDays++
                maxConsecutiveLowDays = maxOf(maxConsecutiveLowDays, consecutiveLowDays)
            } else {
                consecutiveLowDays = 0
            }
        }

        return Pair(maxConsecutiveLowDays, maxConsecutiveLowDays >= 14)
    }

    fun computeForDate(entries: List<MoodEntry>, scars: List<Scar>, targetDate: LocalDate): AvatarState {
        val upTo = entries.filter { !it.date.isAfter(targetDate) }
        return compute(upTo, scars, targetDate)
    }
}
