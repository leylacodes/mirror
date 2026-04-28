package com.mirror.app.ui.screen

import com.mirror.app.AppContainer
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.UserPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

val defaultPrefs = UserPreferences(
    onboardingComplete = true,
    avatarType = AvatarType.TREE,
    notifHour = 20,
    notifMinute = 0,
    notifEnabled = false
)

fun emptyMockContainer(): AppContainer = mockk<AppContainer>(relaxed = true).also { c ->
    every { c.moodRepository.observeAllEntries() } returns flowOf(emptyList())
    every { c.moodRepository.observeScars() } returns flowOf(emptyList())
    every { c.userPrefsDataStore.userPreferences } returns flowOf(defaultPrefs)
    every { c.computeAvatarStateUseCase.compute(any(), any()) } returns AvatarState.Empty
    every { c.computeAvatarStateUseCase.compute(any(), any(), any()) } returns AvatarState.Empty
    every { c.computeAvatarStateUseCase.computeForDate(any(), any(), any()) } returns AvatarState.Empty
    every { c.getAvatarMessageUseCase.execute(any(), any()) } returns "Steady in the breeze."
    coEvery { c.getKeywordSuggestionsUseCase.execute(any()) } returns listOf("work", "sleep", "exercise", "food")
    coEvery { c.moodRepository.getEntryForDate(any()) } returns null
}

fun seededMockContainer(entries: List<MoodEntry>): AppContainer =
    emptyMockContainer().also { c ->
        every { c.moodRepository.observeAllEntries() } returns flowOf(entries)
        coEvery { c.moodRepository.getEntryForDate(LocalDate.now()) } returns
            entries.find { it.date == LocalDate.now() }
        every { c.computeAvatarStateUseCase.compute(any(), any()) } returns AvatarState(
            moodScore = entries.lastOrNull()?.score ?: 3,
            healthScore = 0.6f,
            recentAvg = 0.6f,
            isHibernating = false,
            scars = emptyList(),
            hasLowMoodCounter = false,
            lowMoodDays = 0
        )
    }
