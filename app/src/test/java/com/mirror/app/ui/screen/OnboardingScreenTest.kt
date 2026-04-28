package com.mirror.app.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirror.app.ui.screen.onboarding.OnboardingScreen
import com.mirror.app.ui.screen.onboarding.OnboardingViewModel
import com.mirror.app.ui.theme.MirrorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun launchOnboarding(onFinished: () -> Unit = {}) {
        val vm = OnboardingViewModel(emptyMockContainer())
        composeTestRule.setContent {
            MirrorTheme { OnboardingScreen(vm, onFinished) }
        }
    }

    @Test
    fun breathingPage_showsInstructions() {
        launchOnboarding()
        composeTestRule.onNodeWithText("Take three deep breaths").assertIsDisplayed()
        composeTestRule.onNodeWithText("Then let's see how you're really feeling.").assertIsDisplayed()
    }

    @Test
    fun breathingPage_showsNextButton() {
        launchOnboarding()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun breathingPage_showsBreathingAnimation() {
        launchOnboarding()
        // One of the two states is always visible
        val inhaleNode = composeTestRule.onNodeWithText("inhale")
        val exhaleNode = composeTestRule.onNodeWithText("exhale")
        try {
            inhaleNode.assertIsDisplayed()
        } catch (e: AssertionError) {
            exhaleNode.assertIsDisplayed()
        }
    }

    @Test
    fun onboardingViewModel_defaultScore_isThree() {
        var capturedScore = 0
        val vm = OnboardingViewModel(emptyMockContainer())
        // Collect the initial state
        capturedScore = vm.selectedScore.value
        assert(capturedScore == 3) { "Expected default score 3 but was $capturedScore" }
    }

    @Test
    fun onboardingViewModel_defaultAvatar_isTree() {
        val vm = OnboardingViewModel(emptyMockContainer())
        assert(vm.selectedAvatar.value == com.mirror.app.domain.model.AvatarType.TREE)
    }

    @Test
    fun onboardingViewModel_setScore_updatesState() {
        val vm = OnboardingViewModel(emptyMockContainer())
        vm.setScore(5)
        assert(vm.selectedScore.value == 5)
    }

    @Test
    fun onboardingViewModel_setAvatar_updatesState() {
        val vm = OnboardingViewModel(emptyMockContainer())
        vm.setAvatar(com.mirror.app.domain.model.AvatarType.ROBOT)
        assert(vm.selectedAvatar.value == com.mirror.app.domain.model.AvatarType.ROBOT)
    }
}
