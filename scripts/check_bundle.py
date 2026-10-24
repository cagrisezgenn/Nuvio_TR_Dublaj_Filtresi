#!/usr/bin/env python3
"""Fail fast if the bundled source snapshot stops being the requested 29 Nuvio records."""
from pathlib import Path
import re

s = (Path(__file__).resolve().parents[1] / "src/BundledMediaSources.kt").read_text("utf-8")
items = re.findall(
    r'^\s*NuvioSourcePreset\("([^"]+)",\s*"([^"]+)",\s*"([^"]+)",\s*SourceKind\.(ADDON|PLUGIN),',
    s, re.MULTILINE,
)
assert len(items) == 29, f"Expected 29 Nuvio entries, found {len(items)}"
assert len({it[0] for it in items}) == 29, "Duplicate upstream source IDs"
assert len({it[2] for it in items}) == 29, "Duplicate URL"
addons = [x for x in items if x[3] == "ADDON"]
plugins = [x for x in items if x[3] == "PLUGIN"]
assert len(addons) == 16 and len(plugins) == 13, (len(addons), len(plugins))
assert all("/repo.json" not in x[2] for x in items), "CloudStream repository embedded by mistake"
assert all(x[2].startswith("https://") for x in items)
assert any(x[1] == "MANİTUX" and x[3] == "PLUGIN" for x in items)
print("PASS: exactly 29 Nuvio source presets: 16 addons / 13 plugins; no CloudStream repo.json")
