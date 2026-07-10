package com.geosnap.core.data

import androidx.room.withTransaction
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.TimeSource
import com.geosnap.core.common.geoSnapRunCatching
import com.geosnap.core.database.GeoSnapDatabase
import com.geosnap.core.database.dao.LocationDao
import com.geosnap.core.database.dao.ReportDao
import com.geosnap.core.database.dao.ReportExportDao
import com.geosnap.core.database.dao.ReportMediaDao
import com.geosnap.core.database.entity.ReportMediaEntity
import com.geosnap.core.database.mapper.toDomain
import com.geosnap.core.database.mapper.toEntity
import com.geosnap.core.database.entity.ReportExportEntity
import com.geosnap.core.model.ExportId
import com.geosnap.core.model.ExportStatus
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.Report
import com.geosnap.core.model.ReportDraft
import com.geosnap.core.model.ReportExport
import com.geosnap.core.model.ReportFilter
import com.geosnap.core.model.ReportId
import com.geosnap.core.model.ReportMediaRef
import com.geosnap.core.model.ReportQuery
import com.geosnap.core.model.ReportStatus
import com.geosnap.core.model.ReportSummary
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val db: GeoSnapDatabase,
    private val reportDao: ReportDao,
    private val reportMediaDao: ReportMediaDao,
    private val reportExportDao: ReportExportDao,
    private val locationDao: LocationDao,
    private val time: TimeSource,
    private val dispatchers: DispatcherProvider,
) : ReportRepository {

    private companion object {
        const val PREVIEW_LIMIT = 3
    }

    private fun statusFilter(filter: ReportFilter): String? = when (filter) {
        ReportFilter.ALL -> null
        ReportFilter.DRAFT -> ReportStatus.DRAFT.name
        ReportFilter.EXPORTED -> ReportStatus.EXPORTED.name
        ReportFilter.SHARED -> ReportStatus.SHARED.name
    }

    override fun observeReports(query: ReportQuery): Flow<List<ReportSummary>> =
        reportDao.observeSummaries(statusFilter(query.filter), query.search?.trim()?.takeIf { it.isNotEmpty() })
            .map { views ->
                views.map { view ->
                    view.toDomain(reportMediaDao.previewThumbnails(view.id, PREVIEW_LIMIT))
                }
            }.flowOn(dispatchers.io)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeReport(id: ReportId): Flow<ReportDraft?> =
        reportDao.observeById(id.value).flatMapLatest { entity ->
            if (entity == null) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                combine(
                    reportMediaDao.observeForReport(id.value),
                    reportExportDao.observeLatest(id.value),
                ) { refs, export ->
                    val location = entity.reportLocationId?.let { locationDao.getById(it)?.toDomain() }
                    ReportDraft(
                        report = entity.toDomain(location),
                        attachments = refs.map { ReportMediaRef(MediaId(it.mediaId), it.sortOrder, it.caption, it.included) },
                        latestExport = export?.toDomain(),
                    )
                }
            }
        }.flowOn(dispatchers.io)

    override suspend fun getReport(id: ReportId): ReportDraft? = withContext(dispatchers.io) {
        val entity = reportDao.getById(id.value) ?: return@withContext null
        val location = entity.reportLocationId?.let { locationDao.getById(it)?.toDomain() }
        val refs = reportMediaDao.getForReport(id.value)
        ReportDraft(
            report = entity.toDomain(location),
            attachments = refs.map { ReportMediaRef(MediaId(it.mediaId), it.sortOrder, it.caption, it.included) },
            latestExport = reportExportDao.latest(id.value)?.toDomain(),
        )
    }

    override suspend fun createDraft(): ReportId = withContext(dispatchers.io) {
        val now = time.now()
        val report = Report(
            id = ReportId.random(),
            title = "",
            notes = "",
            status = ReportStatus.DRAFT,
            location = null,
            reportInstant = now,
            timezoneId = ZoneId.systemDefault().id,
            createdAt = now,
            updatedAt = now,
        )
        reportDao.upsert(report.toEntity())
        report.id
    }

    override suspend fun saveReport(report: Report): Result<Unit> = withContext(dispatchers.io) {
        geoSnapRunCatching {
            db.withTransaction {
                report.location?.let { locationDao.upsert(it.toEntity()) }
                // Plain UPDATE — never INSERT OR REPLACE an existing report: REPLACE deletes the row
                // first, which cascade-deletes report_media and report_exports.
                reportDao.update(report.copy(updatedAt = time.now()).toEntity())
            }
        }
    }

    override suspend fun updateLocation(id: ReportId, location: GeoSnapshot?): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                val entity = reportDao.getById(id.value) ?: throw IllegalStateException("report missing")
                db.withTransaction {
                    location?.let { locationDao.upsert(it.toEntity()) }
                    // UPDATE, not REPLACE-upsert — see saveReport.
                    reportDao.update(
                        entity.copy(
                            reportLocationId = location?.id?.value,
                            updatedAtMs = time.now().toEpochMilli(),
                        ),
                    )
                }
            }
        }

    override suspend fun updateStatus(id: ReportId, status: ReportStatus): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching { reportDao.updateStatus(id.value, status.name, time.now().toEpochMilli()) }
        }

    override suspend fun attachMedia(reportId: ReportId, mediaIds: List<MediaId>): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                db.withTransaction {
                    var order = reportMediaDao.maxSortOrder(reportId.value)
                    val rows = mediaIds.map { mediaId ->
                        ReportMediaEntity(reportId.value, mediaId.value, ++order, null, true)
                    }
                    reportMediaDao.upsertAll(rows)
                    reportDao.updateStatus(reportId.value, ReportStatus.DRAFT.name, time.now().toEpochMilli())
                }
            }
        }

    override suspend fun detachMedia(reportId: ReportId, mediaId: MediaId): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching { reportMediaDao.remove(reportId.value, mediaId.value) }
        }

    override suspend fun reorderMedia(reportId: ReportId, orderedMediaIds: List<MediaId>): Result<Unit> =
        withContext(dispatchers.io) {
            geoSnapRunCatching {
                db.withTransaction {
                    val rows = orderedMediaIds.mapIndexed { index, mediaId ->
                        ReportMediaEntity(reportId.value, mediaId.value, index, null, true)
                    }
                    reportMediaDao.upsertAll(rows)
                }
            }
        }

    override suspend fun delete(id: ReportId): Result<Unit> = withContext(dispatchers.io) {
        geoSnapRunCatching {
            db.withTransaction {
                reportDao.delete(id.value)
                locationDao.deleteOrphans()
            }
        }
    }

    override fun observeLatestExport(id: ReportId): Flow<ReportExport?> =
        reportExportDao.observeLatest(id.value).map { it?.toDomain() }.flowOn(dispatchers.io)

    override suspend fun createExport(reportId: ReportId): ExportId = withContext(dispatchers.io) {
        val id = ExportId.random()
        reportExportDao.upsert(
            ReportExportEntity(
                id = id.value, reportId = reportId.value, status = ExportStatus.QUEUED.name,
                outputUri = null, mimeType = "application/pdf", sizeBytes = null, checksumSha256 = null,
                createdAtMs = time.now().toEpochMilli(), completedAtMs = null, sharedAtMs = null, errorCode = null,
            ),
        )
        id
    }

    override suspend fun markExportRunning(exportId: ExportId) = withContext(dispatchers.io) {
        reportExportDao.updateStatus(exportId.value, ExportStatus.RUNNING.name)
    }

    override suspend fun completeExport(
        exportId: ExportId,
        outputUri: String,
        sizeBytes: Long,
        checksum: String?,
    ): Result<Unit> = withContext(dispatchers.io) {
        geoSnapRunCatching {
            val export = reportExportDao.getById(exportId.value) ?: throw IllegalStateException("export missing")
            db.withTransaction {
                reportExportDao.updateResult(
                    id = exportId.value, status = ExportStatus.READY.name, outputUri = outputUri,
                    sizeBytes = sizeBytes, checksum = checksum, completedAtMs = time.now().toEpochMilli(), errorCode = null,
                )
                reportDao.updateStatus(export.reportId, ReportStatus.EXPORTED.name, time.now().toEpochMilli())
            }
        }
    }

    override suspend fun failExport(exportId: ExportId, errorCode: String) = withContext(dispatchers.io) {
        reportExportDao.updateResult(
            id = exportId.value, status = ExportStatus.FAILED.name, outputUri = null,
            sizeBytes = null, checksum = null, completedAtMs = time.now().toEpochMilli(), errorCode = errorCode,
        )
    }

    override suspend fun markExportShared(exportId: ExportId) = withContext(dispatchers.io) {
        val export = reportExportDao.getById(exportId.value) ?: return@withContext
        reportExportDao.markShared(exportId.value, time.now().toEpochMilli())
        reportDao.updateStatus(export.reportId, ReportStatus.SHARED.name, time.now().toEpochMilli())
    }

    override suspend fun getExport(exportId: ExportId): ReportExport? = withContext(dispatchers.io) {
        reportExportDao.getById(exportId.value)?.toDomain()
    }
}
