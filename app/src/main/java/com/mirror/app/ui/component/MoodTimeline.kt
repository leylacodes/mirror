package com.mirror.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mirror.app.domain.model.MoodEntry
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MoodTimeline(
    entries: List<MoodEntry>,
    onEntryTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val entryMap = entries.associateBy { it.date }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days) { date ->
            val entry = entryMap[date]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (entry != null) scoreColor(entry.score).copy(
                                alpha = if (entry.isBackfilled) 0.4f else 1f
                            )
                            else Color.Gray.copy(alpha = 0.2f)
                        )
                        .border(
                            width = if (entry?.isBackfilled == true) 1.dp else 0.dp,
                            color = Color.Gray,
                            shape = CircleShape
                        )
                        .clickable(enabled = entry != null) { onEntryTapped(date.toString()) },
                    contentAlignment = Alignment.Center
                ) {
                    if (entry != null) {
                        Text(
                            text = entry.score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
