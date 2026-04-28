package com.mirror.app.ui.component

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirror.app.ui.theme.GoldAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordChipRow(
    suggestions: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        suggestions.forEach { keyword ->
            val isSelected = keyword in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(keyword) },
                label = { Text(keyword, style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.padding(vertical = 2.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                    selectedLabelColor = GoldAccent
                )
            )
        }
    }
}
