package com.geosnap.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.TimeSource
import com.geosnap.core.common.geoSnapRunCatching
import com.geosnap.core.database.GeoSnapDatabase
import com.geosnap.core.database.dao.LocationDao
import com.geosnap.core.database.dao.MediaDao
import com.geosnap.core.database.mapper.toDomain
import com.geosnap.core.database.mapper.toEntity
import com.geosnap.core.model.CollectionFilter
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaQuery
import com.geosnap.core.model.MediaStatus
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val db: GeoSnapDatabase,
    private val mediaDao: MediaDao,
    private val locationDao: LocationDao,
    private val time: TimeSource,
    private val dispatchers: DispatcherProvider,
) : MediaRepository {

    private data class Range(val start: Long?, val end: Long?)

    /** Date boundaries computed in the display timezone, not from stored strings (DATABASE.md). */
    private fun rangeFor(filter: CollectionFilter): Range {
        val zone = ZoneId.systemDefault()
        return when (filter) {
            CollectionFilter.TODAY -> {
                val startOfDay = LocalDate.now(time.clock()).atStartOfDay(zone).toInstant().toEpochMilli()
                Range(startOfDay, null)
            }
            CollectionFilter.THIS_WEEK -> {
                val weekStart = LocalDate.now(time.clock()).minusDays(6)
                    .atStartOfDay(zone).toInstant().toEpochMilli()
                Range(weekStart, null)
            }
            else -> Range(null, null)
        }
    }

    private fun kindFor(filter: CollectionFilter): String? = when (filter) {
        CollectionFilter.VIDEOS -> MediaKind.VIDEO.name
        CollectionFilter.PHOTOS -> MediaKind.PHOTO.name
        else -> null
    }

    private fun search(q: MediaQuery): String? = q.search?.trim()?.takeIf { it.isNotEmpty() }

    override fun observeMedia(query: MediaQuery): Flow<PagingData<MediaItem>> {
        val range = rangeFor(query.filter)
        return Pager(PagingConfig(pageSize = 60, enablePlaceholders = false, initialLoadSize = 120)) {
            mediaDao.pagingSource(kindFor(query.filter), range.start, range.end, search(query))
        }.flow.map { paging -> paging.map { it.toDomain() } }
    }

    override fun observeMediaList(query: MediaQuery): Flow<List<MediaItem>> {
        val range = rangeFor(query.filter)
        return mediaDao.observeAll(kindFor(query.filter), range.start, range.end, search(query))
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override fun observeMediaById(id: MediaId): Flow<MediaItem?> =
        mediaDao.observeById(id.value).map { it?.toDomain() }.flowOn(dispatchers.io)

    override fun observeLatestReady(): Flow<MediaItem?> =
        mediaDao.observeLatestReady().map { entity ->
            entity?.let { mediaDao.getById(it.id)?.toDomain() }
        }.flowOn(dispatchers.io)

    override suspend fun getById(id: MediaId): MediaItem? =
        withContext(dispatchers.io) { mediaDao.getById(id.value)?.toDomain() }

    override suspend fun getByIds(ids: List<MediaId>): List<MediaItem> =
        withContext(dispatchers.io) { mediaDao.getByIds(ids.map { it.value }).map { it.toDomain() } }

    override suspend fun finalizeCapture(item: MediaItem): Result<MediaItem> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                db.withTransaction {
                    item.location?.let { locationDao.upsert(it.toEntity()) }
                    mediaDao.upsert(item.toEntity())
                }
                item
            }
        }

    override suspend fun insertProcessing(item: MediaItem): Result<MediaItem> =
        finalizeCapture(item.copy(status = MediaStatus.PROCESSING))

    override suspend fun markVideoReady(
        id: MediaId,
        contentUri: String,
        thumbnailUri: String?,
        durationMs: Long?,
        sizeBytes: Long?,
    ): Result<Unit> = withContext(dispatchers.io) {
        geoSnapRunCatching {
            mediaDao.markReady(
                id = id.value,
                status = MediaStatus.READY.name,
                contentUri = contentUri,
                thumbnailUri = thumbnailUri,
                durationMs = durationMs,
                sizeBytes = sizeBytes,
                updatedAtMs = time.now().toEpochMilli(),
            )
        }
    }

    override suspend fun markFailed(id: MediaId, failureCode: String): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                mediaDao.markFailed(id.value, MediaStatus.FAILED.name, failureCode, time.now().toEpochMilli())
            }
        }

    override suspend fun updateStatus(id: MediaId, status: MediaStatus): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                mediaDao.updateStatus(id.value, status.name, time.now().toEpochMilli())
            }
        }

    override suspend fun delete(ids: Set<MediaId>): Result<Unit> = withContext(dispatchers.io) {
        geoSnapRunCatching {
            db.withTransaction {
                mediaDao.deleteByIds(ids.map { it.value })
                locationDao.deleteOrphans()
            }
        }
    }
}
