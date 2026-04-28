package com.mirror.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mirror.app.domain.model.Scar
import java.time.format.DateTimeFormatter

@Composable
fun ScarBadge(
    scar: Scar,
    offsetX: Dp,
    offsetY: Dp,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Box(
        modifier = modifier
            .padding(start = offsetX, top = offsetY)
            .size(12.dp)
            .clip(CircleShape)
            .background(Color(0xFFD32F2F).copy(alpha = 0.8f))
            .clickable { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Scar") },
            text = {
                Column {
                    Text("A difficult period:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${scar.startDate.format(fmt)} – ${scar.endDate.format(fmt)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Close") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
