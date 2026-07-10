package com.geosnap.feature.mediadetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.TimeSource
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.location.AddressResolver
import com.geosnap.core.location.LocationGateway
import com.geosnap.core.media.GalleryExporter
import com.geosnap.core.media.GalleryResult
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val galleryExporter: GalleryExporter,
    private val locationGateway: LocationGateway,
    private val addressResolver: AddressResolver,
    private val time: TimeSource,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    private val mediaId = MediaId(savedStateHandle.get<String>(Routes.Arg.MEDIA_ID).orEmpty())

    val media: StateFlow<MediaItem?> = mediaRepository.observeMediaById(mediaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _exporting = MutableStateFlow(false)
    val exporting: StateFlow<Boolean> = _exporting.asStateFlow()

    private val _settingLocation = MutableStateFlow(false)
    val settingLocation: StateFlow<Boolean> = _settingLocation.asStateFlow()

    private val _effects = Channel<MediaDetailEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun share() {
        viewModelScope.launch {
            mediaRepository.getById(mediaId)?.contentUri?.let { _effects.trySend(MediaDetailEffect.Share(it)) }
        }
    }

    /** Save to gallery. Guarded against repeated taps so no accidental duplicate export. */
    fun saveToGallery() {
        if (_exporting.value) return
        _exporting.value = true
        viewModelScope.launch {
            try {
                val item = mediaRepository.getById(mediaId)
                if (item == null) {
                    _effects.trySend(MediaDetailEffect.GalleryResultMsg(GalleryResult.Failed, isVideo = false))
                    return@launch
                }
                val result = withContext(dispatchers.io) { galleryExporter.export(item) }
                _effects.trySend(MediaDetailEffect.GalleryResultMsg(result, item.kind == MediaKind.VIDEO))
            } finally {
                _exporting.value = false
            }
        }
    }

    /** Capture the device's current location, reverse-geocode it, and save it to this image. */
    fun setLocation() {
        if (_settingLocation.value) return
        _settingLocation.value = true
        viewModelScope.launch {
            try {
                val fix = locationGateway.currentLocation()
                if (fix == null) {
                    _effects.trySend(MediaDetailEffect.LocationResult(success = false))
                    return@launch
                }
                val address = withContext(dispatchers.io) { addressResolver.resolve(fix.latitude, fix.longitude) }
                val snapshot = fix.toSnapshot(
                    capturedAt = time.now(),
                    isApproximate = (fix.horizontalAccuracyMeters ?: Float.MAX_VALUE) > 25f,
                    address = address,
                )
                val result = mediaRepository.updateLocation(mediaId, snapshot)
                _effects.trySend(MediaDetailEffect.LocationResult(success = result.isSuccess))
            } finally {
                _settingLocation.value = false
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            mediaRepository.delete(setOf(mediaId))
            _effects.trySend(MediaDetailEffect.Deleted)
        }
    }
}

sealed interface MediaDetailEffect {
    data class Share(val contentUri: String) : MediaDetailEffect
    data class GalleryResultMsg(val result: GalleryResult, val isVideo: Boolean) : MediaDetailEffect
    data class LocationResult(val success: Boolean) : MediaDetailEffect
    data object Deleted : MediaDetailEffect
}
