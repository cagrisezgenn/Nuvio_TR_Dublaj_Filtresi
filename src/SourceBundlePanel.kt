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
 * Exactly 29 Nuvio entries, imported only by explicit user actions.
 * Existing addon and plugin enablement is not reset or modified.
 * A failed remote manifest is reported and skipped.
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
        Text("Yerleşik 29 Nuvio kaynağı", style = MaterialTheme.typography.titleMedium)
        Text(
            "16 eklenti ve 13 plugin deposu. CloudStream/Play kaynakları bu pakette yoktur. " +
                "Kaynaklar siz onaylamadan kurulmaz; mevcut sağlayıcılarınız korunur.",
            style = MaterialTheme.typography.bodySmall,
        )
        Button(enabled = !busy, onClick = {
            busy = true
            scope.launch {
                var added = 0
                var existing = 0
                val failed = mutableListOf<String>()
                try {
                    AddonRepository.initialize()
                    for (source in BundledMediaSources.nuvioAddons) {
                        message = "Eklenti kontrolü: ${source.name}"
                        if (AddonRepository.uiState.value.addons.any { it.manifestUrl.trimEnd('/') == source.url.trimEnd('/') }) {
                            existing++
                            continue
                        }
                        when (AddonRepository.addAddon(source.url)) {
                            is AddAddonResult.Success -> added++
                            else -> failed += source.name
                        }
                    }
                    message = "Eklentiler: $added eklendi, $existing mevcut, ${failed.size} başarısız." +
                        if (failed.isNotEmpty()) " Başarısızlar: ${failed.joinToString(", ")}" else ""
                } finally { busy = false }
            }
        }) {
            Text(if (busy) "Eklentiler kontrol ediliyor..." else "16 Nuvio eklentisini ekle")
        }
        OutlinedButton(enabled = !busy, onClick = {
            busy = true
            scope.launch {
                var added = 0
                var existing = 0
                val failed = mutableListOf<String>()
                try {
                    PluginRepository.initialize()
                    for (source in BundledMediaSources.nuvioPlugins) {
                        message = "Plugin deposu kontrolü: ${source.name}"
                        if (PluginRepository.uiState.value.repositories.any { it.manifestUrl.trimEnd('/') == source.url.trimEnd('/') }) {
                            existing++
                            continue
                        }
                        when (PluginRepository.addRepository(source.url)) {
                            is AddPluginRepositoryResult.Success -> added++
                            else -> failed += source.name
                        }
                    }
                    message = "Pluginler: $added eklendi, $existing mevcut, ${failed.size} başarısız." +
                        if (failed.isNotEmpty()) " Başarısızlar: ${failed.joinToString(", ")}" else ""
                } finally { busy = false }
            }
        }) {
            Text("13 Nuvio plugin deposunu ekle")
        }
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodySmall)
        OutlinedButton(enabled = !busy, onClick = { detailsVisible = !detailsVisible }) {
            Text(if (detailsVisible) "29 kaynağı gizle" else "29 kaynağı göster")
        }
        if (detailsVisible) {
            Text(
                "Aşağıdaki liste 22 Ekim 2026 tarihli Repo Manager kaydından dondurulmuştur. " +
                    "MANİTUX kaynakta eklenti yazıyor ancak manifest yapısına göre plugin olarak yüklenir. " +
                    "Manifest erişimi başarısız olan adresler katalogda kalır, yüklemede atlanır.",
                style = MaterialTheme.typography.bodySmall,
            )
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BundledMediaSources.nuvioSources.forEachIndexed { index, source ->
                        Text(
                            "${index + 1}. ${source.name} (${if (source.kind == SourceKind.ADDON) "Eklenti" else "Plugin"})\n${source.url}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Text(
                "ÖNEMLİ: İçeriğin veya sağlayıcının contentLanguage: tr olarak işaretlenmesi Türkçe dublaj " +
                    "kanıtı değildir. Tekil akışın ses kanalı oynatıcıda doğrulanmalıdır.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
