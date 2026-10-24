package com.nuvio.app.features.streams

import com.nuvio.app.features.addons.httpGetTextWithHeaders
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * Probes playlist text, not media segments. No movie download or autoplay.
 * Direct HTTPS HLS/DASH URLs only: unsupported/DRM/torrents -> UNKNOWN.
 */
internal object TurkishAudioManifestProbe {
    private const val TIMEOUT_MS = 8_000L

    fun canInspect(stream: StreamItem): Boolean {
        val url = stream.playableDirectUrl?.trim() ?: return false
        if (!url.startsWith("https://", ignoreCase = true)) return false
        val path = url.substringBefore('?').substringBefore('#').lowercase()
        val kind = stream.streamType?.lowercase().orEmpty()
        return path.endsWith(".m3u8") || path.endsWith(".mpd") ||
            kind == "hls" || kind == "dash"
    }

    suspend fun inspect(stream: StreamItem): DeclaredAudioResult {
        if (!canInspect(stream)) {
            return DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "Akışın ses manifestine erişim desteklenmiyor")
        }
        val url = stream.playableDirectUrl ?: return DeclaredAudioResult(
            DeclaredAudioLanguage.UNKNOWN, "Doğrudan medya URL'si bulunamadı"
        )
        return try {
            val raw = withTimeout(TIMEOUT_MS) {
                httpGetTextWithHeaders(url, stream.behaviorHints.proxyHeaders?.request.orEmpty())
            }
            if (raw.length > 1_000_000) {
                DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "Manifest boyutu sınırı aşıldı")
            } else {
                val stripped = raw.trimStart()
                when {
                    stripped.startsWith("#EXTM3U") -> TurkishAudioManifestParser.parseHls(raw)
                    stripped.startsWith("<") && stripped.take(1500).contains(
                        Regex("""<(?:[\w-]+:)?MPD\b""", RegexOption.IGNORE_CASE)
                    ) -> TurkishAudioManifestParser.parseDash(raw)
                    else -> DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "Ses manifesti okunamadı veya korumalı akış")
                }
            }
        } catch (timeout: TimeoutCancellationException) {
            DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "Manifest isteği zaman aşımına uğradı")
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (exception: Exception) {
            // No tokenised URL / HTTP header values in UI or logs.
            DeclaredAudioResult(DeclaredAudioLanguage.UNKNOWN, "Ses bilgisine erişilemedi")
        }
    }
}
