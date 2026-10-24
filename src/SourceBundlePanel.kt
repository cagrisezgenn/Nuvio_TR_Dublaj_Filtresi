package com.nuvio.app.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nuvio.app.features.addons.AddAddonResult
import com.nuvio.app.features.addons.AddonRepository
import com.nuvio.app.features.plugins.AddPluginRepositoryResult
import com.nuvio.app.features.plugins.PluginRepository
import kotlinx.coroutines.launch

/**
 * User-controlled import, no automatic remote code execution. Existing
 * addon/provider preferences are never disabled, removed or reset here.
 */
@Composable
internal fun SourceBundlePanel() {
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var detailsVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Yerleşik kaynak paketi", style = MaterialTheme.typography.titleMedium)
        Text(
            "Nuvio uyumlu kaynak adresleri uygulamaya gömülüdür. Topluca ekleyebilirsiniz; mevcut kaynaklarınız kapatılmaz veya silinmez.",
            style = MaterialTheme.typography.bodySmall,
        )
        Button(enabled = !busy, onClick = {
            busy = true
            scope.launch {
                var added = 0
                var existing = 0
                var failed = 0
                try {
                    AddonRepository.initialize()
                    for (source in BundledMediaSources.nuvioAddons) {
                        message = "Eklenti kontrolü: ${source.name}"
                        if (AddonRepository.uiState.value.addons.any { it.manifestUrl == source.url }) {
                            existing++
                            continue
                        }
                        when (AddonRepository.addAddon(source.url)) {
                            is AddAddonResult.Success -> added++
                            else -> failed++
                        }
                    }
                    message = "Nuvio eklentileri: $added eklendi, $existing mevcut, $failed yüklenemedi."
                } finally { busy = false }
            }
        }) {
            Text(if (busy) "Kaynaklar kontrol ediliyor..." else "Nuvio eklentilerini ekle (${BundledMediaSources.nuvioAddons.size})")
        }

        OutlinedButton(enabled = !busy, onClick = {
            busy = true
            scope.launch {
                var added = 0
                var existing = 0
                var failed = 0
                try {
                    PluginRepository.initialize()
                    for (source in BundledMediaSources.nuvioPlugins) {
                        message = "Plugin deposu kontrolü: ${source.name}"
                        if (PluginRepository.uiState.value.repositories.any { it.manifestUrl == source.url }) {
                            existing++
                            continue
                        }
                        when (PluginRepository.addRepository(source.url)) {
                            is AddPluginRepositoryResult.Success -> added++
                            else -> failed++
                        }
                    }
                    message = "Nuvio plugin depoları: $added eklendi, $existing mevcut, $failed yüklenemedi."
                } finally { busy = false }
            }
        }) {
            Text("Nuvio plugin depolarını ekle (${BundledMediaSources.nuvioPlugins.size})")
        }

        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodySmall)
        OutlinedButton(enabled = !busy, onClick = { detailsVisible = !detailsVisible }) {
            Text("CloudStream kataloğu (${BundledMediaSources.cloudStreamRepositories.size}) " +
                if (detailsVisible) "gizle" else "göster")
        }

        if (detailsVisible) {
            Text(
                "Bu 15 URL, CSRepoManager_User.apk'den alınmıştır. CloudStream repo.json / cs3 dosyaları Nuvio Mobile'ın normal eklenti veya JS plugin formatına uymaz; otomatik kurulmaz veya oynatılmaz.",
                style = MaterialTheme.typography.bodySmall,
            )
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BundledMediaSources.cloudStreamRepositories.forEach { source ->
                        Text("${source.name}\n${source.url}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text(
                "Sağlayıcı açıklamalarında Türkçe dublaj desteği bildirilenler: " +
                    BundledMediaSources.reportedDubCapableProviders.joinToString(", ") +
                    ". Bu bildirim tekil akışın Türkçe ses izi bulunduğu anlamına gelmez.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
