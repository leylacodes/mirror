package com.mirror.app.domain.usecase

import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.MoodEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ComputeAvatarStateUseCaseTest {

    private val useCase = ComputeAvatarStateUseCase()
    private val today = LocalDate.of(2026, 4, 28)

    @Test
    fun `empty entries returns Empty`() {
        val result = useCase.compute(emptyList(), emptyList(), today)
        assertEquals(AvatarState.Empty, result)
    }

    @Test
    fun `single score-5 entry produces high healthScore`() {
        val entries = listOf(MoodEntry(today, score = 5))
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(1.0f, result.healthScore, 0.001f)
    }

    @Test
    fun `single score-1 entry produces low healthScore`() {
        val entries = listOf(MoodEntry(today, score = 1))
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(0.0f, result.healthScore, 0.001f)
    }

    @Test
    fun `healthScore accumulates deltas across entries`() {
        val base = LocalDate.of(2026, 4, 20)
        val entries = listOf(
            MoodEntry(base, score = 3),          // initial: (3-1)/4 = 0.5
            MoodEntry(base.plusDays(1), score = 5), // +0.18 → 0.68
            MoodEntry(base.plusDays(2), score = 5), // +0.18 → 0.86
        )
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(0.86f, result.healthScore, 0.001f)
    }

    @Test
    fun `healthScore is clamped to 0 and 1`() {
        val base = LocalDate.of(2026, 4, 1)
        val entries = (0..20).map { MoodEntry(base.plusDays(it.toLong()), score = 1) }
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(0.0f, result.healthScore, 0.001f)
    }

    @Test
    fun `recentAvg is 7-day trailing average normalized to 0-1`() {
        val entries = (0..6).map { i ->
            MoodEntry(today.minusDays((6 - i).toLong()), score = 5)
        }
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(1.0f, result.recentAvg, 0.001f)
    }

    @Test
    fun `gap greater than 14 days triggers hibernation`() {
        val oldEntry = MoodEntry(today.minusDays(15), score = 3)
        val result = useCase.compute(listOf(oldEntry), emptyList(), today)
        assertTrue(result.isHibernating)
    }

    @Test
    fun `gap of exactly 14 days does not trigger hibernation`() {
        val entry = MoodEntry(today.minusDays(14), score = 3)
        val result = useCase.compute(listOf(entry), emptyList(), today)
        assertFalse(result.isHibernating)
    }

    @Test
    fun `moodScore reflects today entry score`() {
        val entries = listOf(MoodEntry(today, score = 4))
        val result = useCase.compute(entries, emptyList(), today)
        assertEquals(4, result.moodScore)
    }

    @Test
    fun `computeForDate uses only entries up to targetDate`() {
        val entries = listOf(
            MoodEntry(today.minusDays(2), score = 5),
            MoodEntry(today.minusDays(1), score = 5),
            MoodEntry(today, score = 1),
        )
        val resultAtYesterday = useCase.computeForDate(entries, emptyList(), today.minusDays(1))
        val resultAtToday = useCase.computeForDate(entries, emptyList(), today)

        assertTrue(resultAtYesterday.healthScore > resultAtToday.healthScore)
    }
}
