package com.nuvio.app.features.streams

data class StreamItem(
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    val addonName: String = "",
    val behaviorHints: StreamBehaviorHints = StreamBehaviorHints(),
    val clientResolve: StreamClientResolve? = null,
)
data class StreamBehaviorHints(val filename: String? = null)
data class StreamClientResolve(val filename: String? = null, val torrentName: String? = null, val stream: StreamClientResolveStream? = null)
data class StreamClientResolveStream(val raw: StreamClientResolveRaw? = null)
data class StreamClientResolveRaw(val filename: String? = null, val parsed: StreamClientResolveParsed? = null)
data class StreamClientResolveParsed(
    val rawTitle: String? = null,
    val parsedTitle: String? = null,
    val languages: List<String> = emptyList(),
)
data class AddonStreamGroup(val streams: List<StreamItem>, val isLoading: Boolean = false)
