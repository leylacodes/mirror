package com.mirror.app.ui.screen.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mirror.app.ui.avatar.AvatarCanvas
import com.mirror.app.ui.component.MirrorFrame
import com.mirror.app.ui.component.MoodTimeline
import com.mirror.app.ui.theme.GoldAccent
import java.time.LocalDate

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onCheckIn: (String) -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Top bar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mirror", style = MaterialTheme.typography.headlineLarge, color = GoldAccent)
            Row {
                IconButton(onClick = onHistory) {
                    Icon(Icons.Default.DateRange, contentDescription = "History", tint = GoldAccent)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GoldAccent)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Mirror + avatar
        MirrorFrame(size = 280.dp) {
            Box(Modifier.fillMaxSize()) {
                AvatarCanvas(state.avatarState, state.avatarType, Modifier.fillMaxSize())
            }
        }

        Spacer(Modifier.height(20.dp))

        // Message
        Text(
            text = state.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Hibernation banner
        if (state.avatarState.isHibernating) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3A2E00), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "You've been away for a while.",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldAccent
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onCheckIn("reassessment") },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Reassess how you're feeling", color = Color.Black)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Low mood banner
        if (state.avatarState.hasLowMoodCounter) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3A0000), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "You've been feeling low for ${state.avatarState.lowMoodDays} days. Be kind to yourself.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFF8A80),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Check-in CTA
        val today = LocalDate.now().toString()
        if (state.todayEntry == null) {
            Button(
                onClick = { onCheckIn(today) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("How are you feeling today?", color = Color.Black, style = MaterialTheme.typography.titleLarge)
            }
        } else {
            OutlinedButton(
                onClick = { onCheckIn(today) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit today's check-in")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Timeline
        Text(
            "Last 7 days",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        MoodTimeline(
            entries = state.recentEntries,
            onEntryTapped = { date -> onCheckIn(date) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(40.dp))
    }
}
