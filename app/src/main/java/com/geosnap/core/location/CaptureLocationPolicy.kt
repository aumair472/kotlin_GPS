package com.geosnap.core.location

import com.geosnap.core.model.LocationQuality
import java.time.Duration
import java.time.Instant

data class LocationPolicyConfig(
    val maxAgeForFresh: Duration = Duration.ofSeconds(10),
    val verifiedAccuracyMeters: Float = 25f,
    val approximateAccuracyMeters: Float = 500f,
    val rejectMocks: Boolean = true,
)

/** Outcome of evaluating a cached fix for stamping. */
data class LocationDecision(
    val quality: LocationQuality,
    val acceptableForStamp: Boolean,
    val shouldRequestFresh: Boolean,
)

/**
 * Decides whether a cached fix is good enough to stamp, or whether a fresh high-accuracy request is
 * warranted (docs/CAMERA_GPS_PIPELINE.md capture-time location policy). Pure → unit-tested.
 */
class CaptureLocationPolicy(private val config: LocationPolicyConfig = LocationPolicyConfig()) {

    fun evaluate(fix: LocationFix?, now: Instant): LocationDecision {
        if (fix == null) {
            return LocationDecision(LocationQuality.UNAVAILABLE, acceptableForStamp = false, shouldRequestFresh = true)
        }
        if (config.rejectMocks && fix.isMock) {
            return LocationDecision(LocationQuality.UNAVAILABLE, acceptableForStamp = false, shouldRequestFresh = true)
        }
        val ageOk = !Duration.between(fix.elapsedAt, now).let { it.isNegative.not() && it > config.maxAgeForFresh }
        val accuracy = fix.horizontalAccuracyMeters
        val quality = when {
            accuracy != null && accuracy <= config.verifiedAccuracyMeters -> LocationQuality.PRECISE
            accuracy == null || accuracy <= config.approximateAccuracyMeters -> LocationQuality.APPROXIMATE
            else -> LocationQuality.APPROXIMATE
        }
        val precise = quality == LocationQuality.PRECISE && ageOk
        return LocationDecision(
            quality = quality,
            acceptableForStamp = true,
            // Request a fresh fix when the cached one is stale or only approximate, so the shutter
            // can upgrade confidence without blocking indefinitely.
            shouldRequestFresh = !precise,
        )
    }

    fun isApproximate(decision: LocationDecision): Boolean =
        decision.quality != LocationQuality.PRECISE
}
