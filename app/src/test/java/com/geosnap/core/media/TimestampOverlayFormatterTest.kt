package com.geosnap.core.media

import com.geosnap.core.location.CoordinateFormatter
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationId
import com.geosnap.core.model.PostalAddress
import com.geosnap.core.model.TemplateStyle
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import org.junit.Test

class TimestampOverlayFormatterTest {

    private val zone = ZoneId.of("Asia/Karachi")
    private val capturedAt = Instant.parse("2024-11-21T09:32:05Z")
    private val strings = StampStrings(
        north = "N", south = "S", east = "E", west = "W",
        brandLabel = "GeoSnap", locationUnavailable = "Location unavailable",
        altitudeLabel = { "Alt: $it m" }, accuracyLabel = { "±$it m" },
    )

    private val location = GeoSnapshot(
        id = LocationId.random(), latitude = 24.8607, longitude = 67.0104,
        altitudeMeters = 342.0, horizontalAccuracyMeters = 3f, providerTimestamp = capturedAt,
        capturedAt = capturedAt, timezoneId = zone.id, isApproximate = false, isMock = false,
        provider = "fused", address = PostalAddress(formatted = "Karachi, Sindh, Pakistan"),
    )

    @Test
    fun `minimal shows only date-time, no coordinates`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.MINIMAL, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines).hasSize(1)
        assertThat(lines.none { it.contains("N") && it.contains("E") }).isTrue()
    }

    @Test
    fun `classic includes coordinates`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.CLASSIC, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines.any { it.contains("24.8607° N") && it.contains("67.0104° E") }).isTrue()
    }

    @Test
    fun `detailed includes altitude, accuracy and address`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.DETAILED, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines.any { it == "Alt: 342 m" }).isTrue()
        assertThat(lines.any { it == "±3 m" }).isTrue()
        assertThat(lines.any { it.contains("Karachi") }).isTrue()
    }

    @Test
    fun `reporter is branded`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.REPORTER, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines.first()).isEqualTo("GeoSnap")
    }

    @Test
    fun `classic without location marks unavailable not fabricated`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.CLASSIC, capturedAt, zone, null, strings, Locale.US)
        assertThat(lines).contains("Location unavailable")
        assertThat(lines.none { it.contains("°") }).isTrue()
    }

    @Test
    fun `detailed without address omits address line (no fabrication)`() {
        val noAddr = location.copy(address = null)
        val lines = TimestampOverlayFormatter.build(TemplateStyle.DETAILED, capturedAt, zone, noAddr, strings, Locale.US)
        assertThat(lines.any { it.contains("Karachi") }).isFalse()
        // still has coords + altitude + accuracy
        assertThat(lines.any { it.contains("24.8607° N") }).isTrue()
        assertThat(lines.any { it == "Alt: 342 m" }).isTrue()
    }

    @Test
    fun `classic ignores address even when present`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.CLASSIC, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines.any { it.contains("Karachi") }).isFalse()
        assertThat(lines.any { it == "Alt: 342 m" }).isFalse()
    }

    @Test
    fun `reporter with address shows brand first and address line`() {
        val lines = TimestampOverlayFormatter.build(TemplateStyle.REPORTER, capturedAt, zone, location, strings, Locale.US)
        assertThat(lines.first()).isEqualTo("GeoSnap")
        assertThat(lines.any { it.contains("Karachi, Sindh, Pakistan") }).isTrue()
    }

    @Test
    fun `coordinate formatter keeps US decimal separator`() {
        assertThat(CoordinateFormatter.decimal(24.8607, 4)).isEqualTo("24.8607")
        assertThat(CoordinateFormatter.isNorth(-1.0)).isFalse()
        assertThat(CoordinateFormatter.isEast(-1.0)).isFalse()
    }
}
