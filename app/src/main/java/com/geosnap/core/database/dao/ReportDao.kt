package com.geosnap.core.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geosnap.core.database.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

/** Bounded list projection for the reporting screen (counts via correlated subqueries). */
data class ReportSummaryView(
    val id: String,
    val title: String,
    val status: String,
    @ColumnInfo(name = "location_label") val locationLabel: String?,
    @ColumnInfo(name = "report_instant_ms") val reportInstantMs: Long,
    @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "photo_count") val photoCount: Int,
    @ColumnInfo(name = "video_count") val videoCount: Int,
)

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: ReportEntity)

    @Update
    suspend fun update(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE id = :id")
    fun observeById(id: String): Flow<ReportEntity?>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getById(id: String): ReportEntity?

    @Query("UPDATE reports SET status = :status, updated_at_ms = :updatedAtMs WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAtMs: Long)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun delete(id: String)

    @Query(
        """
        SELECT r.id AS id, r.title AS title, r.status AS status,
            COALESCE(l.formatted_address, l.locality) AS location_label,
            r.report_instant_ms AS report_instant_ms, r.timezone_id AS timezone_id,
            (SELECT COUNT(*) FROM report_media rm JOIN media_items m ON m.id = rm.media_id
                WHERE rm.report_id = r.id AND m.kind = 'PHOTO') AS photo_count,
            (SELECT COUNT(*) FROM report_media rm JOIN media_items m ON m.id = rm.media_id
                WHERE rm.report_id = r.id AND m.kind = 'VIDEO') AS video_count
        FROM reports r
        LEFT JOIN locations l ON l.id = r.report_location_id
        WHERE (:status IS NULL OR r.status = :status)
          AND (:search IS NULL OR r.title LIKE '%' || :search || '%'
               OR COALESCE(l.formatted_address, '') LIKE '%' || :search || '%')
        ORDER BY r.updated_at_ms DESC
        """,
    )
    fun observeSummaries(status: String?, search: String?): Flow<List<ReportSummaryView>>
}
