package com.mirror.app.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.ui.screen.home.HomeScreen
import com.mirror.app.ui.screen.home.HomeViewModel
import com.mirror.app.ui.theme.MirrorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsStartCheckInButton() {
        val vm = HomeViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HomeScreen(vm, onCheckIn = {}, onHistory = {}, onSettings = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Start today's check-in").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsAvatarMessage() {
        val vm = HomeViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HomeScreen(vm, onCheckIn = {}, onHistory = {}, onSettings = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Steady in the breeze.").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsLastSevenDaysSection() {
        val vm = HomeViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { HomeScreen(vm, onCheckIn = {}, onHistory = {}, onSettings = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Last 7 days").assertIsDisplayed()
    }

    @Test
    fun withTodayEntry_showsEditCheckInButton() {
        val todayEntry = MoodEntry(LocalDate.now(), score = 4)
        val vm = HomeViewModel(seededMockContainer(listOf(todayEntry)))
        composeTestRule.setContent {
            MirrorTheme { HomeScreen(vm, onCheckIn = {}, onHistory = {}, onSettings = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Edit today's check-in").assertIsDisplayed()
    }
}
