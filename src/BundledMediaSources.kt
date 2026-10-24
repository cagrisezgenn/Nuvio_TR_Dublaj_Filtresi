package com.nuvio.app.features.settings

/** Frozen snapshot of the Nuvio entries in CloudStreamRepoManager/central_repos.json.
 * Source snapshot: 2026-10-22T14:07:43Z. Entries are NOT proof that a URL works.
 * This file contains ONLY Nuvio entries; no CloudStream or Play plugin runtimes.
 */
internal enum class SourceKind { ADDON, PLUGIN }
internal data class NuvioSourcePreset(
    val sourceId: String,
    val name: String,
    val url: String,
    val kind: SourceKind,
    val category: String,
    val sourceKindCorrected: Boolean = false,
)

internal object BundledMediaSources {
    val nuvioSources: List<NuvioSourcePreset> = listOf(
        NuvioSourcePreset("1", "wio sinema plugin", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/manifest.json", SourceKind.PLUGIN, "SİNEMA"),
        NuvioSourcePreset("2", "PLAY KATALOG", "https://play-nuvio-zel-katalog-st-dyosu.ai.studio/", SourceKind.ADDON, "KATALOG"),
        NuvioSourcePreset("3", "JUSTEMİR84 plugin", "https://raw.githubusercontent.com/justemir84/nuvio-dexter-plugin/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("4", "izlelan katalog", "https://katalog.ayruki.workers.dev", SourceKind.ADDON, "katalog"),
        NuvioSourcePreset("5", "jasoelys katalog", "https://katalog.jasoelys.workers.dev/", SourceKind.ADDON, "KATALOG"),
        NuvioSourcePreset("6", "jasoleys", "https://nuvio.jasoelys.workers.dev/manifest.json", SourceKind.PLUGIN, "plugin"),
        NuvioSourcePreset("7", "HANS.MEGA", "https://raw.githubusercontent.com/pnthancyb/hans-mega/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("9", "GSREPO", "https://raw.githubusercontent.com/csprofesor/csnuvio/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("10", "ANTHOLOGY PLUGİN", "https://raw.githubusercontent.com/falsisdev/anthology/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("11", "izlelan", "https://izlelan.com/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("12", "DİZİBOX", "https://raw.githubusercontent.com/kratoskrator/dizibox-nuvio/main/manifest.json", SourceKind.PLUGIN, "DİZİ"),
        NuvioSourcePreset("13", "hdfilmcehennemi", "https://raw.githubusercontent.com/kratoskrator/hdfilmcehennemi-nuvio/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("14", "BRONZE CLOUD", "https://raw.githubusercontent.com/dr-octagon/Nuvio/main/manifest.json", SourceKind.ADDON, "Genel"),
        NuvioSourcePreset("15", "PATRON", "https://raw.githubusercontent.com/patr0nq/nuvioaddons/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("16", "SREAİKER", "https://streailer.elfhosted.com/%7B%22language%22%3A%22tr-TR%22%2C%22externalLink%22%3Afalse%2C%22showRecap%22%3Afalse%2C%22onlyRecaps%22%3Afalse%7D/manifest.json", SourceKind.ADDON, "TÜRKÇE"),
        NuvioSourcePreset("17", "mooncrown", "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("18", "wekmed", "https://raw.githubusercontent.com/Wekmed/nuvio/refs/heads/main/manifest.json", SourceKind.PLUGIN, "Genel"),
        NuvioSourcePreset("19", "nexus", "https://nexuszen.vercel.app/manifest.json", SourceKind.ADDON, "FİLM ALTYAZILI"),
        NuvioSourcePreset("20", "SKCZ TORRENT", "https://61ab9c85a149-skcz-torrents.baby-beamup.club/manifest.json", SourceKind.ADDON, "FİLM"),
        NuvioSourcePreset("21", "MOTORSPORT HUB", "https://stream.prototipo.nl/%7B%7D/manifest.json", SourceKind.ADDON, "İNGİLİZCE"),
        NuvioSourcePreset("22", "sİNEWİX", "https://sine-wix.vercel.app/manifest.json", SourceKind.ADDON, "TÜRKÇE"),
        NuvioSourcePreset("23", "TORRENTİNO", "https://torrentio.strem.fun/manifest.json", SourceKind.ADDON, "TORRENT FİLM"),
        NuvioSourcePreset("24", "İZLEMELİ", "https://izlemeli.github.io/nuvio/tr/manifest.json", SourceKind.ADDON, "TÜRKÇE"),
        NuvioSourcePreset("25", "wiospor", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/live/wiospor/manifest.json", SourceKind.ADDON, "SPOR"),
        NuvioSourcePreset("26", "MANİTUX", "https://raw.githubusercontent.com/manitux-app/nuvio-plugins/refs/heads/main/manifest.json", SourceKind.PLUGIN, "TÜRKÇE", sourceKindCorrected = true),
        NuvioSourcePreset("27", "EUROSPORT", "https://tvvoo.hayd.uk/cfg-tr-hc1/manifest.json", SourceKind.ADDON, "SPORT"),
        NuvioSourcePreset("28", "NUVİO KATALOG", "https://catalog.nuvio.tv/%7B%22language%22%3A%22tr-TR%22%2C%22region%22%3A%22TR%22%7D/manifest.json", SourceKind.ADDON, "KATALOG"),
        NuvioSourcePreset("29", "ANTHOLOGY NUVİO", "https://falsisdev.github.io/anthology/stremio/manifest.json", SourceKind.ADDON, "CANLI TV"),
        NuvioSourcePreset("31", "ALTYAZI", "https://altyazi-stremio.online/manifest.json", SourceKind.ADDON, "ALTYAZI")
    )
    val nuvioAddons get() = nuvioSources.filter { it.kind == SourceKind.ADDON }
    val nuvioPlugins get() = nuvioSources.filter { it.kind == SourceKind.PLUGIN }

    /** Metadata hint only; contentLanguage=tr NEVER confirms dubbed audio. */
    val reportedDubCapableProviders = listOf(
        "DiziYou", "SetFilm", "Dizipal", "Dizilla", "BelgeselX",
        "DiziFun", "FilmMakinesi", "HDFilmCehennemi", "FilmModu",
        "SineWix", "WebteIzle",
    )
}
