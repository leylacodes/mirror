package com.mirror.app.ui.avatar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType

@Composable
fun AvatarCanvas(
    state: AvatarState,
    avatarType: AvatarType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        when (avatarType) {
            AvatarType.TREE -> drawTree(state, shimmer)
            AvatarType.ROBOT -> drawRobot(state, shimmer)
            AvatarType.WATER_BUCKET -> drawWaterBucket(state, wavePhase)
            AvatarType.PILE_OF_SPOONS -> drawPileOfSpoons(state, shimmer)
        }
    }
}
