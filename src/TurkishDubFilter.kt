package com.nuvio.app.features.streams

/**
 * UI-only stream audio-language filters. This never disables providers or changes
 * the repository's fetched stream collection.
 *
 * IMPORTANT: All classifications are claims inferred from filenames / stream
 * metadata, NOT verified media audio tracks. Keep labels honest.
 */
internal enum class TurkishAudioFilterMode(val label: String) {
    ALL("Tümü"),
    TURKISH("TR Dublaj (etiket)"),
    DUAL("TR Çift Ses"),
    UNKNOWN("Belirsiz"),
}

internal data class TurkishAudioAssessment(
    val turkishClaim: Boolean,
    val turkishDualClaim: Boolean,
    val evidence: String?,
)

internal object TurkishDubFilter {
    // Language markers by themselves are too weak: "TR SUB" is NOT Turkish audio.
    private val explicitDub = Regex(
        "(?:^| )(?:turkce|turkish|tr|tur) (?:dublaj|dub|audio|ses)(?: |$)|" +
            "(?:^| )(?:dublaj|dub|audio|ses) (?:turkce|turkish|tr|tur)(?: |$)"
    )
    private val turkishAudioField = Regex(
        "(?:^| )(?:audio|ses|audio language|ses dili|language|languages|dil) " +
            "(?:tr|tur|turkce|turkish)(?: |$)"
    )
    private val trEnDual = Regex(
        "(?:^| )(?:tr|tur|turkce|turkish) (?:en|eng|english)(?: |$)|" +
            "(?:^| )(?:en|eng|english) (?:tr|tur|turkce|turkish)(?: |$)"
    )
    private val dualIndicator = Regex("(?:^| )(?:dual audio|multi audio|cift ses|coklu ses)(?: |$)")
    private val dubbedIndicator = Regex("(?:^| )(?:dub|dublaj|audio|ses)(?: |$)")

    private fun normalize(text: String): String {
        val cleaned = text.lowercase()
            .replace('ı', 'i').replace('ğ', 'g').replace('ü', 'u')
            .replace('ş', 's').replace('ö', 'o').replace('ç', 'c')
        return cleaned.replace(Regex("[^a-z0-9]+"), " ").trim()
    }

    private fun languageCode(text: String): String = normalize(text)

    private fun isTurkish(code: String): Boolean =
        languageCode(code) in setOf("tr", "tur", "turkish", "turkce")

    private fun isOtherLanguage(code: String): Boolean {
        val normalized = languageCode(code)
        return normalized.isNotBlank() && !isTurkish(normalized)
    }

    fun assess(stream: StreamItem): TurkishAudioAssessment {
        val parsed = stream.clientResolve?.stream?.raw?.parsed
        // Do not include addonName/sourceName or arbitrary URLs: provider branding and
        // URL query strings routinely contain "TR" even for original-language streams.
        val fields = listOfNotNull(
            stream.name,
            stream.title,
            stream.description,
            stream.behaviorHints.filename,
            stream.clientResolve?.filename,
            stream.clientResolve?.torrentName,
            stream.clientResolve?.stream?.raw?.filename,
            parsed?.rawTitle,
            parsed?.parsedTitle,
        ).filter { it.isNotBlank() }
        val searchText = normalize(fields.joinToString(" "))
        val reportedLanguages = parsed?.languages.orEmpty()
        val turkishMetadata = reportedLanguages.any(::isTurkish)
        val nonTurkishMetadata = reportedLanguages.any(::isOtherLanguage)
        val metadataDual = turkishMetadata && nonTurkishMetadata

        val explicitTurkishDub = explicitDub.containsMatchIn(searchText)
        val explicitAudioLanguage = turkishAudioField.containsMatchIn(searchText)
        // Bare TR/EN is ambiguous. Require an audio/dual context to avoid matching subtitles.
        val describedDual = trEnDual.containsMatchIn(searchText) &&
            (dualIndicator.containsMatchIn(searchText) || dubbedIndicator.containsMatchIn(searchText))
        val turkishClaim = explicitTurkishDub || explicitAudioLanguage || turkishMetadata || describedDual
        val dualClaim = turkishClaim && (metadataDual || describedDual ||
            dualIndicator.containsMatchIn(searchText))
        val reason = when {
            turkishMetadata -> "Sağlayıcının raporladığı dil etiketi"
            explicitTurkishDub -> "Türkçe dublaj/ana ses etiketi"
            explicitAudioLanguage -> "Türkçe ses dili etiketi"
            describedDual -> "Türkçe içeren çift ses etiketi"
            else -> null
        }
        return TurkishAudioAssessment(turkishClaim, dualClaim, reason)
    }

    fun matches(stream: StreamItem, mode: TurkishAudioFilterMode): Boolean {
        if (mode == TurkishAudioFilterMode.ALL) return true
        val assessment = assess(stream)
        return when (mode) {
            TurkishAudioFilterMode.ALL -> true
            TurkishAudioFilterMode.TURKISH -> assessment.turkishClaim
            TurkishAudioFilterMode.DUAL -> assessment.turkishDualClaim
            TurkishAudioFilterMode.UNKNOWN -> !assessment.turkishClaim
        }
    }

    fun filterGroups(groups: List<AddonStreamGroup>, mode: TurkishAudioFilterMode): List<AddonStreamGroup> {
        if (mode == TurkishAudioFilterMode.ALL) return groups
        return groups.mapNotNull { group ->
            val matching = group.streams.filter { matches(it, mode) }
            if (matching.isEmpty() && !group.isLoading) null else group.copy(streams = matching)
        }
    }
}
