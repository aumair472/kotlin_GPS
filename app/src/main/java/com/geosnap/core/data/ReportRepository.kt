package com.geosnap.core.data

import com.geosnap.core.model.ExportId
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.Report
import com.geosnap.core.model.ReportDraft
import com.geosnap.core.model.ReportExport
import com.geosnap.core.model.ReportId
import com.geosnap.core.model.ReportQuery
import com.geosnap.core.model.ReportStatus
import com.geosnap.core.model.ReportSummary
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observeReports(query: ReportQuery): Flow<List<ReportSummary>>
    fun observeReport(id: ReportId): Flow<ReportDraft?>
    suspend fun getReport(id: ReportId): ReportDraft?

    /** Create a persistent empty draft immediately (auto-save model). */
    suspend fun createDraft(): ReportId

    /** Idempotent upsert of report fields; bumps updatedAt. */
    suspend fun saveReport(report: Report): Result<Unit>
    suspend fun updateLocation(id: ReportId, location: GeoSnapshot?): Result<Unit>
    suspend fun updateStatus(id: ReportId, status: ReportStatus): Result<Unit>

    suspend fun attachMedia(reportId: ReportId, mediaIds: List<MediaId>): Result<Unit>
    suspend fun detachMedia(reportId: ReportId, mediaId: MediaId): Result<Unit>
    suspend fun reorderMedia(reportId: ReportId, orderedMediaIds: List<MediaId>): Result<Unit>

    suspend fun delete(id: ReportId): Result<Unit>

    fun observeLatestExport(id: ReportId): Flow<ReportExport?>

    // Export lifecycle (PDF worker)
    suspend fun createExport(reportId: ReportId): ExportId
    suspend fun markExportRunning(exportId: ExportId)
    suspend fun completeExport(exportId: ExportId, outputUri: String, sizeBytes: Long, checksum: String?): Result<Unit>
    suspend fun failExport(exportId: ExportId, errorCode: String)
    suspend fun markExportShared(exportId: ExportId)
    suspend fun getExport(exportId: ExportId): ReportExport?
}
