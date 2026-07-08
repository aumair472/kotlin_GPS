package com.geosnap.core.data

import androidx.paging.PagingData
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaQuery
import com.geosnap.core.model.MediaStatus
import kotlinx.coroutines.flow.Flow

/**
 * Coordinates Room media/location persistence. Domain models in, domain models out; entities never
 * leak to callers (docs/DATABASE.md).
 */
interface MediaRepository {
    fun observeMedia(query: MediaQuery): Flow<PagingData<MediaItem>>
    fun observeMediaList(query: MediaQuery): Flow<List<MediaItem>>
    fun observeMediaById(id: MediaId): Flow<MediaItem?>
    fun observeLatestReady(): Flow<MediaItem?>
    suspend fun getById(id: MediaId): MediaItem?
    suspend fun getByIds(ids: List<MediaId>): List<MediaItem>

    /** Persist a finalized capture (media + optional location) in one transaction. */
    suspend fun finalizeCapture(item: MediaItem): Result<MediaItem>

    /** Insert a PROCESSING row for a video whose overlay job is pending. */
    suspend fun insertProcessing(item: MediaItem): Result<MediaItem>

    suspend fun markVideoReady(id: MediaId, contentUri: String, thumbnailUri: String?, durationMs: Long?, sizeBytes: Long?): Result<Unit>
    suspend fun markFailed(id: MediaId, failureCode: String): Result<Unit>
    suspend fun updateStatus(id: MediaId, status: MediaStatus): Result<Unit>

    suspend fun delete(ids: Set<MediaId>): Result<Unit>
}
