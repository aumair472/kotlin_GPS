package com.geosnap.core.model

import java.time.Instant

data class Report(
    val id: ReportId,
    val title: String,
    val notes: String,
    val status: ReportStatus,
    val location: GeoSnapshot?,
    val reportInstant: Instant,
    val timezoneId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/** A report row plus a bounded preview projection for the reporting list. */
data class ReportSummary(
    val id: ReportId,
    val title: String,
    val status: ReportStatus,
    val locationLabel: String?,
    val reportInstant: Instant,
    val timezoneId: String,
    val photoCount: Int,
    val videoCount: Int,
    val previewThumbnailUris: List<String>,
)

/** An attachment of a media item to a report, with ordering and optional caption. */
data class ReportMediaRef(
    val mediaId: MediaId,
    val sortOrder: Int,
    val caption: String? = null,
    val included: Boolean = true,
)

/** Full editable report with attachments for the editor screen. */
data class ReportDraft(
    val report: Report,
    val attachments: List<ReportMediaRef>,
    val latestExport: ReportExport?,
)

data class ReportExport(
    val id: ExportId,
    val reportId: ReportId,
    val status: ExportStatus,
    val outputUri: String?,
    val mimeType: String,
    val sizeBytes: Long?,
    val checksumSha256: String?,
    val createdAt: Instant,
    val completedAt: Instant?,
    val sharedAt: Instant?,
    val errorCode: String?,
)
