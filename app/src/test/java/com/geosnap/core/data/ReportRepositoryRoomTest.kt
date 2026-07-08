package com.geosnap.core.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.TimeSource
import com.geosnap.core.database.GeoSnapDatabase
import com.geosnap.core.model.ExportStatus
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import com.geosnap.core.model.ReportFilter
import com.geosnap.core.model.ReportQuery
import com.geosnap.core.model.ReportStatus
import com.geosnap.core.model.TemplateStyle
import com.google.common.truth.Truth.assertThat
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ReportRepositoryRoomTest {

    private lateinit var db: GeoSnapDatabase
    private lateinit var reportRepo: ReportRepositoryImpl
    private lateinit var mediaRepo: MediaRepositoryImpl
    private val fixedNow = Instant.parse("2026-06-18T10:00:00Z")

    private val time = object : TimeSource {
        override fun now() = fixedNow
        override fun clock(): Clock = Clock.fixed(fixedNow, ZoneOffset.UTC)
    }
    private val dispatchers = object : DispatcherProvider {
        val d: CoroutineDispatcher = UnconfinedTestDispatcher()
        override val io get() = d
        override val default get() = d
        override val main get() = Dispatchers.Unconfined
    }

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), GeoSnapDatabase::class.java)
            .allowMainThreadQueries().build()
        reportRepo = ReportRepositoryImpl(db, db.reportDao(), db.reportMediaDao(), db.reportExportDao(), db.locationDao(), time, dispatchers)
        mediaRepo = MediaRepositoryImpl(db, db.mediaDao(), db.locationDao(), time, dispatchers)
    }

    @After
    fun tearDown() = db.close()

    private fun photo(id: String) = MediaItem(
        id = MediaId(id), kind = MediaKind.PHOTO, status = MediaStatus.READY,
        contentUri = "content://m/$id", sourceUri = null, displayName = "p_$id.jpg", mimeType = "image/jpeg",
        capturedAt = fixedNow, timezoneId = "UTC", durationMs = null, width = 100, height = 100, sizeBytes = 1,
        orientationDegrees = 0, templateId = TemplateStyle.CLASSIC.id, templateVersion = 1, renderedStamp = null,
        location = null, addressSearchText = null, thumbnailUri = "content://t/$id", checksumSha256 = null,
        failureCode = null, createdAt = fixedNow, updatedAt = fixedNow,
    )

    @Test
    fun `draft is persisted immediately and shows in list`() = runTest {
        val id = reportRepo.createDraft()
        val summaries = reportRepo.observeReports(ReportQuery(ReportFilter.DRAFT)).first()
        assertThat(summaries.map { it.id }).contains(id)
    }

    @Test
    fun `attach media updates counts`() = runTest {
        mediaRepo.finalizeCapture(photo("a")).getOrThrow()
        mediaRepo.finalizeCapture(photo("b")).getOrThrow()
        val id = reportRepo.createDraft()
        reportRepo.attachMedia(id, listOf(MediaId("a"), MediaId("b"))).getOrThrow()

        val summary = reportRepo.observeReports(ReportQuery()).first().first { it.id == id }
        assertThat(summary.photoCount).isEqualTo(2)
        assertThat(summary.previewThumbnailUris).hasSize(2)
    }

    @Test
    fun `export lifecycle transitions report to EXPORTED`() = runTest {
        val id = reportRepo.createDraft()
        val exportId = reportRepo.createExport(id)
        reportRepo.markExportRunning(exportId)
        reportRepo.completeExport(exportId, "content://export/1", 2048, "abc").getOrThrow()

        val draft = reportRepo.getReport(id)!!
        assertThat(draft.report.status).isEqualTo(ReportStatus.EXPORTED)
        assertThat(draft.latestExport?.status).isEqualTo(ExportStatus.READY)
        assertThat(draft.latestExport?.outputUri).isEqualTo("content://export/1")
    }

    @Test
    fun `deleting report cascades attachments`() = runTest {
        mediaRepo.finalizeCapture(photo("a")).getOrThrow()
        val id = reportRepo.createDraft()
        reportRepo.attachMedia(id, listOf(MediaId("a"))).getOrThrow()
        reportRepo.delete(id).getOrThrow()

        assertThat(reportRepo.getReport(id)).isNull()
        // media itself survives report deletion
        assertThat(mediaRepo.getById(MediaId("a"))).isNotNull()
    }
}
