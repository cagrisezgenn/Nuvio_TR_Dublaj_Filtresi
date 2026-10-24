package com.nuvio.tv.ui.screens.stream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.nuvio.tv.domain.model.Stream

/** TV display-only filter. Source provider language 'tr' is never audio evidence. */
internal enum class TvDubMode(val title: String) {
    ALL("Tümü"), EXPLICIT("TR Dublaj"), PROBABLE("Olası"), UNKNOWN("Belirsiz")
}
internal object TvDubFilter {
    private val explicit = Regex(
        """(?:türkçe|turkce|turkish|tr)[\s._:/-]*(?:dublaj(?:lı|li)?|dub|audio|ses(?:lendirme)?)|(?:dublaj(?:lı|li)?|dub|audio|ses)[\s._:/-]*(?:türkçe|turkce|turkish|tr)""",
        RegexOption.IGNORE_CASE
    )
    private val possible = Regex("""\bdublaj(?:lı|li)?\b|\bdubbed\b""", RegexOption.IGNORE_CASE)
    private val negative = Regex(
        """sadece altyazı|yalnızca altyazı|dublaj yok|english dub|ingilizce dublaj|japanese dub|japonca dublaj""",
        RegexOption.IGNORE_CASE
    )
    fun classification(stream: Stream): TvDubMode {
        val label = listOfNotNull(stream.name, stream.title, stream.description).joinToString(" ")
        if (negative.containsMatchIn(label)) return TvDubMode.UNKNOWN
        if (explicit.containsMatchIn(label)) return TvDubMode.EXPLICIT
        if (possible.containsMatchIn(label)) return TvDubMode.PROBABLE
        return TvDubMode.UNKNOWN
    }
    fun filter(streams: List<Stream>, mode: TvDubMode): List<Stream> =
        if (mode == TvDubMode.ALL) streams
        else streams.filter { classification(it) == mode }
}
@Composable
internal fun TvDubFocusBar(
    selected: TvDubMode,
    onSelect: (TvDubMode) -> Unit,
    shown: Int,
    total: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 10.dp),
    ) {
        TvDubMode.entries.forEach { mode ->
            Button(onClick = { onSelect(mode) }) {
                Text(if (selected == mode) "✓ ${mode.title}" else mode.title)
            }
        }
    }
}
