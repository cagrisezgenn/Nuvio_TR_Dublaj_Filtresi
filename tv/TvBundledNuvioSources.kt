package com.nuvio.tv.core.bundled

/** 29 Nuvio presets from CSRepoManager's 2026-10-22 public index, NOT CloudStream .cs3 files. */
enum class TvSourceKind { ADDON, PLUGIN }
data class TvNuvioPreset(val name: String, val url: String, val kind: TvSourceKind)

object TvBundledNuvioSources {
    val presets: List<TvNuvioPreset> = listOf(
        TvNuvioPreset("wio sinema plugin", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("PLAY KATALOG", "https://play-nuvio-zel-katalog-st-dyosu.ai.studio/", TvSourceKind.ADDON),
        TvNuvioPreset("JUSTEMİR84 plugin", "https://raw.githubusercontent.com/justemir84/nuvio-dexter-plugin/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("izlelan katalog", "https://katalog.ayruki.workers.dev", TvSourceKind.ADDON),
        TvNuvioPreset("jasoelys katalog", "https://katalog.jasoelys.workers.dev/", TvSourceKind.ADDON),
        TvNuvioPreset("jasoleys", "https://nuvio.jasoelys.workers.dev/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("HANS.MEGA", "https://raw.githubusercontent.com/pnthancyb/hans-mega/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("GSREPO", "https://raw.githubusercontent.com/csprofesor/csnuvio/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("ANTHOLOGY PLUGİN", "https://raw.githubusercontent.com/falsisdev/anthology/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("izlelan", "https://izlelan.com/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("DİZİBOX", "https://raw.githubusercontent.com/kratoskrator/dizibox-nuvio/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("hdfilmcehennemi", "https://raw.githubusercontent.com/kratoskrator/hdfilmcehennemi-nuvio/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("BRONZE CLOUD", "https://raw.githubusercontent.com/dr-octagon/Nuvio/main/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("PATRON", "https://raw.githubusercontent.com/patr0nq/nuvioaddons/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("SREAİKER", "https://streailer.elfhosted.com/%7B%22language%22%3A%22tr-TR%22%2C%22externalLink%22%3Afalse%2C%22showRecap%22%3Afalse%2C%22onlyRecaps%22%3Afalse%7D/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("mooncrown", "https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("wekmed", "https://raw.githubusercontent.com/Wekmed/nuvio/refs/heads/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("nexus", "https://nexuszen.vercel.app/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("SKCZ TORRENT", "https://61ab9c85a149-skcz-torrents.baby-beamup.club/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("MOTORSPORT HUB", "https://stream.prototipo.nl/%7B%7D/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("sİNEWİX", "https://sine-wix.vercel.app/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("TORRENTİNO", "https://torrentio.strem.fun/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("İZLEMELİ", "https://izlemeli.github.io/nuvio/tr/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("wiospor", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/live/wiospor/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("MANİTUX", "https://raw.githubusercontent.com/manitux-app/nuvio-plugins/refs/heads/main/manifest.json", TvSourceKind.PLUGIN),
        TvNuvioPreset("EUROSPORT", "https://tvvoo.hayd.uk/cfg-tr-hc1/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("NUVİO KATALOG", "https://catalog.nuvio.tv/%7B%22language%22%3A%22tr-TR%22%2C%22region%22%3A%22TR%22%7D/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("ANTHOLOGY NUVİO", "https://falsisdev.github.io/anthology/stremio/manifest.json", TvSourceKind.ADDON),
        TvNuvioPreset("ALTYAZI", "https://altyazi-stremio.online/manifest.json", TvSourceKind.ADDON)
    )
    val addons: List<TvNuvioPreset> get() = presets.filter { it.kind == TvSourceKind.ADDON }
    val plugins: List<TvNuvioPreset> get() = presets.filter { it.kind == TvSourceKind.PLUGIN }
}
