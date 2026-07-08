package com.geosnap.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Computes collection group keys/labels from capture instants in the display timezone (pure). */
class DateLabeler(
    private val zone: ZoneId = ZoneId.systemDefault(),
    private val locale: Locale = Locale.getDefault(),
) {
    private val dayFormat = DateTimeFormatter.ofPattern("d MMM yyyy", locale)

    fun dayKey(instant: Instant): LocalDate = instant.atZone(zone).toLocalDate()

    /** Returns "Today"/"Yesterday" sentinels or a formatted date. UI maps sentinels to localized text. */
    fun label(day: LocalDate, today: LocalDate): DayLabel = when (day) {
        today -> DayLabel.Today
        today.minusDays(1) -> DayLabel.Yesterday
        else -> DayLabel.Date(dayFormat.format(day))
    }
}

sealed interface DayLabel {
    data object Today : DayLabel
    data object Yesterday : DayLabel
    data class Date(val text: String) : DayLabel
}
