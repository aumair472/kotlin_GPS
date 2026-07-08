package com.geosnap.feature.camera

import com.geosnap.core.location.LocationFix
import com.geosnap.core.media.CaptureMode
import com.geosnap.core.model.LocationQuality
import com.geosnap.core.model.PostalAddress
import com.geosnap.core.model.TemplateStyle

/** Explicit video capture phases (FINAL-01). */
enum class RecordingPhase { IDLE, COUNTDOWN, RECORDING, FINALIZING }

/** Reverse-geocode lifecycle for the live overlay (FIX-04). */
enum class AddressStatus { NONE, RESOLVING, AVAILABLE, UNAVAILABLE }

/** Immutable camera UI state (UDF). */
data class CameraUiState(
    val mode: CaptureMode = CaptureMode.PHOTO,
    val template: TemplateStyle = TemplateStyle.DEFAULT,
    val gpsQuality: LocationQuality = LocationQuality.UNAVAILABLE,
    val latestFix: LocationFix? = null,
    val resolvedAddress: PostalAddress? = null,
    val addressStatus: AddressStatus = AddressStatus.NONE,
    val hasFlash: Boolean = false,
    val torchEnabled: Boolean = false,
    val canSwitchLens: Boolean = false,
    val isFrontFacing: Boolean = false,
    val capturing: Boolean = false,
    val isRecording: Boolean = false,
    val recordingSeconds: Long = 0,
    /** Non-null (3→2→1) while the pre-record countdown runs; null otherwise. */
    val countdown: Int? = null,
    val recordingPhase: RecordingPhase = RecordingPhase.IDLE,
    val latestThumbnailUri: String? = null,
    val cameraError: Boolean = false,
    val audioEnabled: Boolean = true,
)

sealed interface CameraEffect {
    data class CaptureSaved(val mediaId: String) : CameraEffect
    data object CaptureFailed : CameraEffect
    data object VideoSaved : CameraEffect
    data object VideoFailed : CameraEffect
    data object LocationUnavailableForCapture : CameraEffect
    data class ShowMessage(val isError: Boolean, val text: String) : CameraEffect
}
