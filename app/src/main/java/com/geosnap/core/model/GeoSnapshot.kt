package com.geosnap.core.model

import java.time.Instant

/** Display-only postal fields from optional reverse geocoding. */
data class PostalAddress(
    val locality: String? = null,
    val adminArea: String? = null,
    val countryCode: String? = null,
    val formatted: String? = null,
)

/**
 * Capture-time location snapshot (docs/CAMERA_GPS_PIPELINE.md). Never fabricated: when no fix is
 * available the capture stores no snapshot and is marked "Location unavailable".
 */
data class GeoSnapshot(
    val id: LocationId,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val horizontalAccuracyMeters: Float?,
    val providerTimestamp: Instant?,
    val capturedAt: Instant,
    val timezoneId: String,
    val isApproximate: Boolean,
    val isMock: Boolean,
    val provider: String?,
    val address: PostalAddress? = null,
) {
    val quality: LocationQuality
        get() = if (isApproximate) LocationQuality.APPROXIMATE else LocationQuality.PRECISE

    init {
        require(latitude in -90.0..90.0) { "latitude out of range: $latitude" }
        require(longitude in -180.0..180.0) { "longitude out of range: $longitude" }
    }
}
