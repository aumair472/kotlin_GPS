package com.geosnap.feature.collection

import com.geosnap.core.common.DayLabel
import com.geosnap.core.model.CollectionFilter
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem

data class MediaGroup(val label: DayLabel, val items: List<MediaItem>)

data class CollectionUiState(
    val filter: CollectionFilter = CollectionFilter.ALL,
    val search: String = "",
    val groups: List<MediaGroup> = emptyList(),
    val loading: Boolean = true,
    val selectionMode: Boolean = false,
    val selectedIds: Set<MediaId> = emptySet(),
) {
    val isEmpty: Boolean get() = !loading && groups.isEmpty()
}

sealed interface CollectionEffect {
    data class ShareMedia(val contentUris: List<String>) : CollectionEffect
    data object Deleted : CollectionEffect
}
