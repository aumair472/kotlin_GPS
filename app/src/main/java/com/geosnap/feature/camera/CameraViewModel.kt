package com.geosnap.feature.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.files.CaptureWorkspace
import com.geosnap.core.location.AddressResolver
import com.geosnap.core.location.CaptureLocationPolicy
import com.geosnap.core.location.LocationFix
import com.geosnap.core.location.LocationGateway
import com.geosnap.core.media.CameraController
import com.geosnap.core.media.CaptureMode
import com.geosnap.core.media.FinalizePhotoRequest
import com.geosnap.core.media.PhotoFinalizer
import com.geosnap.core.media.StampStrings
import androidx.camera.video.VideoRecordEvent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.geosnap.core.common.TimeSource
import com.geosnap.core.media.TimestampOverlayFormatter
import com.geosnap.core.media.worker.VideoOverlayWorker
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationQuality
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val locationGateway: LocationGateway,
    private val cameraController: CameraController,
    private val photoFinalizer: PhotoFinalizer,
    private val mediaRepository: MediaRepository,
    private val workspace: CaptureWorkspace,
    private val workManager: WorkManager,
    private val addressResolver: AddressResolver,
    private val time: TimeSource,
) : ViewModel() {

    private companion object {
        val NAME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.US)
        const val COUNTDOWN_FROM = 3
    }

    private var recordingMediaId: MediaId? = null
    private var recordingSnapshot: GeoSnapshot? = null
    private var recordingStart: Instant = Instant.EPOCH
    private var recordingSourceFile: File? = null
    private var recordingStampLines: List<String> = emptyList()

    private val policy = CaptureLocationPolicy()

    private val _state = MutableStateFlow(CameraUiState())
    val state: StateFlow<CameraUiState> = _state.asStateFlow()

    private val _effects = Channel<CameraEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var locationJob: Job? = null
    private var countdownJob: Job? = null
    private var addressJob: Job? = null
    private var lastGeocodedLat: Double? = null
    private var lastGeocodedLon: Double? = null

    init {
        viewModelScope.launch {
            settings.preferences.collectLatest { prefs ->
                _state.value = _state.value.copy(
                    template = com.geosnap.core.model.TemplateStyle.fromId(prefs.selectedTemplateId),
                    audioEnabled = prefs.videoAudioEnabled,
                    mode = if (prefs.defaultMode == MediaKind.VIDEO) CaptureMode.VIDEO else _state.value.mode,
                )
            }
        }
        viewModelScope.launch {
            mediaRepository.observeLatestReady().collectLatest { item ->
                _state.value = _state.value.copy(latestThumbnailUri = item?.thumbnailUri ?: item?.contentUri)
            }
        }
    }

    /** Called once the camera permission is granted and the preview surface exists. */
    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            val result = cameraController.bind(
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                mode = _state.value.mode,
                torchEnabled = _state.value.torchEnabled,
            )
            _state.value = _state.value.copy(
                hasFlash = cameraController.hasFlashUnit,
                canSwitchLens = cameraController.hasFrontAndBack(),
                isFrontFacing = false,
                cameraError = result.isFailure,
            )
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) startLocationUpdates() else {
            locationJob?.cancel()
            _state.value = _state.value.copy(gpsQuality = LocationQuality.UNAVAILABLE, latestFix = null)
        }
    }

    private fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        _state.value = _state.value.copy(gpsQuality = LocationQuality.UNAVAILABLE)
        locationJob = viewModelScope.launch {
            locationGateway.locationUpdates().collectLatest { fix ->
                val decision = policy.evaluate(fix, time.now())
                _state.value = _state.value.copy(latestFix = fix, gpsQuality = decision.quality)
                fix?.let { maybeResolveAddress(it) }
            }
        }
    }

    /** Debounced, distance-gated reverse geocoding (FIX-04). Off-main, cached, never per-frame. */
    private fun maybeResolveAddress(fix: LocationFix) {
        val movedFar = run {
            val pLat = lastGeocodedLat
            val pLon = lastGeocodedLon
            if (pLat == null || pLon == null) return@run true
            val out = FloatArray(1)
            android.location.Location.distanceBetween(pLat, pLon, fix.latitude, fix.longitude, out)
            out[0] > 50f
        }
        val haveAddress = _state.value.addressStatus == AddressStatus.AVAILABLE && _state.value.resolvedAddress != null
        if (haveAddress && !movedFar) return
        if (addressJob?.isActive == true && !movedFar) return

        addressJob?.cancel()
        addressJob = viewModelScope.launch {
            delay(1_200) // debounce rapid fixes
            _state.value = _state.value.copy(addressStatus = AddressStatus.RESOLVING)
            val address = addressResolver.resolve(fix.latitude, fix.longitude)
            lastGeocodedLat = fix.latitude
            lastGeocodedLon = fix.longitude
            _state.value = if (address?.formatted?.isNotBlank() == true) {
                _state.value.copy(resolvedAddress = address, addressStatus = AddressStatus.AVAILABLE)
            } else {
                _state.value.copy(addressStatus = AddressStatus.UNAVAILABLE)
            }
        }
    }

    fun setMode(mode: CaptureMode) {
        if (_state.value.mode == mode || _state.value.isRecording) return
        if (_state.value.countdown != null) cancelCountdown()
        _state.value = _state.value.copy(mode = mode)
    }

    fun toggleTorch() {
        val next = !_state.value.torchEnabled
        cameraController.setTorch(next)
        _state.value = _state.value.copy(torchEnabled = next)
    }

    fun setZoom(linear: Float) = cameraController.setLinearZoom(linear)

    fun focusAt(previewView: PreviewView, x: Float, y: Float) {
        cameraController.focusAt(previewView.meteringPointFactory, x, y)
    }

    fun switchLens(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        if (_state.value.isRecording) return
        cameraController.toggleLens()
        bindCamera(lifecycleOwner, previewView)
    }

    fun onCapture(strings: StampStrings, locale: Locale) {
        if (_state.value.capturing || _state.value.mode != CaptureMode.PHOTO) return
        _state.value = _state.value.copy(capturing = true)
        viewModelScope.launch {
            try {
                val capturedAt = time.now()
                val zone = ZoneId.systemDefault()
                val decision = policy.evaluate(_state.value.latestFix, capturedAt)
                val fix = if (decision.shouldRequestFresh) {
                    locationGateway.currentLocation() ?: _state.value.latestFix
                } else {
                    _state.value.latestFix
                }
                val freshDecision = policy.evaluate(fix, time.now())
                val snapshot = fix
                    ?.takeIf { freshDecision.acceptableForStamp }
                    ?.toSnapshot(
                        capturedAt,
                        isApproximate = policy.isApproximate(freshDecision),
                        zoneId = zone,
                        address = _state.value.resolvedAddress,
                    )

                val temp = cameraController.takePhoto(workspace.captureDir())
                val result = photoFinalizer.finalize(
                    FinalizePhotoRequest(
                        tempFile = temp,
                        capturedAt = capturedAt,
                        zoneId = zone,
                        style = _state.value.template,
                        location = snapshot,
                        strings = strings,
                        locale = locale,
                    ),
                )
                result.fold(
                    onSuccess = { item ->
                        if (snapshot == null) _effects.trySend(CameraEffect.LocationUnavailableForCapture)
                        _effects.trySend(CameraEffect.CaptureSaved(item.id.value))
                    },
                    onFailure = { _effects.trySend(CameraEffect.CaptureFailed) },
                )
            } catch (e: Throwable) {
                _effects.trySend(CameraEffect.CaptureFailed)
            } finally {
                _state.value = _state.value.copy(capturing = false)
            }
        }
    }

    /**
     * Video shutter action. While idle → run a visible 3→2→1 countdown, then start recording. While
     * counting down → cancel. While recording → stop. The clean source is finalized by
     * [VideoOverlayWorker].
     */
    fun onToggleRecording(strings: StampStrings, locale: Locale) {
        if (_state.value.mode != CaptureMode.VIDEO) return
        // Ignore taps while finalizing so overlapping recordings can't start.
        if (_state.value.recordingPhase == RecordingPhase.FINALIZING) return
        when {
            _state.value.isRecording -> {
                _state.value = _state.value.copy(recordingPhase = RecordingPhase.FINALIZING)
                cameraController.stopRecording()
            }
            _state.value.countdown != null -> cancelCountdown()
            else -> startCountdown(strings, locale)
        }
    }

    private fun startCountdown(strings: StampStrings, locale: Locale) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (n in COUNTDOWN_FROM downTo 1) {
                _state.value = _state.value.copy(countdown = n, recordingPhase = RecordingPhase.COUNTDOWN)
                delay(1_000)
            }
            _state.value = _state.value.copy(countdown = null)
            beginRecording(strings, locale)
        }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        _state.value = _state.value.copy(countdown = null, recordingPhase = RecordingPhase.IDLE)
    }

    /** The clean source is finalized by [VideoOverlayWorker]. */
    private fun beginRecording(strings: StampStrings, locale: Locale) {
        val capturedAt = time.now()
        val zone = ZoneId.systemDefault()
        val decision = policy.evaluate(_state.value.latestFix, capturedAt)
        // Snapshot the selected template + resolved address at the moment recording begins.
        val snapshot = _state.value.latestFix
            ?.takeIf { decision.acceptableForStamp }
            ?.toSnapshot(
                capturedAt,
                isApproximate = policy.isApproximate(decision),
                zoneId = zone,
                address = _state.value.resolvedAddress,
            )
        val lines = TimestampOverlayFormatter.build(_state.value.template, capturedAt, zone, snapshot, strings, locale)
        val sourceFile = File(workspace.videoSourceDir(), "src_${capturedAt.toEpochMilli()}.mp4")

        val started = cameraController.startRecording(sourceFile, _state.value.audioEnabled) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    _state.value = _state.value.copy(
                        isRecording = true, recordingSeconds = 0, recordingPhase = RecordingPhase.RECORDING,
                    )
                }
                is VideoRecordEvent.Status -> {
                    _state.value = _state.value.copy(
                        recordingSeconds = event.recordingStats.recordedDurationNanos / 1_000_000_000,
                    )
                }
                is VideoRecordEvent.Finalize -> {
                    _state.value = _state.value.copy(
                        isRecording = false, recordingSeconds = 0, recordingPhase = RecordingPhase.IDLE,
                    )
                    // Success only at finalization, with a valid (non-empty) output URI.
                    val validOutput = !event.hasError() && sourceFile.exists() && sourceFile.length() > 0
                    if (validOutput) {
                        enqueueVideoProcessing(sourceFile, capturedAt, zone, snapshot, lines)
                        _effects.trySend(CameraEffect.VideoSaved)
                    } else {
                        runCatching { sourceFile.delete() }
                        _effects.trySend(CameraEffect.VideoFailed)
                    }
                }
                else -> Unit
            }
        }
        if (!started) {
            _state.value = _state.value.copy(recordingPhase = RecordingPhase.IDLE, isRecording = false)
            _effects.trySend(CameraEffect.VideoFailed)
        } else {
            recordingMediaId = MediaId.random()
            recordingSnapshot = snapshot
            recordingStart = capturedAt
            recordingSourceFile = sourceFile
            recordingStampLines = lines
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun enqueueVideoProcessing(
        sourceFile: File,
        capturedAt: Instant,
        zone: ZoneId,
        snapshot: GeoSnapshot?,
        lines: List<String>,
    ) {
        viewModelScope.launch {
            val id = recordingMediaId ?: MediaId.random()
            val displayName = "GPSCameraTimestampMap_${NAME_FORMAT.format(capturedAt.atZone(zone))}_${id.value.take(6)}.mp4"
            val now = time.now()
            val processing = MediaItem(
                id = id, kind = MediaKind.VIDEO, status = MediaStatus.PROCESSING,
                contentUri = null, sourceUri = sourceFile.absolutePath, displayName = displayName,
                mimeType = "video/mp4", capturedAt = capturedAt, timezoneId = zone.id,
                durationMs = null, width = null, height = null, sizeBytes = null,
                orientationDegrees = 0, templateId = _state.value.template.id,
                templateVersion = _state.value.template.version,
                renderedStamp = TimestampOverlayFormatter.renderedStamp(lines), location = snapshot,
                addressSearchText = snapshot?.address?.formatted?.lowercase(Locale.getDefault()),
                thumbnailUri = null, checksumSha256 = null, failureCode = null,
                createdAt = now, updatedAt = now,
            )
            mediaRepository.insertProcessing(processing)
            val request = OneTimeWorkRequestBuilder<VideoOverlayWorker>()
                .setInputData(
                    workDataOf(
                        VideoOverlayWorker.KEY_MEDIA_ID to id.value,
                        VideoOverlayWorker.KEY_SOURCE_PATH to sourceFile.absolutePath,
                        VideoOverlayWorker.KEY_DISPLAY_NAME to displayName,
                        VideoOverlayWorker.KEY_STAMP_LINES to lines.joinToString(VideoOverlayWorker.STAMP_SEPARATOR),
                    ),
                )
                .build()
            workManager.enqueueUniqueWork("video_overlay_${id.value}", ExistingWorkPolicy.KEEP, request)
        }
    }

    override fun onCleared() {
        cameraController.unbind()
        super.onCleared()
    }
}
