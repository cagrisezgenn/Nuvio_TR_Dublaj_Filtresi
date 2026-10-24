#!/usr/bin/env python3
"""Attach the bundled source import panel to Nuvio's existing Addons Settings page.

The importer preserves all existing sources and uses the normal Nuvio
addAddon/addRepository APIs, which validate each remote manifest. CloudStream
repo.json entries are shown as a non-executable catalogue ONLY.
"""
import argparse
from pathlib import Path
import shutil
import sys

UPSTREAM = Path("composeApp/src/commonMain/kotlin/com/nuvio/app/features/settings")
START = "internal fun LazyListScope.addonsSettingsContent() {\n    item {\n"
REPLACEMENT = (
    "internal fun LazyListScope.addonsSettingsContent() {\n"
    "    // NUVIO_TR_SOURCE_BUNDLE_V03: explicit bulk import; no silent installations.\n"
    "    item { SourceBundlePanel() }\n"
    "    item {\n"
)
MARKER = "NUVIO_TR_SOURCE_BUNDLE_V03"
NAMES = ("BundledMediaSources.kt", "SourceBundlePanel.kt")


def patch(source: str) -> str:
    if MARKER in source:
        return source
    if source.count(START) != 1:
        raise ValueError("Unsupported Nuvio AddonsSettingsPage.kt structure; no files changed")
    return source.replace(START, REPLACEMENT, 1)


def install(repo: Path, check: bool) -> None:
    dest = repo / UPSTREAM
    settings_file = dest / "AddonsSettingsPage.kt"
    if not settings_file.exists():
        raise FileNotFoundError(settings_file)
    raw = settings_file.read_text(encoding="utf-8")
    updated = patch(raw)
    assets = Path(__file__).resolve().parent.parent / "src"

    for name in NAMES:
        if not (assets / name).is_file():
            raise FileNotFoundError(assets / name)
        if updated != raw and (dest / name).exists():
            raise ValueError(f"Will not overwrite existing upstream file: {dest / name}")
    if check:
        print("Source bundle patch: compatible. No files modified.")
        return
    if updated == raw:
        print("Source bundle already installed.")
        return

    backup = settings_file.with_suffix(".kt.trsources-original.bak")
    if backup.exists():
        raise ValueError("Backup exists: " + str(backup))
    shutil.copy2(settings_file, backup)
    try:
        for name in NAMES:
            shutil.copy2(assets / name, dest / name)
        settings_file.write_text(updated, encoding="utf-8")
    except Exception:
        shutil.copy2(backup, settings_file)
        for name in NAMES:
            (dest / name).unlink(missing_ok=True)
        raise
    print("Source bundle panel added: " + str(settings_file))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("repository")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    try:
        install(Path(args.repository).resolve(), args.check)
    except (ValueError, FileNotFoundError) as ex:
        print(f"ERROR: {ex}", file=sys.stderr)
        sys.exit(3)
