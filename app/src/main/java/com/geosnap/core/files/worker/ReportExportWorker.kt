package com.geosnap.core.files.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.geosnap.R
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.data.ReportRepository
import com.geosnap.core.files.CaptureWorkspace
import com.geosnap.core.files.FileSharer
import com.geosnap.core.files.PdfStrings
import com.geosnap.core.files.ReportPdfGenerator
import com.geosnap.core.model.ExportId
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.ReportId
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

/**
 * Durable PDF export (REPORTING_EXPORT.md). Generates the PDF off the UI thread, records the result
 * on the export row (QUEUED→RUNNING→READY/FAILED), and exposes it via a FileProvider content URI.
 */
@HiltWorker
class ReportExportWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val reportRepository: ReportRepository,
    private val mediaRepository: MediaRepository,
    private val pdfGenerator: ReportPdfGenerator,
    private val fileSharer: FileSharer,
    private val workspace: CaptureWorkspace,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_REPORT_ID = "report_id"
        const val KEY_EXPORT_ID = "export_id"
        const val MAX_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result {
        val reportId = inputData.getString(KEY_REPORT_ID)?.let(::ReportId) ?: return Result.failure()
        val exportId = inputData.getString(KEY_EXPORT_ID)?.let(::ExportId) ?: return Result.failure()

        return try {
            reportRepository.markExportRunning(exportId)
            val draft = reportRepository.getReport(reportId) ?: error("report missing")
            val attachments = mediaRepository.getByIds(draft.attachments.sortedBy { it.sortOrder }.map { MediaId(it.mediaId.value) })

            val output = File(workspace.exportDir(), "report_${reportId.value.take(8)}.pdf")
            pdfGenerator.generate(draft.report, attachments, output, strings())

            val uri = fileSharer.contentUri(output)
            val checksum = fileSharer.sha256(output)
            reportRepository.completeExport(exportId, uri.toString(), output.length(), checksum).getOrThrow()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount + 1 < MAX_ATTEMPTS) {
                Result.retry()
            } else {
                reportRepository.failExport(exportId, "PDF_EXPORT_FAILED")
                Result.failure()
            }
        }
    }

    private fun strings() = PdfStrings(
        titleFallback = appContext.getString(R.string.new_report_title),
        locationLabel = appContext.getString(R.string.report_field_location),
        dateLabel = appContext.getString(R.string.report_field_date),
        notesLabel = appContext.getString(R.string.report_field_notes),
        gpsLabel = appContext.getString(R.string.report_gps_data),
        latLabel = appContext.getString(R.string.report_latitude),
        lonLabel = appContext.getString(R.string.report_longitude),
        altLabel = appContext.getString(R.string.report_altitude),
        accuracyLabel = appContext.getString(R.string.report_accuracy),
        unavailable = appContext.getString(R.string.report_location_unavailable),
        photosHeading = appContext.getString(R.string.report_attached_photos),
        brand = appContext.getString(R.string.app_name),
    )
}
