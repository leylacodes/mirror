package com.mirror.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mirror.app.ui.theme.GoldAccent
import com.mirror.app.ui.theme.ScoreHigh
import com.mirror.app.ui.theme.ScoreLow
import com.mirror.app.ui.theme.ScoreMedHigh
import com.mirror.app.ui.theme.ScoreMedLow
import com.mirror.app.ui.theme.ScoreMid

@Composable
fun MoodSlider(
    selectedScore: Int,
    onScoreSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { score ->
            val isSelected = score == selectedScore
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) scoreColor(score) else scoreColor(score).copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) GoldAccent else scoreColor(score).copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onScoreSelected(score) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun scoreColor(score: Int): Color = when (score) {
    1 -> ScoreLow
    2 -> ScoreMedLow
    3 -> ScoreMid
    4 -> ScoreMedHigh
    5 -> ScoreHigh
    else -> ScoreMid
}
