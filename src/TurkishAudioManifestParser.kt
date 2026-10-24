package com.nuvio.app.features.streams

/** Reads DECLARED audio languages in HLS/DASH playlist metadata, not spoken audio. */
internal enum class DeclaredAudioLanguage { TURKISH, OTHER, UNKNOWN }
internal data class DeclaredAudioResult(val status: DeclaredAudioLanguage, val details: String)

internal object TurkishAudioManifestParser {
    private val attrPattern = Regex("""([A-Za-z0-9:_-]+)\s*=\s*(?:"([^"]*)"|'([^']*)'|([^,\s>]+))""")
    private val hlsAudio = Regex("""^\s*#EXT-X-MEDIA\s*:\s*(.+)$""", RegexOption.IGNORE_CASE)
    private val adaptation = Regex("""<(?:[\w-]+:)?AdaptationSet\b([^>]*)>""", RegexOption.IGNORE_CASE)
    private val adaptationEnd = Regex("""</(?:[\w-]+:)?AdaptationSet\s*>""", RegexOption.IGNORE_CASE)
    private val representation = Regex("""<(?:[\w-]+:)?Representation\b([^>]*)>""", RegexOption.IGNORE_CASE)

    private fun attrs(s: String): Map<String, String> =
        attrPattern.findAll(s).associate { match ->
            match.groupValues[1].substringAfter(':').lowercase() to
                (match.groupValues[2].ifEmpty { match.groupValues[3] }.ifEmpty { match.groupValues[4] })
        }

    internal fun isTurkish(lang: String): Boolean {
        val normalized = lang.trim().lowercase().replace('_', '-')
        return normalized == "tr" || normalized.startsWith("tr-") ||
            normalized == "tur" || normalized == "turkish"
    }

    fun parseHls(body: String): DeclaredAudioResult {
        if (!body.trimStart().startsWith("#EXTM3U")) return DeclaredAudioResult(
            DeclaredAudioLanguage.UNKNOWN, "HLS oynatma listesi değil"
        )
        val entries = body.lineSequence().mapNotNull { line ->
            val m = hlsAudio.matchEntire(line) ?: return@mapNotNull null
            attrs(m.groupValues[1]).takeIf { it["type"]?.equals("AUDIO", ignoreCase = true) == true }
        }.toList()
        val languages = entries.mapNotNull { it["language"]?.takeIf(String::isNotBlank) }
        return classify(languages, "HLS")
    }

    fun parseDash(body: String): DeclaredAudioResult {
        if (!body.contains(Regex("""<(?:[\w-]+:)?MPD\b""", RegexOption.IGNORE_CASE))) {
            return DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "DASH MPD değil")
        }
        val languages = mutableListOf<String>()
        adaptation.findAll(body).forEach { match ->
            val a = attrs(match.groupValues[1])
            val isAudio = a["contenttype"]?.equals("audio", ignoreCase = true) == true ||
                a["mimetype"]?.startsWith("audio/", ignoreCase = true) == true
            if (isAudio) {
                a["lang"]?.let(languages::add)
                val suffix = body.substring(match.range.last + 1)
                val end = adaptationEnd.find(suffix)?.range?.first ?: 0
                if (end > 0 && end < 120_000) {
                    representation.findAll(suffix.take(end)).forEach { item ->
                        attrs(item.groupValues[1])["lang"]?.let(languages::add)
                    }
                }
            }
        }
        return classify(languages, "DASH")
    }

    private fun classify(langs: List<String>, format: String): DeclaredAudioResult {
        val identified = langs.map(String::trim).filter(String::isNotBlank).distinct()
        return when {
            identified.any(::isTurkish) ->
                DeclaredAudioResult(DeclaredAudioLanguage.TURKISH, "$format ses manifesti: tr (bildirilen)")
            identified.isNotEmpty() ->
                DeclaredAudioResult(DeclaredAudioLanguage.OTHER, "$format ses manifestinde yalnızca ${identified.joinToString(", ")} bildiriliyor")
            else -> DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "$format: ses dili belirtilmiyor")
        }
    }
}
