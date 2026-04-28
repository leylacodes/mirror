package com.mirror.app.ui.screen.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.ui.avatar.AvatarCanvas
import com.mirror.app.ui.component.MoodSlider
import com.mirror.app.ui.theme.GoldAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(vm: OnboardingViewModel, onFinished: () -> Unit) {
    val score by vm.selectedScore.collectAsState()
    val avatar by vm.selectedAvatar.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> BreathingPage()
                1 -> MoodRatingPage(score = score, onScore = { vm.setScore(it) })
                2 -> AvatarPickerPage(selected = avatar, onSelect = { vm.setAvatar(it) })
                3 -> ExplainerPage()
            }
        }

        // Page dots
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { i ->
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == i) GoldAccent else Color.Gray)
                )
            }
        }

        // Next / Finish button
        Button(
            onClick = {
                if (pagerState.currentPage < 3) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    vm.finish(onFinished)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
        ) {
            Text(
                text = if (pagerState.currentPage < 3) "Next" else "Let's go",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun BreathingPage() {
    var expanded by remember { mutableStateOf(false) }
    val circleSize by animateDpAsState(
        targetValue = if (expanded) 240.dp else 80.dp,
        animationSpec = tween(durationMillis = 4000),
        label = "breathe"
    )

    LaunchedEffect(Unit) {
        while (true) {
            expanded = true
            delay(4200)
            expanded = false
            delay(4200)
        }
    }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(GoldAccent.copy(alpha = 0.3f))
                .border(2.dp, GoldAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(if (expanded) "exhale" else "inhale", color = GoldAccent)
        }
        Spacer(Modifier.height(40.dp))
        Text(
            "Take three deep breaths",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Then let's see how you're really feeling.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MoodRatingPage(score: Int, onScore: (Int) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "How are you feeling?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "1 = worst, 5 = most awesome",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(48.dp))
        MoodSlider(selectedScore = score, onScoreSelected = onScore)
    }
}

@Composable
private fun AvatarPickerPage(selected: AvatarType, onSelect: (AvatarType) -> Unit) {
    val avatars = AvatarType.values()
    val previewState = AvatarState(
        moodScore = 4, healthScore = 0.7f, recentAvg = 0.7f,
        isHibernating = false, scars = emptyList(),
        hasLowMoodCounter = false, lowMoodDays = 0
    )
    val labels = mapOf(
        AvatarType.TREE to "Tree",
        AvatarType.ROBOT to "Robot",
        AvatarType.WATER_BUCKET to "Water Bucket",
        AvatarType.PILE_OF_SPOONS to "Pile of Spoons"
    )

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Choose your avatar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Pick the one that feels most like your inner self.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            avatars.toList().chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { type ->
                        val isSelected = type == selected
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldAccent else Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelect(type) }
                                .padding(8.dp)
                        ) {
                            Box(Modifier.size(80.dp)) {
                                AvatarCanvas(previewState, type)
                            }
                            Text(
                                labels[type] ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplainerPage() {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your mirror", style = MaterialTheme.typography.headlineLarge, color = GoldAccent)
        Spacer(Modifier.height(24.dp))
        Text(
            "As you track your mood each day, your avatar will change to reflect your inner state.\n\n" +
                "Good days make it flourish. Hard days leave their mark.\n\n" +
                "It's not about being perfect. It's about seeing yourself clearly.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
