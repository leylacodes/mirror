package com.mirror.app.ui.screen.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mirror.app.ui.component.KeywordChipRow
import com.mirror.app.ui.component.MoodSlider
import com.mirror.app.ui.theme.GoldAccent
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(vm: CheckInViewModel, onSaved: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val fmt = DateTimeFormatter.ofPattern("EEEE, MMMM d")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (state.isHibernationReassessment) "How are you feeling?"
                    else if (state.isEditingExisting) "Edit check-in"
                    else state.date.format(fmt),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onSaved) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            if (state.isHibernationReassessment) {
                Text(
                    "You've been away for a while. How are you feeling right now?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "This score will be used to fill in the days you were away.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(24.dp))
            }

            Text("How are you feeling?", style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            Spacer(Modifier.height(16.dp))
            MoodSlider(selectedScore = state.score, onScoreSelected = { vm.setScore(it) })

            Spacer(Modifier.height(32.dp))
            Text("What contributed?", style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            Spacer(Modifier.height(12.dp))
            KeywordChipRow(
                suggestions = state.suggestions,
                selected = state.selectedKeywords,
                onToggle = { vm.toggleKeyword(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))
            Button(
                onClick = { vm.save(onSaved) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Save", color = Color.Black, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
