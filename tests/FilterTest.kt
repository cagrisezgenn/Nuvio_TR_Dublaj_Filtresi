package com.nuvio.app.features.streams

fun main() {
    fun a(label: String) = TurkishDubFilter.assess(StreamItem(name = label))
    check(a("Film 1080p Türkçe Dublaj").turkishClaim)
    check(a("Film 4K TR-DUB").turkishClaim)
    check(a("Film TURKISH AUDIO 1080p").turkishClaim)
    check(a("Film Dublaj Türkçe").turkishClaim)
    check(a("Film Türkçe Seslendirme").explicitTurkishClaim)
    check(a("Film Turkish Voiceover").explicitTurkishClaim)
    check(a("Film Dublaj / Altyazılı").probableTurkishClaim)
    check(!a("Film Dublaj / Altyazılı").explicitTurkishClaim)
    check(!a("Film Sadece Altyazılı Dublaj").turkishClaim)
    check(!a("Film İngilizce Dublaj").turkishClaim)
    check(a("Film 1080p Dual Audio TR-EN").turkishDualClaim)
    check(a("Film 720p TR/EN Çift Ses").turkishDualClaim)
    check(!a("Film Dual Audio").turkishClaim)
    check(!a("Film Türkçe Altyazı").turkishClaim)
    check(!a("Film TR SUB 1080p").turkishClaim)
    check(!a("Turkish subtitles, English dub").turkishClaim)
    check(!a("Film 1080p WEB-DL").turkishClaim)
    check(!TurkishDubFilter.assess(StreamItem(addonName = "Türkçe Dublaj Kaynak", name = "Movie English audio")).turkishClaim)
    check(TurkishDubFilter.assess(StreamItem(clientResolve = StreamClientResolve(
        stream = StreamClientResolveStream(raw = StreamClientResolveRaw(
            parsed = StreamClientResolveParsed(languages = listOf("en", "tr"))
        ))
    ))).turkishDualClaim)
    val groups = listOf(AddonStreamGroup(listOf(StreamItem(name="TR DUB"), StreamItem(name="TR SUB"))))
    check(TurkishDubFilter.filterGroups(groups, TurkishAudioFilterMode.ALL).first().streams.size == 2)
    check(TurkishDubFilter.filterGroups(groups, TurkishAudioFilterMode.TURKISH).first().streams.size == 1)
    check(TurkishDubFilter.filterGroups(groups, TurkishAudioFilterMode.UNKNOWN).first().streams.size == 1)
    println("PASS: 22 Turkish audio labeling and list filtering assertions")
}
