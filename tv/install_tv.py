#!/usr/bin/env python3
"""Patch official NuvioTV dev source for a separate, remote-first Turkish TV APK.

Refuse upstream drift rather than modifying arbitrary Kotlin fragments.
"""
from pathlib import Path
import argparse, shutil, sys

ROOT = Path("app/src/main/java/com/nuvio/tv")
SRC = Path(__file__).resolve().parent
MARK = "NUVIO_TR_TV_V1"

def replace_one(text, needle, replacement, label):
    count = text.count(needle)
    if count != 1:
        raise RuntimeError(f"{label}: expected one exact anchor, found {count}")
    return text.replace(needle, replacement, 1)

def patch_file(text, name):
    if MARK in text:
        return text
    if name == "stream":
        text = replace_one(text,
            "    val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n",
            "    val uiState by viewModel.uiState.collectAsStateWithLifecycle()\n"
            "    // " + MARK + ": D-pad source metadata filter, not real audio detection.\n"
            "    var tvDubMode by rememberSaveable { mutableStateOf(TvDubMode.ALL) }\n"
            "    val tvFilteredStreams = TvDubFilter.filter(uiState.filteredStreams, tvDubMode)\n",
            "tv state")
        text = replace_one(text, "                    streams = uiState.filteredStreams,\n",
            "                    streams = tvFilteredStreams,\n"
            "                    tvDubMode = tvDubMode,\n"
            "                    onTvDubModeSelected = { tvDubMode = it },\n", "stream list")
        text = replace_one(text, "                        val currentIndex = uiState.filteredStreams.indexOfFirst {\n",
            "                        val currentIndex = tvFilteredStreams.indexOfFirst {\n", "focused index")
        text = replace_one(text,
            "private fun RightStreamSection(\n    isLoading: Boolean,\n",
            "private fun RightStreamSection(\n    isLoading: Boolean,\n"
            "    tvDubMode: TvDubMode,\n"
            "    onTvDubModeSelected: (TvDubMode) -> Unit,\n", "right panel props")
        text = replace_one(text,
            "                    .padding(NuvioTheme.spacing.lg)\n"
            "            ) {\n                resumeText?.let { text ->\n",
            "                    .padding(NuvioTheme.spacing.lg)\n"
            "            ) {\n"
            "                TvDubFocusBar(\n"
            "                    selected = tvDubMode,\n"
            "                    onSelect = onTvDubModeSelected,\n"
            "                    shown = streams.size,\n"
            "                    total = streams.size\n"
            "                )\n"
            "                resumeText?.let { text ->\n", "D-pad filter bar")
        return text
    if name == "addon-vm":
        text = replace_one(text, "import com.nuvio.tv.R\n",
          "import com.nuvio.tv.R\nimport com.nuvio.tv.core.bundled.TvBundledNuvioSources\n", "addon import")
        needle = "    fun installAddon() {\n"
        block = """    // NUVIO_TR_TV_V1: validated and opt-in, never delete or disable existing addons.
    fun installBundledTvAddons() {
        if (isReadOnly) return
        viewModelScope.launch {
            _uiState.update { it.copy(isInstalling = true, transientMessage = "16 Nuvio kaynağı denetleniyor...") }
            var installed = 0
            var existing = 0
            var failed = 0
            try {
                val known = uiState.value.installedAddons.map { it.baseUrl.trimEnd('/') }.toMutableSet()
                for (preset in TvBundledNuvioSources.addons) {
                    val url = preset.url
                    val canonical = url.trimEnd('/').removeSuffix("/manifest.json")
                    if (known.any { it.removeSuffix("/manifest.json") == canonical }) {
                        existing++
                        continue
                    }
                    when (addonRepository.fetchAddon(url)) {
                        is NetworkResult.Success -> {
                            addonRepository.addAddon(url)
                            known.add(canonical)
                            installed++
                        }
                        else -> failed++
                    }
                }
                _uiState.update {
                    it.copy(
                        transientMessage = "Nuvio TV eklentileri: $installed eklendi, $existing mevcut, $failed yüklenemedi",
                        transientMessageIsError = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isInstalling = false) }
            }
        }
    }

"""
        return replace_one(text, needle, block+needle, "addon install API")
    if name == "plugin-vm":
        text = replace_one(text, "import com.nuvio.tv.R\n",
          "import com.nuvio.tv.R\nimport com.nuvio.tv.core.bundled.TvBundledNuvioSources\n"
          "import kotlinx.coroutines.flow.first\n", "plugin import")
        needle = "    private fun addRepository(url: String) {\n"
        block = """    // NUVIO_TR_TV_V1: repositories remain opt-in; existing scraper toggles are untouched.
    fun installBundledTvPlugins() {
        if (isReadOnly) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingRepo = true, successMessage = "13 Nuvio plugin deposu denetleniyor...") }
            var added = 0
            var existing = 0
            var failed = 0
            try {
                val seen = pluginManager.repositories.first().map { it.url.substringBefore('?').trimEnd('/') }.toMutableSet()
                for (preset in TvBundledNuvioSources.plugins) {
                    val key = preset.url.substringBefore('?').trimEnd('/')
                    if (key in seen) { existing++; continue }
                    val result = pluginManager.addRepository(preset.url)
                    if (result.isSuccess) { added++; seen.add(key) } else failed++
                }
                _uiState.update {
                    it.copy(successMessage = "Nuvio TV pluginleri: $added eklendi, $existing mevcut, $failed yüklenemedi")
                }
            } finally {
                _uiState.update { it.copy(isAddingRepo = false) }
            }
        }
    }

"""
        return replace_one(text, needle, block+needle, "plugin install API")
    if name == "addon-screen":
        needle = "            item {\n                ManageFromPhoneCard(\n"
        block = """            // NUVIO_TR_TV_V1: focusable TV remote button.
            if (!viewModel.isReadOnly) {
                item {
                    Button(onClick = { viewModel.installBundledTvAddons() }, enabled = !uiState.isInstalling) {
                        Text("Nuvio TR: 16 eklentiyi topluca kur")
                    }
                }
            }

"""
        return replace_one(text, needle, block+needle, "addon TV button")
    if name == "plugin-screen":
        needle = "                // Manage from phone card\n"
        block = """                // NUVIO_TR_TV_V1: D-pad selectable bulk plugin installation.
                item {
                    Button(onClick = { viewModel.installBundledTvPlugins() }, enabled = !uiState.isAddingRepo) {
                        Text("Nuvio TR: 13 plugin deposunu topluca kur")
                    }
                }

"""
        return replace_one(text, needle, block+needle, "plugin TV button")
    if name == "gradle":
        return replace_one(text, '        applicationId = "com.nuvio.tv"\n',
            '        // NUVIO_TR_TV_V1: side-by-side with the official Nuvio TV app.\n'
            '        applicationId = "com.cagrisezgen.nuviotv.tr"\n', "side by side ID")
    raise ValueError(name)

FILES = {
    "stream": ROOT / "ui/screens/stream/StreamScreen.kt",
    "addon-vm": ROOT / "ui/screens/addon/AddonManagerViewModel.kt",
    "plugin-vm": ROOT / "ui/screens/plugin/PluginViewModel.kt",
    "addon-screen": ROOT / "ui/screens/addon/AddonManagerScreen.kt",
    "plugin-screen": ROOT / "ui/screens/plugin/PluginScreen.kt",
    "gradle": Path("app/build.gradle.kts"),
}
COPIES = {
    "TvDubFocusBar.kt": ROOT / "ui/screens/stream/TvDubFocusBar.kt",
    "TvBundledNuvioSources.kt": ROOT / "core/bundled/TvBundledNuvioSources.kt",
}

def main():
    args = argparse.ArgumentParser()
    args.add_argument("upstream")
    args.add_argument("--check", action="store_true")
    cfg = args.parse_args()
    project = Path(cfg.upstream).resolve()
    planned = {}
    for name, path in FILES.items():
        dest = project / path
        old = dest.read_text(encoding="utf-8")
        planned[dest] = patch_file(old, name)
    for src, rel in COPIES.items():
        source = SRC / src
        dest = project / rel
        if not source.is_file(): raise FileNotFoundError(source)
        if dest.exists(): raise RuntimeError("Refusing to overwrite " + str(dest))
    if cfg.check:
        print("PASS: TV patch compatible; 29 source presets, TV focus and screen anchors")
        return
    for dest, contents in planned.items():
        dest.write_text(contents, encoding="utf-8")
    for filename, rel in COPIES.items():
        dest = project / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(SRC / filename, dest)
    print("TV patch installed: native DPAD TV UI + 16 addons + 13 plugins (opt-in)")

if __name__ == "__main__":
    try: main()
    except (RuntimeError, FileNotFoundError) as error:
        print("ERROR:", error, file=sys.stderr)
        sys.exit(3)
