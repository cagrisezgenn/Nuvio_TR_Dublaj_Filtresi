package com.nuvio.app.features.streams

fun main() {
    val hlsTurkish = """
        #EXTM3U
        #EXT-X-VERSION:6
        #EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID="audio",NAME="Turkish",LANGUAGE="tr-TR",DEFAULT=YES,URI="audio/tr.m3u8"
        #EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID="audio",NAME="Original",LANGUAGE="en",DEFAULT=NO,URI="audio/en.m3u8"
    """.trimIndent()
    check(TurkishAudioManifestParser.parseHls(hlsTurkish).status == DeclaredAudioLanguage.TURKISH)
    check(TurkishAudioManifestParser.parseHls(
        "#EXTM3U\n#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"aud\",LANGUAGE=eng,URI=\"en.m3u8\""
    ).status == DeclaredAudioLanguage.OTHER)
    check(TurkishAudioManifestParser.parseHls(
        "#EXTM3U\n#EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID=\"sub\",LANGUAGE=\"tr\",URI=\"sub/tr.vtt\""
    ).status == DeclaredAudioLanguage.UNKNOWN)
    check(TurkishAudioManifestParser.parseHls("#EXTM3U\n#EXTINF:6,\npart001.ts")
        .status == DeclaredAudioLanguage.UNKNOWN)
    check(TurkishAudioManifestParser.parseHls("Not an HLS manifest")
        .status == DeclaredAudioLanguage.UNKNOWN)
    check(TurkishAudioManifestParser.parseHls(
        "#EXTM3U\n#EXT-X-MEDIA:TYPE=AUDIO,NAME=\"Turkish\",LANGUAGE=\"en\",URI=\"en.m3u8\""
    ).status == DeclaredAudioLanguage.OTHER)
    val dashTurkish = """
        <MPD xmlns="urn:mpeg:dash:schema:mpd:2011">
          <Period>
            <AdaptationSet mimeType="video/mp4" lang="tr"><Representation id="video"/></AdaptationSet>
            <AdaptationSet mimeType="audio/mp4" lang="tr"><Representation id="audio"/></AdaptationSet>
          </Period>
        </MPD>
    """.trimIndent()
    check(TurkishAudioManifestParser.parseDash(dashTurkish).status == DeclaredAudioLanguage.TURKISH)
    check(TurkishAudioManifestParser.parseDash(
        """<MPD><AdaptationSet contentType="audio" lang="en"></AdaptationSet></MPD>"""
    ).status == DeclaredAudioLanguage.OTHER)
    check(TurkishAudioManifestParser.parseDash(
        """<MPD><AdaptationSet contentType="text" lang="tr"></AdaptationSet></MPD>"""
    ).status == DeclaredAudioLanguage.UNKNOWN)
    check(TurkishAudioManifestParser.parseDash(
        """<MPD><AdaptationSet mimeType="video/mp4" lang="tr"></AdaptationSet></MPD>"""
    ).status == DeclaredAudioLanguage.UNKNOWN)
    check(TurkishAudioManifestParser.parseDash(
        """<MPD><AdaptationSet mimeType="audio/mp4"><Representation lang="tr"/></AdaptationSet></MPD>"""
    ).status == DeclaredAudioLanguage.TURKISH)
    check(TurkishAudioManifestParser.parseDash("invalid manifest").status == DeclaredAudioLanguage.UNKNOWN)
    println("PASS: 12 HLS/DASH audio language assertions")
}
