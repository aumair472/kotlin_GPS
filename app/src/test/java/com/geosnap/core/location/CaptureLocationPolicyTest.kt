package com.geosnap.core.location

import com.geosnap.core.model.LocationQuality
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.junit.Test

class CaptureLocationPolicyTest {

    private val policy = CaptureLocationPolicy()
    private val now = Instant.parse("2026-06-17T10:00:10Z")

    private fun fix(accuracy: Float?, ageSec: Long, mock: Boolean = false) = LocationFix(
        latitude = 24.86, longitude = 67.01, altitudeMeters = 342.0,
        horizontalAccuracyMeters = accuracy, elapsedAt = now.minusSeconds(ageSec),
        isMock = mock, provider = "fused",
    )

    @Test
    fun `null fix is unavailable and requests fresh`() {
        val d = policy.evaluate(null, now)
        assertThat(d.quality).isEqualTo(LocationQuality.UNAVAILABLE)
        assertThat(d.acceptableForStamp).isFalse()
        assertThat(d.shouldRequestFresh).isTrue()
    }

    @Test
    fun `mock fix is rejected when rejectMocks`() {
        val d = policy.evaluate(fix(accuracy = 5f, ageSec = 1, mock = true), now)
        assertThat(d.acceptableForStamp).isFalse()
        assertThat(d.quality).isEqualTo(LocationQuality.UNAVAILABLE)
    }

    @Test
    fun `fresh accurate fix is precise and acceptable`() {
        val d = policy.evaluate(fix(accuracy = 8f, ageSec = 2), now)
        assertThat(d.quality).isEqualTo(LocationQuality.PRECISE)
        assertThat(d.acceptableForStamp).isTrue()
        assertThat(d.shouldRequestFresh).isFalse()
    }

    @Test
    fun `stale but accurate fix is acceptable yet requests fresh`() {
        val d = policy.evaluate(fix(accuracy = 8f, ageSec = 60), now)
        assertThat(d.acceptableForStamp).isTrue()
        assertThat(d.shouldRequestFresh).isTrue()
    }

    @Test
    fun `low accuracy fix is approximate`() {
        val d = policy.evaluate(fix(accuracy = 120f, ageSec = 1), now)
        assertThat(d.quality).isEqualTo(LocationQuality.APPROXIMATE)
        assertThat(policy.isApproximate(d)).isTrue()
    }
}
