package com.geosnap.core.model

import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.junit.Assert.assertThrows
import org.junit.Test

class ModelTest {

    @Test
    fun `language fromTag resolves exact and base tags`() {
        assertThat(AppLanguage.fromTag("ur")).isEqualTo(AppLanguage.URDU)
        assertThat(AppLanguage.fromTag("zh-CN")).isEqualTo(AppLanguage.CHINESE_SIMPLIFIED)
        assertThat(AppLanguage.fromTag("fr-FR")).isEqualTo(AppLanguage.FRENCH)
        assertThat(AppLanguage.fromTag(null)).isNull()
        assertThat(AppLanguage.fromTag("xx")).isNull()
    }

    @Test
    fun `rtl languages flagged`() {
        assertThat(AppLanguage.ARABIC.isRtl).isTrue()
        assertThat(AppLanguage.URDU.isRtl).isTrue()
        assertThat(AppLanguage.ENGLISH.isRtl).isFalse()
    }

    @Test
    fun `template style resolves by id with default fallback`() {
        assertThat(TemplateStyle.fromId("detailed")).isEqualTo(TemplateStyle.DETAILED)
        assertThat(TemplateStyle.fromId(null)).isEqualTo(TemplateStyle.DEFAULT)
        assertThat(TemplateStyle.fromId("nope")).isEqualTo(TemplateStyle.DEFAULT)
    }

    @Test
    fun `template catalog covers every style`() {
        TemplateStyle.entries.forEach { style ->
            assertThat(TemplateCatalog.spec(style).style).isEqualTo(style)
        }
        assertThat(TemplateCatalog.spec(TemplateStyle.MINIMAL).showCoordinates).isFalse()
        assertThat(TemplateCatalog.spec(TemplateStyle.DETAILED).showAddress).isTrue()
        assertThat(TemplateCatalog.spec(TemplateStyle.REPORTER).branded).isTrue()
    }

    @Test
    fun `geosnapshot rejects out of range coordinates`() {
        assertThrows(IllegalArgumentException::class.java) {
            GeoSnapshot(
                id = LocationId.random(), latitude = 120.0, longitude = 0.0,
                altitudeMeters = null, horizontalAccuracyMeters = null, providerTimestamp = null,
                capturedAt = Instant.EPOCH, timezoneId = "UTC", isApproximate = false,
                isMock = false, provider = null,
            )
        }
    }

    @Test
    fun `geosnapshot quality reflects approximate flag`() {
        val base = GeoSnapshot(
            id = LocationId.random(), latitude = 24.86, longitude = 67.01,
            altitudeMeters = 342.0, horizontalAccuracyMeters = 3f, providerTimestamp = null,
            capturedAt = Instant.EPOCH, timezoneId = "Asia/Karachi", isApproximate = false,
            isMock = false, provider = "fused",
        )
        assertThat(base.quality).isEqualTo(LocationQuality.PRECISE)
        assertThat(base.copy(isApproximate = true).quality).isEqualTo(LocationQuality.APPROXIMATE)
    }
}
