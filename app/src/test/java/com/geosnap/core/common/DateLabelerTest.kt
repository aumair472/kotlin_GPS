package com.geosnap.core.common

import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import org.junit.Test

class DateLabelerTest {

    private val zone = ZoneId.of("UTC")
    private val labeler = DateLabeler(zone, Locale.US)
    private val today = LocalDate.of(2026, 6, 18)

    @Test
    fun `same day is Today`() {
        val instant = Instant.parse("2026-06-18T08:00:00Z")
        assertThat(labeler.label(labeler.dayKey(instant), today)).isEqualTo(DayLabel.Today)
    }

    @Test
    fun `previous day is Yesterday`() {
        val instant = Instant.parse("2026-06-17T23:00:00Z")
        assertThat(labeler.label(labeler.dayKey(instant), today)).isEqualTo(DayLabel.Yesterday)
    }

    @Test
    fun `older day is formatted date`() {
        val instant = Instant.parse("2024-11-21T10:00:00Z")
        val label = labeler.label(labeler.dayKey(instant), today)
        assertThat(label).isInstanceOf(DayLabel.Date::class.java)
        assertThat((label as DayLabel.Date).text).contains("2024")
    }
}
