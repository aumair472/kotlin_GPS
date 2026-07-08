package com.geosnap.core.media

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import com.geosnap.core.model.TemplateStyle
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GalleryExporterTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    private fun item(contentUri: String?) = MediaItem(
        id = MediaId.random(), kind = MediaKind.PHOTO, status = MediaStatus.READY,
        contentUri = contentUri, sourceUri = null, displayName = "p.jpg", mimeType = "image/jpeg",
        capturedAt = Instant.EPOCH, timezoneId = "UTC", durationMs = null, width = 1, height = 1,
        sizeBytes = 1, orientationDegrees = 0, templateId = TemplateStyle.CLASSIC.id, templateVersion = 1,
        renderedStamp = null, location = null, addressSearchText = null, thumbnailUri = null,
        checksumSha256 = null, failureCode = null, createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH,
    )

    @Test
    fun publicMediaStoreUri_reportsAlreadyAvailable() {
        val result = GalleryExporter(context).export(item("content://media/external/images/media/42"))
        assertThat(result).isEqualTo(GalleryResult.AlreadyAvailable)
    }

    @Test
    fun nullContentUri_reportsFailed() {
        assertThat(GalleryExporter(context).export(item(null))).isEqualTo(GalleryResult.Failed)
    }
}
