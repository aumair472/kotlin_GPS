package com.geosnap.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.common.DateLabeler
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.model.CollectionFilter
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val dateLabeler = DateLabeler()
    private val filterFlow = MutableStateFlow(CollectionFilter.ALL)
    private val searchFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    private val _effects = Channel<CollectionEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        combine(
            filterFlow,
            searchFlow.debounce(300).distinctUntilChanged(),
        ) { filter, search -> MediaQuery(filter, search.ifBlank { null }) }
            .flatMapLatest { query -> mediaRepository.observeMediaList(query) }
            .onEach { items -> _state.value = _state.value.copy(groups = group(items), loading = false) }
            .launchIn(viewModelScope)
    }

    private fun group(items: List<MediaItem>): List<MediaGroup> {
        val today = LocalDate.now()
        return items
            .groupBy { dateLabeler.dayKey(it.capturedAt) }
            .toSortedMap(compareByDescending { it })
            .map { (day, dayItems) -> MediaGroup(dateLabeler.label(day, today), dayItems) }
    }

    fun setFilter(filter: CollectionFilter) {
        filterFlow.value = filter
        _state.value = _state.value.copy(filter = filter)
    }

    fun setSearch(query: String) {
        searchFlow.value = query
        _state.value = _state.value.copy(search = query)
    }

    fun toggleSelection(id: MediaId) {
        val current = _state.value.selectedIds
        val next = if (id in current) current - id else current + id
        _state.value = _state.value.copy(selectedIds = next, selectionMode = next.isNotEmpty())
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedIds = emptySet(), selectionMode = false)
    }

    fun shareSelected() {
        val ids = _state.value.selectedIds
        if (ids.isEmpty()) return
        viewModelScope.launch {
            val uris = mediaRepository.getByIds(ids.toList()).mapNotNull { it.contentUri }
            if (uris.isNotEmpty()) _effects.trySend(CollectionEffect.ShareMedia(uris))
        }
    }

    fun deleteSelected() {
        val ids = _state.value.selectedIds
        if (ids.isEmpty()) return
        viewModelScope.launch {
            mediaRepository.delete(ids)
            clearSelection()
            _effects.trySend(CollectionEffect.Deleted)
        }
    }
}
