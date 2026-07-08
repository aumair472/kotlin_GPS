package com.geosnap.core.location

import java.util.Locale
import kotlin.math.abs

/**
 * Formats coordinates for display/stamping. The underlying decimal representation stored in metadata
 * is never localized (docs/LOCALIZATION.md); only the surrounding N/S/E/W label may be localized by
 * the caller. Uses Locale.US for the numeric part to keep '.' decimal separators stable.
 */
object CoordinateFormatter {

    /** e.g. 24.860700 → "24.8607". [decimals] default 4 (≈11 m precision). */
    fun decimal(value: Double, decimals: Int = 4): String =
        String.format(Locale.US, "%.${decimals}f", value)

    fun latitudeMagnitude(latitude: Double, decimals: Int = 4): String =
        decimal(abs(latitude), decimals)

    fun longitudeMagnitude(longitude: Double, decimals: Int = 4): String =
        decimal(abs(longitude), decimals)

    fun isNorth(latitude: Double): Boolean = latitude >= 0
    fun isEast(longitude: Double): Boolean = longitude >= 0

    /** Compact "24.8607° N, 67.0104° E" for stamps where the caller supplies hemisphere letters. */
    fun pair(
        latitude: Double,
        longitude: Double,
        north: String,
        south: String,
        east: String,
        west: String,
        decimals: Int = 4,
    ): String {
        val lat = "${decimal(abs(latitude), decimals)}° ${if (isNorth(latitude)) north else south}"
        val lon = "${decimal(abs(longitude), decimals)}° ${if (isEast(longitude)) east else west}"
        return "$lat, $lon"
    }
}
