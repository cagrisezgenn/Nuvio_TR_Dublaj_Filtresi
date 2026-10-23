#!/usr/bin/env python3
"""Fail-safe patch for NuvioMobile cmp-rewrite's Compose stream selection screen.

Usage: python scripts/install_filter.py /path/to/NuvioMobile
Never changes addon/plugin enablement or the underlying StreamsRepository.
"""
from pathlib import Path
import argparse
import shutil
import sys

PART = Path('composeApp/src/commonMain/kotlin/com/nuvio/app/features/streams')
FLAG = 'TURKISH_DUB_FILTER_V1'


def replace_once(s: str, old: str, new: str, marker: str) -> str:
    count = s.count(old)
    if count != 1:
        raise RuntimeError(f'Safety stop: {marker}: expected 1 exact match, got {count}. '\
                           'Upstream may have changed; no files modified.')
    return s.replace(old, new, 1)


def patch(original: str) -> str:
    if FLAG in original:
        return original
    modified = replace_once(
        original,
        '    val filteredGroups = uiState.filteredGroups\n',
        '    // ' + FLAG + ': presentation-only filtering; source providers remain enabled.\n'
        '    var turkishAudioFilter by remember(uiState.requestToken) { mutableStateOf(TurkishAudioFilterMode.ALL) }\n'
        '    val filteredGroups = TurkishDubFilter.filterGroups(uiState.filteredGroups, turkishAudioFilter)\n',
        'StreamList group collection',
    )
    # Limit to StreamList to avoid accidentally replacing a different LazyColumn.
    begin = modified.index('internal fun StreamList(')
    end = modified.index('private fun LazyListScope.streamSection(', begin)
    head, segment, tail = modified[:begin], modified[begin:end], modified[end:]
    segment = replace_once(
        segment,
        '    ) {\n        sharedStream?.let { shared ->\n',
        '    ) {\n'
        '        item(key = "turkish_audio_filter") {\n'
        '            TurkishDubFilterToolbar(\n'
        '                mode = turkishAudioFilter,\n'
        '                onModeSelected = { turkishAudioFilter = it },\n'
        '                visibleCount = filteredGroups.sumOf { it.streams.size },\n'
        '                totalCount = uiState.filteredGroups.sumOf { it.streams.size },\n'
        '            )\n'
        '        }\n'
        '        sharedStream?.takeIf { TurkishDubFilter.matches(it.stream, turkishAudioFilter) }?.let { shared ->\n',
        'StreamList list content',
    )
    segment = replace_once(
        segment,
        '                    EmptyStateBlock(reason = uiState.emptyStateReason)\n',
        '                    if (turkishAudioFilter == TurkishAudioFilterMode.ALL) {\n'
        '                        EmptyStateBlock(reason = uiState.emptyStateReason)\n'
        '                    } else {\n'
        '                        Text(\n'
        '                            "Bu dil filtresine uyan kaynak henüz bulunamadı. Belirsiz veya Tümü seçeneğini deneyin.",\n'
        '                            modifier = Modifier.padding(16.dp),\n'
        '                            color = MaterialTheme.colorScheme.onSurfaceVariant,\n'
        '                        )\n'
        '                    }\n',
        'StreamList empty state',
    )
    return head + segment + tail


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('repository', help='Locally cloned NuvioMobile repository root')
    parser.add_argument('--check', action='store_true', help='Validate compatibility without writing files')
    args = parser.parse_args()
    repository = Path(args.repository).resolve()
    project_file = repository / PART / 'StreamsScreen.kt'
    if not project_file.is_file():
        print('ERROR: StreamsScreen.kt not found: ' + str(project_file), file=sys.stderr)
        return 2
    original = project_file.read_text(encoding='utf-8')
    try:
        result = patch(original)
    except (ValueError, RuntimeError) as exc:
        print(f'ERROR: {exc}', file=sys.stderr)
        return 3
    if args.check:
        print('Compatible source markers: OK; no files modified.')
        return 0
    if result == original:
        print('Already patched; no changes.')
        return 0
    destination = repository / PART
    this_src = Path(__file__).resolve().parent.parent / 'src'
    for filename in ('TurkishDubFilter.kt', 'TurkishDubFilterToolbar.kt'):
        if (destination / filename).exists():
            print(f'ERROR: Destination already exists: {destination / filename}', file=sys.stderr)
            return 4
    backup = project_file.with_suffix('.kt.trdub-original.bak')
    if backup.exists():
        print(f'ERROR: Backup exists; will not overwrite: {backup}', file=sys.stderr)
        return 5
    # Do all validation before any mutation.
    for filename in ('TurkishDubFilter.kt', 'TurkishDubFilterToolbar.kt'):
        if not (this_src / filename).is_file():
            print(f'ERROR: Patch file missing: {filename}', file=sys.stderr)
            return 6
    shutil.copy2(project_file, backup)
    try:
        for filename in ('TurkishDubFilter.kt', 'TurkishDubFilterToolbar.kt'):
            shutil.copy2(this_src / filename, destination / filename)
        project_file.write_text(result, encoding='utf-8')
    except Exception:
        shutil.copy2(backup, project_file)
        for filename in ('TurkishDubFilter.kt', 'TurkishDubFilterToolbar.kt'):
            (destination / filename).unlink(missing_ok=True)
        raise
    print('PATCH APPLIED to ' + str(project_file))
    print('All provider/addon/plugin settings left untouched.')
    print('Original backup at ' + str(backup))
    print('Next: gradlew.bat :androidApp:assembleFullDebug')
    return 0

if __name__ == '__main__':
    sys.exit(main())
