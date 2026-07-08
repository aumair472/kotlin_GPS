package com.geosnap.core.location

import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationId
import com.geosnap.core.model.PostalAddress
import java.time.Instant
import java.time.ZoneId

/** Raw location reading from the provider before it becomes a persisted [GeoSnapshot]. */
data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val horizontalAccuracyMeters: Float?,
    val elapsedAt: Instant,
    val isMock: Boolean,
    val provider: String?,
) {
    /** Promote to a persistable snapshot captured [capturedAt]. */
    fun toSnapshot(
        capturedAt: Instant,
        isApproximate: Boolean,
        zoneId: ZoneId = ZoneId.systemDefault(),
        address: PostalAddress? = null,
    ): GeoSnapshot = GeoSnapshot(
        id = LocationId.random(),
        latitude = latitude,
        longitude = longitude,
        altitudeMeters = altitudeMeters,
        horizontalAccuracyMeters = horizontalAccuracyMeters,
        providerTimestamp = elapsedAt,
        capturedAt = capturedAt,
        timezoneId = zoneId.id,
        isApproximate = isApproximate,
        isMock = isMock,
        provider = provider,
        address = address,
    )
}
