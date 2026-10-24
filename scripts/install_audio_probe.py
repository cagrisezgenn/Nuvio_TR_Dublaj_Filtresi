#!/usr/bin/env python3
"""Safely add content/stream-scoped HLS/DASH audio metadata inspection to patched v0.4."""
from pathlib import Path
import argparse
import shutil
import sys

STREAMS = Path("composeApp/src/commonMain/kotlin/com/nuvio/app/features/streams")
MARKER = "TR_AUDIO_MANIFEST_PROBE_V05"
FILES = ("TurkishAudioManifestParser.kt", "TurkishAudioManifestProbe.kt")

def one(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise ValueError(f"Expected one {label} anchor, found {count}; refusing unsafe patch")
    return text.replace(old, new, 1)

def patch(source: str) -> str:
    if MARKER in source:
        return source
    if "TURKISH_DUB_FILTER_V2_STICKY" not in source:
        raise ValueError("Run install_filter.py before install_audio_probe.py")
    s = one(
        source,
        "import androidx.compose.runtime.mutableStateOf\n",
        "import androidx.compose.runtime.mutableStateOf\n"
        "import androidx.compose.runtime.mutableStateMapOf\n"
        "import kotlinx.coroutines.async\n"
        "import kotlinx.coroutines.awaitAll\n"
        "import kotlinx.coroutines.coroutineScope\n",
        "state/async import",
    )
    old = "    val filteredGroups = TurkishDubFilter.filterGroups(uiState.filteredGroups, turkishAudioFilter)\n"
    insert = """    // TR_AUDIO_MANIFEST_PROBE_V05: per-content, per-stream metadata (not provider claims).
    val audioManifestReports = remember(uiState.requestToken) { mutableStateMapOf<String, DeclaredAudioResult>() }
    var audioScanning by remember(uiState.requestToken) { mutableStateOf(false) }
    val audioScanScope = rememberCoroutineScope()
    val scanEligible = uiState.filteredGroups.flatMap { it.streams }
        .filter(TurkishAudioManifestProbe::canInspect)
        .distinctBy { it.playableDirectUrl }
    val declaredTurkishUrls = audioManifestReports.filterValues {
        it.status == DeclaredAudioLanguage.TURKISH
    }.keys.toSet()
    val filteredGroups = TurkishDubFilter.filterGroups(
        uiState.filteredGroups, turkishAudioFilter, declaredTurkishUrls
    )
    val onInspectManifests: () -> Unit = {
        if (!audioScanning) {
            audioScanning = true
            // Limit network reads per tap. No autoplay or downloading video segments.
            val pending = scanEligible.filter {
                val url = it.playableDirectUrl
                url != null && url !in audioManifestReports
            }.take(24)
            audioScanScope.launch {
                try {
                    for (chunk in pending.chunked(4)) {
                        val results = coroutineScope {
                            chunk.map { stream ->
                                async {
                                    val url = stream.playableDirectUrl.orEmpty()
                                    url to TurkishAudioManifestProbe.inspect(stream)
                                }
                            }.awaitAll()
                        }
                        results.forEach { (url, report) ->
                            audioManifestReports[url] = report
                        }
                    }
                } finally {
                    audioScanning = false
                }
            }
        }
    }
"""
    s = one(s, old, insert, "StreamList groups")
    s = one(
        s,
        "            totalCount = uiState.filteredGroups.sumOf { it.streams.size },\n"
        "        )\n",
        "            totalCount = uiState.filteredGroups.sumOf { it.streams.size },\n"
        "            eligibleCount = scanEligible.size,\n"
        "            inspectedCount = audioManifestReports.size,\n"
        "            isInspecting = audioScanning,\n"
        "            onInspect = onInspectManifests,\n"
        "        )\n",
        "sticky toolbar parameters",
    )
    s = one(
        s,
        "sharedStream?.takeIf { TurkishDubFilter.matches(it.stream, turkishAudioFilter) }?.let { shared ->",
        "sharedStream?.takeIf { TurkishDubFilter.matches(it.stream, turkishAudioFilter, declaredTurkishUrls) }?.let { shared ->",
        "shared stream filtering",
    )
    return s

def run(project: Path, check: bool) -> None:
    target = project / STREAMS / "StreamsScreen.kt"
    if not target.is_file():
        raise FileNotFoundError(target)
    before = target.read_text(encoding="utf-8")
    after = patch(before)
    assets = Path(__file__).resolve().parent.parent / "src"
    for filename in FILES:
        if not (assets / filename).is_file():
            raise FileNotFoundError(assets / filename)
        if after != before and (project / STREAMS / filename).exists():
            raise ValueError(f"Refuse overwrite: {filename}")
    if check:
        print("Audio manifest patch is compatible; no files changed.")
        return
    if after == before:
        print("Audio manifest patch already installed.")
        return
    backup = target.with_suffix(".kt.traudio-original.bak")
    if backup.exists():
        raise ValueError("Backup exists; stop")
    shutil.copy2(target, backup)
    try:
        for filename in FILES:
            shutil.copy2(assets / filename, project / STREAMS / filename)
        target.write_text(after, encoding="utf-8")
    except Exception:
        shutil.copy2(backup, target)
        for filename in FILES:
            (project / STREAMS / filename).unlink(missing_ok=True)
        raise
    print("HLS/DASH audio inspection integrated without changing provider settings.")

if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("repository")
    p.add_argument("--check", action="store_true")
    a = p.parse_args()
    try:
        run(Path(a.repository).resolve(), a.check)
    except (ValueError, FileNotFoundError) as exc:
        print("ERROR:", exc, file=sys.stderr)
        sys.exit(3)
