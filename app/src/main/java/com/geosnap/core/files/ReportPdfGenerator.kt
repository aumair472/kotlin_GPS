package com.geosnap.core.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.geosnap.core.location.CoordinateFormatter
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.Report
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject
import kotlin.math.min

/** Localized labels for the PDF, resolved by the caller (worker). */
data class PdfStrings(
    val titleFallback: String,
    val locationLabel: String,
    val dateLabel: String,
    val notesLabel: String,
    val gpsLabel: String,
    val latLabel: String,
    val lonLabel: String,
    val altLabel: String,
    val accuracyLabel: String,
    val unavailable: String,
    val photosHeading: String,
    val brand: String,
)

/**
 * Renders a multi-page A4 PDF for a report with metadata + attached photos using PdfDocument.
 * Coordinate values are never localized; surrounding labels come from [PdfStrings].
 */
class ReportPdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        const val PAGE_W = 595
        const val PAGE_H = 842
        const val MARGIN = 40f
        const val THUMB_MAX = 480
    }

    fun generate(report: Report, attachments: List<MediaItem>, output: File, strings: PdfStrings): File {
        val doc = PdfDocument()
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#111827"); textSize = 22f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#6B7280"); textSize = 11f }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#111827"); textSize = 13f }
        val monoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#111827"); textSize = 12f; typeface = Typeface.MONOSPACE
        }
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2563EB"); textSize = 12f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
        var canvas = page.canvas
        var y = MARGIN

        fun newPage() {
            doc.finishPage(page)
            pageNumber++
            page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
            canvas = page.canvas
            y = MARGIN
        }
        fun ensure(space: Float) { if (y + space > PAGE_H - MARGIN) newPage() }

        canvas.drawText(strings.brand, MARGIN, y, brandPaint); y += 22f
        canvas.drawText(report.title.ifBlank { strings.titleFallback }, MARGIN, y, titlePaint); y += 28f

        val zone = ZoneId.of(report.timezoneId)
        val dateText = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withZone(zone).withLocale(Locale.getDefault()).format(report.reportInstant)
        y = drawLabeled(canvas, strings.dateLabel, dateText, labelPaint, bodyPaint, y);

        val loc = report.location
        if (loc != null) {
            loc.address?.formatted?.let { y = drawLabeled(canvas, strings.locationLabel, it, labelPaint, bodyPaint, y) }
            y += 4f
            canvas.drawText(strings.gpsLabel, MARGIN, y, labelPaint); y += 16f
            val lat = "${CoordinateFormatter.latitudeMagnitude(loc.latitude)}° ${if (loc.latitude >= 0) "N" else "S"}"
            val lon = "${CoordinateFormatter.longitudeMagnitude(loc.longitude)}° ${if (loc.longitude >= 0) "E" else "W"}"
            canvas.drawText("${strings.latLabel}: $lat   ${strings.lonLabel}: $lon", MARGIN, y, monoPaint); y += 16f
            loc.altitudeMeters?.let { canvas.drawText("${strings.altLabel}: ${it.toInt()} m", MARGIN, y, monoPaint); y += 16f }
            loc.horizontalAccuracyMeters?.let { canvas.drawText("${strings.accuracyLabel}: ±${it.toInt()} m", MARGIN, y, monoPaint); y += 16f }
        } else {
            y = drawLabeled(canvas, strings.locationLabel, strings.unavailable, labelPaint, bodyPaint, y)
        }

        if (report.notes.isNotBlank()) {
            y += 8f
            canvas.drawText(strings.notesLabel, MARGIN, y, labelPaint); y += 16f
            y = drawWrapped(canvas, report.notes, bodyPaint, y) { ensure(it) }
        }

        if (attachments.isNotEmpty()) {
            y += 12f; ensure(20f)
            canvas.drawText(strings.photosHeading, MARGIN, y, labelPaint); y += 18f
            attachments.forEach { item ->
                // Try the thumbnail first, then fall back to the full-res source so a stale/unreadable
                // thumbnail URI doesn't silently drop the photo from the report.
                val bmp = loadBitmap(item.thumbnailUri) ?: loadBitmap(item.contentUri)
                if (bmp != null) {
                    val ratio = bmp.height.toFloat() / bmp.width
                    val drawW = PAGE_W - 2 * MARGIN
                    val drawH = drawW * ratio
                    ensure(drawH + 8f)
                    val dst = android.graphics.RectF(MARGIN, y, MARGIN + drawW, y + drawH)
                    canvas.drawBitmap(bmp, null, dst, null)
                    y += drawH + 10f
                    bmp.recycle()
                }
            }
        }

        doc.finishPage(page)
        FileOutputStream(output).use { doc.writeTo(it) }
        doc.close()
        return output
    }

    private fun drawLabeled(canvas: Canvas, label: String, value: String, labelPaint: Paint, bodyPaint: Paint, startY: Float): Float {
        var y = startY
        canvas.drawText(label, MARGIN, y, labelPaint); y += 16f
        canvas.drawText(value, MARGIN, y, bodyPaint); y += 20f
        return y
    }

    private inline fun drawWrapped(canvas: Canvas, text: String, paint: Paint, startY: Float, ensure: (Float) -> Unit): Float {
        var y = startY
        val maxWidth = PAGE_W - 2 * MARGIN
        text.split("\n").forEach { paragraph ->
            val words = paragraph.split(" ")
            var line = StringBuilder()
            for (word in words) {
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(candidate) > maxWidth && line.isNotEmpty()) {
                    canvas.drawText(line.toString(), MARGIN, y, paint); y += 16f
                    line = StringBuilder(word)
                } else line = StringBuilder(candidate)
            }
            if (line.isNotEmpty()) { canvas.drawText(line.toString(), MARGIN, y, paint); y += 16f }
        }
        return y
    }

    private fun loadBitmap(uriString: String?): Bitmap? {
        val uri = uriString?.let(Uri::parse) ?: return null
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bytes = input.readBytes()
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                var sample = 1
                while (min(bounds.outWidth, bounds.outHeight) / sample > THUMB_MAX) sample *= 2
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
            }
        }.getOrNull()
    }
}
