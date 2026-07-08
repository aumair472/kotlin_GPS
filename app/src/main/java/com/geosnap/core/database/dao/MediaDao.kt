package com.geosnap.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.geosnap.core.database.entity.MediaEntity
import com.geosnap.core.database.entity.MediaWithLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(media: MediaEntity)

    @Update
    suspend fun update(media: MediaEntity)

    @Query("UPDATE media_items SET status = :status, updated_at_epoch_ms = :updatedAtMs WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAtMs: Long)

    @Query(
        "UPDATE media_items SET status = :status, content_uri = :contentUri, thumbnail_uri = :thumbnailUri, " +
            "duration_ms = :durationMs, size_bytes = :sizeBytes, updated_at_epoch_ms = :updatedAtMs WHERE id = :id",
    )
    suspend fun markReady(
        id: String,
        status: String,
        contentUri: String?,
        thumbnailUri: String?,
        durationMs: Long?,
        sizeBytes: Long?,
        updatedAtMs: Long,
    )

    @Query("UPDATE media_items SET status = :status, failure_code = :failureCode, updated_at_epoch_ms = :updatedAtMs WHERE id = :id")
    suspend fun markFailed(id: String, status: String, failureCode: String?, updatedAtMs: Long)

    @Transaction
    @Query("SELECT * FROM media_items WHERE id = :id")
    fun observeById(id: String): Flow<MediaWithLocation?>

    @Transaction
    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: String): MediaWithLocation?

    /**
     * Composable collection query. Null filters are no-ops. `kinds` is passed as a CSV check via the
     * `:filterKind` flag (0 = all). Date range is half-open [startMs, endMs).
     */
    @Transaction
    @Query(
        """
        SELECT * FROM media_items
        WHERE (:onlyKind IS NULL OR kind = :onlyKind)
          AND (:startMs IS NULL OR captured_at_epoch_ms >= :startMs)
          AND (:endMs IS NULL OR captured_at_epoch_ms < :endMs)
          AND (:search IS NULL OR address_search_text LIKE '%' || :search || '%')
          AND status != 'MISSING'
        ORDER BY captured_at_epoch_ms DESC
        """,
    )
    fun pagingSource(
        onlyKind: String?,
        startMs: Long?,
        endMs: Long?,
        search: String?,
    ): PagingSource<Int, MediaWithLocation>

    @Transaction
    @Query(
        """
        SELECT * FROM media_items
        WHERE (:onlyKind IS NULL OR kind = :onlyKind)
          AND (:startMs IS NULL OR captured_at_epoch_ms >= :startMs)
          AND (:endMs IS NULL OR captured_at_epoch_ms < :endMs)
          AND (:search IS NULL OR address_search_text LIKE '%' || :search || '%')
          AND status != 'MISSING'
        ORDER BY captured_at_epoch_ms DESC
        """,
    )
    fun observeAll(onlyKind: String?, startMs: Long?, endMs: Long?, search: String?): Flow<List<MediaWithLocation>>

    @Transaction
    @Query("SELECT * FROM media_items WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<MediaWithLocation>

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("SELECT * FROM media_items WHERE status = 'READY' ORDER BY captured_at_epoch_ms DESC LIMIT 1")
    fun observeLatestReady(): Flow<MediaEntity?>

    @Query("SELECT thumbnail_uri FROM media_items WHERE id = :id")
    suspend fun thumbnailUri(id: String): String?
}
