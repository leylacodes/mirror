package com.mirror.app.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.ui.avatar.AvatarCanvas
import com.mirror.app.ui.component.scoreColor
import com.mirror.app.ui.theme.GoldAccent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: HistoryViewModel, onEdit: (String) -> Unit, onBack: () -> Unit) {
    val state by vm.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("History", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Slider", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Calendar", modifier = Modifier.padding(16.dp))
            }
        }
        when (selectedTab) {
            0 -> SliderHistoryTab(state, onEdit)
            1 -> CalendarHistoryTab(state, vm, onEdit)
        }
    }
}

@Composable
private fun SliderHistoryTab(state: HistoryUiState, onEdit: (String) -> Unit) {
    val sorted = state.entries.sortedByDescending { it.date }
    if (sorted.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No entries yet.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }
    val pagerState = rememberPagerState(pageCount = { sorted.size })
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        val entry = sorted[page]
        val avatarState = state.historicalStates[entry.date]
        val fmt = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(entry.date.format(fmt), style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            if (entry.isBackfilled) {
                Text(
                    "Estimated",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(16.dp))

            if (avatarState != null) {
                Box(
                    Modifier
                        .size(220.dp)
                        .alpha(if (entry.isBackfilled) 0.6f else 1f)
                ) {
                    AvatarCanvas(avatarState, state.avatarType)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Score: ${entry.score}", style = MaterialTheme.typography.headlineMedium, color = scoreColor(entry.score))
            if (entry.keywords.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(entry.keywords.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(24.dp))
            androidx.compose.material3.OutlinedButton(onClick = { onEdit(entry.date.toString()) }) {
                Text("Edit")
            }
        }
    }
}

@Composable
private fun CalendarHistoryTab(state: HistoryUiState, vm: HistoryViewModel, onEdit: (String) -> Unit) {
    val entryMap = state.entries.associateBy { it.date }
    val month = state.currentMonth
    val fmt = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Month navigation
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { vm.prevMonth() }) { Icon(Icons.Default.ChevronLeft, "Previous", tint = GoldAccent) }
            Text(month.format(fmt), style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            IconButton(onClick = { vm.nextMonth() }) { Icon(Icons.Default.ChevronRight, "Next", tint = GoldAccent) }
        }
        Spacer(Modifier.height(8.dp))

        // Day of week headers
        Row(Modifier.fillMaxWidth()) {
            DayOfWeek.values().forEach { dow ->
                Text(
                    dow.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        // Calendar grid
        val firstDay = month.atDay(1)
        val startOffset = firstDay.dayOfWeek.value % 7
        val daysInMonth = month.lengthOfMonth()

        val cells = startOffset + daysInMonth
        val rows = (cells + 6) / 7

        for (row in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val dayIndex = row * 7 + col - startOffset + 1
                    if (dayIndex < 1 || dayIndex > daysInMonth) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(dayIndex)
                        val entry = entryMap[date]
                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    if (entry != null) scoreColor(entry.score).copy(alpha = if (entry.isBackfilled) 0.3f else 0.8f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = if (entry?.isBackfilled == true) 1.dp else 0.dp,
                                    color = Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable(enabled = entry != null) { onEdit(date.toString()) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    dayIndex.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (entry != null) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                if (entry != null) {
                                    Text(
                                        entry.score.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
