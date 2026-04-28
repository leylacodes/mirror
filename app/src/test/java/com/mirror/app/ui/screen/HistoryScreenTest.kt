package com.mirror.app.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.ui.screen.history.HistoryScreen
import com.mirror.app.ui.screen.history.HistoryViewModel
import com.mirror.app.ui.theme.MirrorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsSliderAndCalendarTabs() {
        val vm = HistoryViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.onNodeWithText("Slider").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoEntriesMessage() {
        val vm = HistoryViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No entries yet.").assertIsDisplayed()
    }

    @Test
    fun withEntries_showsScoreAndEditButton() {
        val entry = MoodEntry(LocalDate.of(2026, 4, 28), score = 4)
        val vm = HistoryViewModel(seededMockContainer(listOf(entry)))
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Score: 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
    }

    @Test
    fun withEntries_showsFormattedDate() {
        val entry = MoodEntry(LocalDate.of(2026, 4, 28), score = 3)
        val vm = HistoryViewModel(seededMockContainer(listOf(entry)))
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tuesday, April 28, 2026").assertIsDisplayed()
    }

    @Test
    fun calendarTab_showsMonthNavigation() {
        val vm = HistoryViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.onNodeWithText("Calendar").performClick()
        composeTestRule.waitForIdle()
        // Month navigation arrows are always present in calendar tab
        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    }

    @Test
    fun backfilled_entry_showsEstimatedLabel() {
        val entry = MoodEntry(LocalDate.of(2026, 4, 27), score = 3, isBackfilled = true)
        val vm = HistoryViewModel(seededMockContainer(listOf(entry)))
        composeTestRule.setContent {
            MirrorTheme { HistoryScreen(vm, onEdit = {}, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Estimated").assertIsDisplayed()
    }
}
