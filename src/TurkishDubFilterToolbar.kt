package com.nuvio.app.features.streams

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Filters the visible results; no addon or provider is switched off. */
@Composable
internal fun TurkishDubFilterToolbar(
    mode: TurkishAudioFilterMode,
    onModeSelected: (TurkishAudioFilterMode) -> Unit,
    visibleCount: Int,
    totalCount: Int,
    eligibleCount: Int = 0,
    inspectedCount: Int = 0,
    isInspecting: Boolean = false,
    onInspect: () -> Unit = {},
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = eligibleCount > inspectedCount && !isInspecting,
                onClick = onInspect,
            ) {
                Text(if (isInspecting) "Ses verisi taranıyor…" else "HLS/DASH ses izlerini tara")
            }
        }
        Text(
            text = "$visibleCount / $totalCount kaynak · HLS/DASH taranan: $inspectedCount/$eligibleCount. " +
                "'TR Ses' manifestin dil bildirimidir, konuşma dili dinlenerek doğrulanmadı.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
