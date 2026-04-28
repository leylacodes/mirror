package com.mirror.app.ui.screen.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.ui.avatar.AvatarCanvas
import com.mirror.app.ui.theme.GoldAccent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val prefs by vm.prefs.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    val notifPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else null

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(Modifier.fillMaxSize().padding(24.dp)) {

            // Avatar picker
            Text("Your avatar", style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            Spacer(Modifier.height(12.dp))
            val avatarLabels = mapOf(
                AvatarType.TREE to "Tree",
                AvatarType.ROBOT to "Robot",
                AvatarType.WATER_BUCKET to "Water Bucket",
                AvatarType.PILE_OF_SPOONS to "Pile of Spoons"
            )
            val previewState = AvatarState(4, 0.7f, 0.7f, false, emptyList(), false, 0)
            AvatarType.values().toList().chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { type ->
                        val isSelected = type == prefs.avatarType
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
                                .clickable { vm.setAvatarType(type) }
                                .padding(8.dp)
                        ) {
                            Box(Modifier.size(64.dp)) { AvatarCanvas(previewState, type) }
                            Text(
                                avatarLabels[type] ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(32.dp))

            // Notifications
            Text("Daily reminder", style = MaterialTheme.typography.titleLarge, color = GoldAccent)
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable notifications", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = prefs.notifEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && notifPermission != null && !notifPermission.status.isGranted) {
                            notifPermission.launchPermissionRequest()
                        } else {
                            vm.setNotifEnabled(enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = GoldAccent)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Reminder time: %02d:%02d".format(prefs.notifHour, prefs.notifMinute),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { showTimePicker = true }) {
                    Text("Change", color = GoldAccent)
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = prefs.notifHour,
            initialMinute = prefs.notifMinute
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    TextButton(onClick = {
                        vm.setNotifTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("OK", color = GoldAccent) }
                }
            }
        }
    }
}
