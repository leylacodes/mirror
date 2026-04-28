package com.mirror.app.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.ui.screen.settings.SettingsScreen
import com.mirror.app.ui.screen.settings.SettingsViewModel
import com.mirror.app.ui.theme.MirrorTheme
import io.mockk.coVerify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsAvatarSection() {
        val vm = SettingsViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Your avatar").assertIsDisplayed()
    }

    @Test
    fun showsAllFourAvatarOptions() {
        val vm = SettingsViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tree").assertIsDisplayed()
        composeTestRule.onNodeWithText("Robot").assertIsDisplayed()
        composeTestRule.onNodeWithText("Water Bucket").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pile of Spoons").assertIsDisplayed()
    }

    @Test
    fun showsDailyReminderSection() {
        val vm = SettingsViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Daily reminder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enable notifications").assertIsDisplayed()
    }

    @Test
    fun showsDefaultReminderTime() {
        val vm = SettingsViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Reminder time: 20:00").assertIsDisplayed()
    }

    @Test
    fun tappingAvatarOption_callsSetAvatarType() {
        val container = emptyMockContainer()
        val vm = SettingsViewModel(container)
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Robot").performClick()
        composeTestRule.waitForIdle()
        coVerify { container.userPrefsDataStore.setAvatarType(AvatarType.ROBOT) }
    }

    @Test
    fun showsChangeButtonForReminderTime() {
        val vm = SettingsViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { SettingsScreen(vm, onBack = {}) }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Change").assertIsDisplayed()
    }
}
