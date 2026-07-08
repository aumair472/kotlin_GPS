package com.geosnap.feature.reporting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.data.ReportRepository
import com.geosnap.core.model.ReportFilter
import com.geosnap.core.model.ReportQuery
import com.geosnap.core.model.ReportSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ReportingUiState(
    val filter: ReportFilter = ReportFilter.ALL,
    val search: String = "",
    val reports: List<ReportSummary> = emptyList(),
    val loading: Boolean = true,
) {
    val isEmpty: Boolean get() = !loading && reports.isEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ReportingViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val filterFlow = MutableStateFlow(ReportFilter.ALL)
    private val searchFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(ReportingUiState())
    val state: StateFlow<ReportingUiState> = _state.asStateFlow()

    init {
        combine(filterFlow, searchFlow.debounce(300).distinctUntilChanged()) { filter, search ->
            ReportQuery(filter, search.ifBlank { null })
        }
            .flatMapLatest { reportRepository.observeReports(it) }
            .onEach { _state.value = _state.value.copy(reports = it, loading = false) }
            .launchIn(viewModelScope)
    }

    fun setFilter(filter: ReportFilter) {
        filterFlow.value = filter
        _state.value = _state.value.copy(filter = filter)
    }

    fun setSearch(query: String) {
        searchFlow.value = query
        _state.value = _state.value.copy(search = query)
    }

    fun createReport(onCreated: (String) -> Unit) {
        viewModelScope.launch { onCreated(reportRepository.createDraft().value) }
    }
}
