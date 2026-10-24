package com.nuvio.app.features.settings

/** Bundled *references*, not executable code and not an assertion that a stream is licensed or reachable. */
internal data class BundledSource(val name: String, val url: String)

internal object BundledMediaSources {
    // These URLs must be installed by Nuvio's normal manifest validation routines.
    val nuvioAddons = listOf(
        BundledSource("Nuvio Catalog TR", "https://catalog.nuvio.tv/%7B%22language%22%3A%22tr-TR%22%2C%22region%22%3A%22TR%22%7D/manifest.json"),
        BundledSource("İzlemeli TR", "https://izlemeli.github.io/nuvio/tr/manifest.json"),
        BundledSource("Cinemeta", "https://v3-cinemeta.strem.io/manifest.json"),
        BundledSource("OpenSubtitles v3", "https://opensubtitles-v3.strem.io/manifest.json"),
        BundledSource("SubSense", "https://subsense.nepiraw.com/manifest.json"),
        BundledSource("AltyaziDB", "https://altyazidb.online/manifest.json"),
        BundledSource("AltyaziTR", "https://altyazi-stremio.online/manifest.json"),
        BundledSource("Anthology TV", "https://falsisdev.github.io/anthology/stremio/manifest.json"),
        BundledSource("WioSpor", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/live/wiospor/manifest.json"),
        BundledSource("TvVoo Turkey", "https://tvvoo.hayd.uk/cfg-tr-hc1/manifest.json"),
        BundledSource("CNCVerse Bridge", "https://cncverse.dpdns.org/manifest.json"),
        BundledSource("Torrentio", "https://torrentio.strem.fun/manifest.json")
    )

    val nuvioPlugins = listOf(
        BundledSource("Anthology", "https://raw.githubusercontent.com/falsisdev/anthology/main/manifest.json"),
        BundledSource("Manitux Nuvio", "https://raw.githubusercontent.com/manitux-app/nuvio-plugins/refs/heads/main/manifest.json"),
        BundledSource("WioSinema", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/manifest.json"),
        BundledSource("WioCinema", "https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/wiocinema/manifest.json"),
        BundledSource("Patron", "https://raw.githubusercontent.com/patr0nq/nuvioaddons/refs/heads/main/manifest.json"),
        BundledSource("V1R", "https://raw.githubusercontent.com/v1rtech/v1rtvbox/master/Nuvio/addons/manifest.json"),
        BundledSource("CSNuvio", "https://raw.githubusercontent.com/csprofesor/csnuvio/main/manifest.json"),
        BundledSource("CVN Onrcvn", "https://raw.githubusercontent.com/onrcvndev/nuvio-providers/refs/heads/main/manifest.json"),
        BundledSource("İzlelan", "https://izlelan.com/manifest.json"),
        BundledSource("Yoru", "https://raw.githubusercontent.com/yoruix/nuvio-providers/refs/heads/main/manifest.json"),
        BundledSource("NiakVIO", "https://raw.githubusercontent.com/niakw/NiakVIO/refs/heads/main/manifest.json")
    )

    /**
     * Exact entries found in CSRepoManager_User.apk/assets/central_repos.json.
     * CloudStream repo.json -> plugins.json -> compiled .cs3/Dex.
     * These cannot be passed to Nuvio's addRepository(manifest.json).
     * They are included here as a human-readable catalogue only.
     */
    val cloudStreamRepositories = listOf(
        BundledSource("cs-karma", "https://raw.githubusercontent.com/Kraptor123/cs-Karma/refs/heads/master/repo.json"),
        BundledSource("NeO Eklenti Deposu", "https://raw.githubusercontent.com/neoser1984/cloudstream-extensions/main/repo.json"),
        BundledSource("SafakStream Repository", "https://raw.githubusercontent.com/SafakStream/SafakStream/builds/repo.json"),
        BundledSource("Manitux Cloudstream Plugins", "https://raw.githubusercontent.com/manitux-app/cs-plugins/refs/heads/main/repo.json"),
        BundledSource("TurkSinema", "https://raw.githubusercontent.com/Wiojelt/TurkSinema/main/repo.json"),
        BundledSource("cstest", "https://raw.githubusercontent.com/ctnkyaumt/cstest/master/repo.json"),
        BundledSource("AllForU", "https://raw.githubusercontent.com/RVRBEAST76/allforu-repo/builds/repo.json"),
        BundledSource("BronzeCloud", "https://raw.githubusercontent.com/Dr-Octagon/cloudstream-turkish/refs/heads/builds/repo_stable.json"),
        BundledSource("Kekik / feroxxcs3", "https://raw.githubusercontent.com/liberta09/Kekik-cloudstream/builds/repo.json"),
        BundledSource("Phisher", "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/refs/heads/builds/repo.json"),
        BundledSource("Mega repository", "https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json"),
        BundledSource("mudti", "https://raw.githubusercontent.com/pltmustafa/plt-stream/refs/heads/master/repo.json"),
        BundledSource("megaturk", "https://raw.githubusercontent.com/Kraptor123/TurkMegaRepo/refs/heads/master/repo.json"),
        BundledSource("Latte Sinetech", "https://raw.githubusercontent.com/GitLatte/Sinetech/refs/heads/main/repo.json"),
        BundledSource("Makoto", "https://raw.githubusercontent.com/Sertel392/Makotogecici/refs/heads/main/repo.json")
    )

    // Provider-level hints from available CloudStream plugins.json metadata, NOT per-video Turkish audio evidence.
    val reportedDubCapableProviders = listOf(
        "DiziYou", "SetFilm", "Dizipal", "Dizilla", "BelgeselX",
        "DiziFun", "FullHDFilmİzlede", "FilmMakinesi", "HDFilmCehennemi"
    )
}
