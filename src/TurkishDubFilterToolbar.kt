package com.nuvio.app.features.streams

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Accessible in-list toggle that works with *all* enabled addons and plugins. */
@Composable
internal fun TurkishDubFilterToolbar(
    mode: TurkishAudioFilterMode,
    onModeSelected: (TurkishAudioFilterMode) -> Unit,
    visibleCount: Int,
    totalCount: Int,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TurkishAudioFilterMode.entries.forEach { option ->
                FilterChip(
                    selected = mode == option,
                    onClick = { onModeSelected(option) },
                    label = { Text(option.label) },
                )
            }
        }
        Text(
            text = "$visibleCount / $totalCount kaynak · Türkçe ses tahmini sadece sağlayıcı etiketlerindendir; ses izi doğrulanmadı.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
