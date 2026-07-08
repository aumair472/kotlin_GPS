package com.geosnap.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geosnap.core.database.entity.ReportExportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportExportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(export: ReportExportEntity)

    @Query("SELECT * FROM report_exports WHERE id = :id")
    suspend fun getById(id: String): ReportExportEntity?

    @Query("SELECT * FROM report_exports WHERE report_id = :reportId ORDER BY created_at_ms DESC LIMIT 1")
    fun observeLatest(reportId: String): Flow<ReportExportEntity?>

    @Query("SELECT * FROM report_exports WHERE report_id = :reportId ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun latest(reportId: String): ReportExportEntity?

    @Query(
        "UPDATE report_exports SET status = :status, output_uri = :outputUri, size_bytes = :sizeBytes, " +
            "checksum_sha256 = :checksum, completed_at_ms = :completedAtMs, error_code = :errorCode WHERE id = :id",
    )
    suspend fun updateResult(
        id: String,
        status: String,
        outputUri: String?,
        sizeBytes: Long?,
        checksum: String?,
        completedAtMs: Long?,
        errorCode: String?,
    )

    @Query("UPDATE report_exports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE report_exports SET shared_at_ms = :sharedAtMs WHERE id = :id")
    suspend fun markShared(id: String, sharedAtMs: Long)
}
