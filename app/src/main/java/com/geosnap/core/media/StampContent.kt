package com.geosnap.core.media

import com.geosnap.core.location.CoordinateFormatter
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.TemplateCatalog
import com.geosnap.core.model.TemplateSpec
import com.geosnap.core.model.TemplateStyle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Localized hemisphere + label strings resolved from resources, passed into the pure builder. */
data class StampStrings(
    val north: String,
    val south: String,
    val east: String,
    val west: String,
    val brandLabel: String,
    val locationUnavailable: String,
    val altitudeLabel: (String) -> String,
    val accuracyLabel: (String) -> String,
)

/**
 * Builds the ordered text lines a stamp renders, per [TemplateSpec]. Pure (no Android) so the exact
 * stamp content is unit-testable and identical across the live overlay, final media, and preview.
 */
object TimestampOverlayFormatter {

    fun build(
        style: TemplateStyle,
        capturedAt: Instant,
        zoneId: ZoneId,
        location: GeoSnapshot?,
        strings: StampStrings,
        locale: Locale,
    ): List<String> {
        val spec = TemplateCatalog.spec(style)
        val lines = mutableListOf<String>()

        if (spec.branded) lines += strings.brandLabel

        if (spec.showDateTime) {
            val formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(locale)
                .withZone(zoneId)
            lines += formatter.format(capturedAt)
        }

        if (location == null) {
            if (spec.showCoordinates) lines += strings.locationUnavailable
            return lines
        }

        if (spec.showCoordinates) {
            lines += CoordinateFormatter.pair(
                latitude = location.latitude,
                longitude = location.longitude,
                north = strings.north, south = strings.south,
                east = strings.east, west = strings.west,
            )
        }
        if (spec.showAltitude) {
            location.altitudeMeters?.let { alt ->
                lines += strings.altitudeLabel(CoordinateFormatter.decimal(alt, 0))
            }
        }
        if (spec.showAccuracy) {
            location.horizontalAccuracyMeters?.let { acc ->
                lines += strings.accuracyLabel(CoordinateFormatter.decimal(acc.toDouble(), 0))
            }
        }
        if (spec.showAddress) {
            location.address?.formatted?.takeIf { it.isNotBlank() }?.let { lines += it }
        }
        return lines
    }

    /** Single-line audit string persisted to `rendered_stamp`. */
    fun renderedStamp(lines: List<String>): String = lines.joinToString(" • ")
}
