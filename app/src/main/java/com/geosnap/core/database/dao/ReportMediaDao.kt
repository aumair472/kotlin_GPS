package com.geosnap.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geosnap.core.database.entity.ReportMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(refs: List<ReportMediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ref: ReportMediaEntity)

    @Query("SELECT * FROM report_media WHERE report_id = :reportId ORDER BY sort_order ASC")
    fun observeForReport(reportId: String): Flow<List<ReportMediaEntity>>

    @Query("SELECT * FROM report_media WHERE report_id = :reportId ORDER BY sort_order ASC")
    suspend fun getForReport(reportId: String): List<ReportMediaEntity>

    @Query("DELETE FROM report_media WHERE report_id = :reportId AND media_id = :mediaId")
    suspend fun remove(reportId: String, mediaId: String)

    @Query("DELETE FROM report_media WHERE report_id = :reportId")
    suspend fun clear(reportId: String)

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM report_media WHERE report_id = :reportId")
    suspend fun maxSortOrder(reportId: String): Int

    /** Up to [limit] preview thumbnail URIs for a report, in attachment order. */
    @Query(
        """
        SELECT m.thumbnail_uri FROM report_media rm
        JOIN media_items m ON m.id = rm.media_id
        WHERE rm.report_id = :reportId AND m.thumbnail_uri IS NOT NULL
        ORDER BY rm.sort_order ASC LIMIT :limit
        """,
    )
    suspend fun previewThumbnails(reportId: String, limit: Int): List<String>

    @Query("SELECT report_id FROM report_media WHERE media_id IN (:mediaIds)")
    suspend fun reportsContaining(mediaIds: List<String>): List<String>
}
