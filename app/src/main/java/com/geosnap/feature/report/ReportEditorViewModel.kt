package com.geosnap.feature.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.data.ReportRepository
import com.geosnap.core.files.worker.ReportExportWorker
import com.geosnap.core.location.LocationGateway
import com.geosnap.core.common.TimeSource
import com.geosnap.core.model.ExportStatus
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.Report
import com.geosnap.core.model.ReportExport
import com.geosnap.core.model.ReportId
import com.geosnap.core.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class ReportEditorUiState(
    val title: String = "",
    val notes: String = "",
    val reportInstant: Instant = Instant.EPOCH,
    val timezoneId: String = ZoneId.systemDefault().id,
    val locationLabel: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val gpsLive: Boolean = false,
    val attachments: List<MediaItem> = emptyList(),
    val pickerMedia: List<MediaItem> = emptyList(),
    val showPicker: Boolean = false,
    val export: ReportExport? = null,
    val loaded: Boolean = false,
)

sealed interface ReportEditorEffect {
    data class Share(val contentUri: String) : ReportEditorEffect
    data class SaveAs(val contentUri: String) : ReportEditorEffect
    data object ExportFailed : ReportEditorEffect
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ReportEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reportRepository: ReportRepository,
    private val mediaRepository: MediaRepository,
    private val locationGateway: LocationGateway,
    private val workManager: WorkManager,
    private val time: TimeSource,
) : ViewModel() {

    private val reportId = ReportId(savedStateHandle.get<String>(Routes.Arg.REPORT_ID).orEmpty())

    private val _state = MutableStateFlow(ReportEditorUiState())
    val state: StateFlow<ReportEditorUiState> = _state.asStateFlow()

    private val _effects = Channel<ReportEditorEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val saveTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var loadedReport: Report? = null

    init {
        reportRepository.observeReport(reportId).onEach { draft ->
            if (draft == null) return@onEach
            loadedReport = draft.report
            val attachments = mediaRepository.getByIds(draft.attachments.sortedBy { it.sortOrder }.map { it.mediaId })
            val loc = draft.report.location
            _state.value = _state.value.copy(
                title = if (_state.value.loaded) _state.value.title else draft.report.title,
                notes = if (_state.value.loaded) _state.value.notes else draft.report.notes,
                reportInstant = draft.report.reportInstant,
                timezoneId = draft.report.timezoneId,
                locationLabel = loc?.address?.formatted,
                latitude = loc?.latitude, longitude = loc?.longitude,
                altitude = loc?.altitudeMeters, accuracy = loc?.horizontalAccuracyMeters,
                attachments = attachments,
                export = draft.latestExport,
                loaded = true,
            )
        }.launchIn(viewModelScope)

        // Durable auto-save: coalesce rapid edits then persist.
        saveTrigger.debounce(400).onEach { persist() }.launchIn(viewModelScope)

        // Picker source: all ready media.
        mediaRepository.observeMediaList(com.geosnap.core.model.MediaQuery())
            .onEach { _state.value = _state.value.copy(pickerMedia = it) }
            .launchIn(viewModelScope)
    }

    fun onTitleChange(value: String) {
        _state.value = _state.value.copy(title = value)
        saveTrigger.tryEmit(Unit)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value)
        saveTrigger.tryEmit(Unit)
    }

    private suspend fun persist() {
        val base = loadedReport ?: return
        reportRepository.saveReport(
            base.copy(title = _state.value.title.trim(), notes = _state.value.notes.trim()),
        )
    }

    fun refreshLocation() {
        _state.value = _state.value.copy(gpsLive = true)
        viewModelScope.launch {
            val fix = locationGateway.currentLocation()
            if (fix != null) {
                val snapshot = fix.toSnapshot(time.now(), isApproximate = (fix.horizontalAccuracyMeters ?: Float.MAX_VALUE) > 25f)
                reportRepository.updateLocation(reportId, snapshot)
            }
            _state.value = _state.value.copy(gpsLive = false)
        }
    }

    fun openPicker() { _state.value = _state.value.copy(showPicker = true) }
    fun closePicker() { _state.value = _state.value.copy(showPicker = false) }

    fun attach(ids: List<MediaId>) {
        viewModelScope.launch {
            reportRepository.attachMedia(reportId, ids)
            closePicker()
        }
    }

    fun detach(id: MediaId) {
        viewModelScope.launch { reportRepository.detachMedia(reportId, id) }
    }

    fun exportPdf() {
        viewModelScope.launch {
            persist()
            val exportId = reportRepository.createExport(reportId)
            val request = OneTimeWorkRequestBuilder<ReportExportWorker>()
                .setInputData(
                    workDataOf(
                        ReportExportWorker.KEY_REPORT_ID to reportId.value,
                        ReportExportWorker.KEY_EXPORT_ID to exportId.value,
                    ),
                )
                .build()
            workManager.enqueueUniqueWork("report_export_${reportId.value}", ExistingWorkPolicy.REPLACE, request)
        }
    }

    fun share() {
        val export = _state.value.export
        if (export?.status == ExportStatus.READY && export.outputUri != null) {
            _effects.trySend(ReportEditorEffect.Share(export.outputUri!!))
            viewModelScope.launch { reportRepository.markExportShared(export.id) }
        } else {
            exportPdf()
        }
    }

    fun saveAs() {
        val export = _state.value.export
        if (export?.status == ExportStatus.READY && export.outputUri != null) {
            _effects.trySend(ReportEditorEffect.SaveAs(export.outputUri!!))
        } else {
            exportPdf()
        }
    }
}
