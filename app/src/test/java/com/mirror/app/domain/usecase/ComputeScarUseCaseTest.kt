package com.mirror.app.domain.usecase

import com.mirror.app.domain.model.MoodEntry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ComputeScarUseCaseTest {

    private val mockRepo = mockk<com.mirror.app.data.repository.MoodRepository>(relaxed = true)
    private val useCase = ComputeScarUseCase(mockRepo)
    private val base = LocalDate.of(2026, 1, 1)

    @Test
    fun `fewer than 14 entries produces no scars`() {
        val entries = (0..12).map { MoodEntry(base.plusDays(it.toLong()), score = 1) }
        val scars = useCase.compute(entries)
        assertTrue(scars.isEmpty())
    }

    @Test
    fun `14 consecutive days averaging below 4 produces a scar`() {
        val entries = (0..13).map { MoodEntry(base.plusDays(it.toLong()), score = 2) }
        val scars = useCase.compute(entries)
        assertEquals(1, scars.size)
    }

    @Test
    fun `entries averaging 4 or above produce no scars`() {
        val entries = (0..20).map { MoodEntry(base.plusDays(it.toLong()), score = 4) }
        val scars = useCase.compute(entries)
        assertTrue(scars.isEmpty())
    }

    @Test
    fun `scar end date extends as low mood continues`() {
        val entries = (0..20).map { MoodEntry(base.plusDays(it.toLong()), score = 2) }
        val scars = useCase.compute(entries)
        assertEquals(1, scars.size)
        assertEquals(base.plusDays(20), scars[0].endDate)
    }

    @Test
    fun `computeAndPersist calls replaceScars`() = runTest {
        val entries = (0..20).map { MoodEntry(base.plusDays(it.toLong()), score = 2) }
        coEvery { mockRepo.replaceScars(any()) } returns Unit
        useCase.computeAndPersist(entries)
        io.mockk.coVerify { mockRepo.replaceScars(any()) }
    }
}
