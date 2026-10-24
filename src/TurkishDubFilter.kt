package com.nuvio.app.features.streams

/**
 * Display-only filtering. Providers, repository fetches and playback are never
 * disabled or altered by this class.
 *
 * All labels here are REPORTED or INFERRED labels, not inspected player audio
 * tracks. In particular, Turkish UI text "Dublaj / Altyazılı" means that a
 * dub may be offered, NOT that the selected stream was verified as Turkish.
 */
internal enum class TurkishAudioFilterMode(val label: String) {
    ALL("Tümü"),
    TURKISH("TR Dublaj + Olası"),
    EXPLICIT("Açık TR Etiketi"),
    DUAL("TR Çift Ses"),
    UNKNOWN("Belirsiz"),
}

internal data class TurkishAudioAssessment(
    val turkishClaim: Boolean,
    val turkishDualClaim: Boolean,
    val evidence: String?,
    val probableTurkishClaim: Boolean = false,
    val explicitTurkishClaim: Boolean = false,
)

internal object TurkishDubFilter {
    private val explicitDub = Regex(
        "(?:^| )(?:turkce|turkish|tr|tur) (?:dublaj|dublajli|dub|audio|ses|seslendirme|seslendirmeli|voiceover)(?: |$)|" +
            "(?:^| )(?:dublaj|dublajli|dub|audio|ses|seslendirme|seslendirmeli|voiceover) (?:turkce|turkish|tr|tur)(?: |$)"
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
    private val dubbedIndicator = Regex("(?:^| )(?:dub|dublaj|dublajli|audio|ses)(?: |$)")

    // Turkish-localised source label, often accompanied by /Altyazılı.
    // This is intentionally classified as PROBABLE (not verified).
    private val genericTurkishDubLabel = Regex("(?:^| )(?:dublaj|dublajli)(?: |$)")
    private val negativeDubCue = Regex(
        "(?:^| )(?:dublaj yok|dublaj mevcut degil|dublaj bulunmuyor|" +
            "sadece altyazili|yalnizca altyazili|no dub|not dubbed|" +
            "subtitle only|sub only)(?: |$)"
    )
    private val foreignLanguageDub = Regex(
        "(?:^| )(?:ingilizce|english|en|eng|japonca|japanese|ja|jp|" +
            "fransizca|french|fr|almanca|german|de|ispanyolca|spanish|es|" +
            "hintce|hindi|hi|korece|korean|ko|rusca|russian|ru|arapca|arabic|ar) " +
            "(?:dublaj|dublajli|dub)(?: |$)|" +
            "(?:^| )(?:dub|dublaj|dublajli) (?:english|japanese|french|german|" +
            "spanish|hindi|korean|russian|arabic)(?: |$)"
    )

    private fun normalize(text: String): String {
        val cleaned = text.replace('İ', 'i').lowercase().replace("\u0307", "")
            .replace('ı', 'i').replace('ğ', 'g').replace('ü', 'u')
            .replace('ş', 's').replace('ö', 'o').replace('ç', 'c')
        return cleaned.replace(Regex("[^a-z0-9]+"), " ").trim()
    }

    private fun isTurkish(code: String): Boolean =
        normalize(code) in setOf("tr", "tur", "turkish", "turkce")

    private fun isOtherLanguage(code: String): Boolean =
        normalize(code).isNotBlank() && !isTurkish(code)

    fun assess(stream: StreamItem): TurkishAudioAssessment {
        val parsed = stream.clientResolve?.stream?.raw?.parsed
        // Never infer audio language from provider branding or URL parameters.
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
        val text = normalize(fields.joinToString(" "))
        val reportedLanguages = parsed?.languages.orEmpty()
        val turkishMetadata = reportedLanguages.any(::isTurkish)
        val metadataDual = turkishMetadata && reportedLanguages.any(::isOtherLanguage)

        val explicitTurkishDub = explicitDub.containsMatchIn(text)
        val explicitAudioLanguage = turkishAudioField.containsMatchIn(text)
        val describedDual = trEnDual.containsMatchIn(text) &&
            (dualIndicator.containsMatchIn(text) || dubbedIndicator.containsMatchIn(text))
        val explicitTurkish = explicitTurkishDub || explicitAudioLanguage || turkishMetadata || describedDual

        // Only an inference: the Turkish UI label "Dublaj / Altyazılı" is
        // insufficient to know which audio track is actually available.
        val probableTurkish = !explicitTurkish &&
            genericTurkishDubLabel.containsMatchIn(text) &&
            !negativeDubCue.containsMatchIn(text) &&
            !foreignLanguageDub.containsMatchIn(text)
        val turkishClaim = explicitTurkish || probableTurkish
        // A bare "Dublaj / Altyazılı" never proves that two audio tracks exist.
        val dualClaim = explicitTurkish && (metadataDual || describedDual ||
            dualIndicator.containsMatchIn(text))
        val evidence = when {
            turkishMetadata -> "Sağlayıcının Türkçe dil etiketi (ses izi doğrulanmadı)"
            explicitTurkishDub -> "Açık Türkçe dublaj etiketi (ses izi doğrulanmadı)"
            explicitAudioLanguage -> "Açık Türkçe ses dili etiketi (ses izi doğrulanmadı)"
            describedDual -> "Türkçe içeren çift ses etiketi (ses izi doğrulanmadı)"
            probableTurkish -> "Olası: 'Dublaj' açıklaması; Türkçe ses doğrulanmadı"
            else -> null
        }
        return TurkishAudioAssessment(
            turkishClaim = turkishClaim,
            turkishDualClaim = dualClaim,
            evidence = evidence,
            probableTurkishClaim = probableTurkish,
            explicitTurkishClaim = explicitTurkish,
        )
    }

    fun matches(stream: StreamItem, mode: TurkishAudioFilterMode): Boolean {
        if (mode == TurkishAudioFilterMode.ALL) return true
        val assessment = assess(stream)
        return when (mode) {
            TurkishAudioFilterMode.ALL -> true
            TurkishAudioFilterMode.TURKISH -> assessment.turkishClaim
            TurkishAudioFilterMode.EXPLICIT -> assessment.explicitTurkishClaim
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