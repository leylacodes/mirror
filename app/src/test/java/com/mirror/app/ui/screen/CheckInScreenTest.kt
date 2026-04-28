package com.mirror.app.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.ui.screen.checkin.CheckInScreen
import com.mirror.app.ui.screen.checkin.CheckInViewModel
import com.mirror.app.ui.theme.MirrorTheme
import io.mockk.coEvery
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CheckInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val today = LocalDate.now().toString()

    @Test
    fun newCheckIn_showsDateTitle() {
        val vm = CheckInViewModel(emptyMockContainer(), today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("How are you feeling?").assertIsDisplayed()
    }

    @Test
    fun newCheckIn_showsKeywordsSection() {
        val vm = CheckInViewModel(emptyMockContainer(), today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("What contributed?").assertIsDisplayed()
    }

    @Test
    fun newCheckIn_showsSaveButton() {
        val vm = CheckInViewModel(emptyMockContainer(), today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun editCheckIn_showsEditTitle() {
        val existing = MoodEntry(LocalDate.now(), score = 3)
        val container = emptyMockContainer()
        coEvery { container.moodRepository.getEntryForDate(LocalDate.now()) } returns existing
        val vm = CheckInViewModel(container, today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Edit check-in").assertIsDisplayed()
    }

    @Test
    fun hibernationReassessment_showsExplanationText() {
        val vm = CheckInViewModel(emptyMockContainer(), "reassessment")
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("You've been away for a while. How are you feeling right now?")
            .assertIsDisplayed()
    }

    @Test
    fun keywordChips_areDisplayed() {
        val vm = CheckInViewModel(emptyMockContainer(), today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("work").assertIsDisplayed()
        composeTestRule.onNodeWithText("sleep").assertIsDisplayed()
    }

    @Test
    fun saveButton_triggersOnSaved() {
        var saved = false
        val container = emptyMockContainer()
        coEvery { container.saveMoodEntryUseCase.execute(any()) } returns Unit
        val vm = CheckInViewModel(container, today)
        composeTestRule.setContent {
            MirrorTheme { CheckInScreen(vm, onSaved = { saved = true }) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()
        assert(saved)
    }
}
