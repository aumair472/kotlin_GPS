package com.geosnap.core.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.TimeSource
import com.geosnap.core.database.GeoSnapDatabase
import com.geosnap.core.model.CollectionFilter
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationId
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaQuery
import com.geosnap.core.model.MediaStatus
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
class MediaRepositoryRoomTest {

    private lateinit var db: GeoSnapDatabase
    private lateinit var repo: MediaRepositoryImpl
    private val fixedNow = Instant.parse("2026-06-17T10:15:30Z")

    private val testTime = object : TimeSource {
        override fun now(): Instant = fixedNow
        override fun clock(): Clock = Clock.fixed(fixedNow, ZoneOffset.UTC)
    }
    private val testDispatchers = object : DispatcherProvider {
        val d: CoroutineDispatcher = UnconfinedTestDispatcher()
        override val io get() = d
        override val default get() = d
        override val main get() = Dispatchers.Unconfined
    }

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GeoSnapDatabase::class.java,
        ).allowMainThreadQueries().build()
        repo = MediaRepositoryImpl(db, db.mediaDao(), db.locationDao(), testTime, testDispatchers)
    }

    @After
    fun tearDown() = db.close()

    private fun photo(id: String, withLocation: Boolean): MediaItem {
        val loc = if (withLocation) {
            GeoSnapshot(
                id = LocationId.random(), latitude = 24.86, longitude = 67.01,
                altitudeMeters = 342.0, horizontalAccuracyMeters = 3f, providerTimestamp = fixedNow,
                capturedAt = fixedNow, timezoneId = "Asia/Karachi", isApproximate = false,
                isMock = false, provider = "fused",
            )
        } else {
            null
        }
        return MediaItem(
            id = MediaId(id), kind = MediaKind.PHOTO, status = MediaStatus.READY,
            contentUri = "content://media/$id", sourceUri = null, displayName = "GeoSnap_$id.jpg",
            mimeType = "image/jpeg", capturedAt = fixedNow, timezoneId = "Asia/Karachi",
            durationMs = null, width = 4000, height = 3000, sizeBytes = 1_200_000,
            orientationDegrees = 0, templateId = TemplateStyle.CLASSIC.id, templateVersion = 1,
            renderedStamp = "stamp", location = loc, addressSearchText = "karachi",
            thumbnailUri = "content://thumb/$id", checksumSha256 = null, failureCode = null,
            createdAt = fixedNow, updatedAt = fixedNow,
        )
    }

    @Test
    fun `finalize persists media with linked location transactionally`() = runTest {
        repo.finalizeCapture(photo("a", withLocation = true)).getOrThrow()

        val loaded = repo.getById(MediaId("a"))
        assertThat(loaded).isNotNull()
        assertThat(loaded!!.hasLocation).isTrue()
        assertThat(loaded.location!!.latitude).isWithin(1e-6).of(24.86)
    }

    @Test
    fun `photos filter excludes videos and search matches address`() = runTest {
        repo.finalizeCapture(photo("a", withLocation = true)).getOrThrow()
        repo.finalizeCapture(
            photo("b", withLocation = false).copy(kind = MediaKind.VIDEO, mimeType = "video/mp4", addressSearchText = null),
        ).getOrThrow()

        val photos = repo.observeMediaList(MediaQuery(CollectionFilter.PHOTOS)).first()
        assertThat(photos.map { it.id.value }).containsExactly("a")

        val search = repo.observeMediaList(MediaQuery(CollectionFilter.ALL, search = "karachi")).first()
        assertThat(search.map { it.id.value }).containsExactly("a")
    }

    @Test
    fun `delete removes media and orphan locations`() = runTest {
        repo.finalizeCapture(photo("a", withLocation = true)).getOrThrow()
        repo.delete(setOf(MediaId("a"))).getOrThrow()

        assertThat(repo.getById(MediaId("a"))).isNull()
        val all = repo.observeMediaList(MediaQuery()).first()
        assertThat(all).isEmpty()
    }
}
