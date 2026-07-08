package com.geosnap.core.model

import java.time.Instant

/**
 * A captured photo or video and its metadata. Domain model — never a Room entity (mapped at the
 * data boundary). `contentUri`/`thumbnailUri` are stringified content URIs.
 */
data class MediaItem(
    val id: MediaId,
    val kind: MediaKind,
    val status: MediaStatus,
    val contentUri: String?,
    val sourceUri: String?,
    val displayName: String,
    val mimeType: String,
    val capturedAt: Instant,
    val timezoneId: String,
    val durationMs: Long?,
    val width: Int?,
    val height: Int?,
    val sizeBytes: Long?,
    val orientationDegrees: Int,
    val templateId: String,
    val templateVersion: Int,
    val renderedStamp: String?,
    val location: GeoSnapshot?,
    val addressSearchText: String?,
    val thumbnailUri: String?,
    val checksumSha256: String?,
    val failureCode: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val hasLocation: Boolean get() = location != null
    val isVideo: Boolean get() = kind == MediaKind.VIDEO
}
