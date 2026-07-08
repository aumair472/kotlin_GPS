package com.geosnap.core.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.common.GeoSnapError
import com.geosnap.core.common.TimeSource
import com.geosnap.core.common.geoSnapRunCatching
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import com.geosnap.core.model.TemplateStyle
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.withContext

/** Everything the finalizer needs that isn't an injected collaborator. */
data class FinalizePhotoRequest(
    val tempFile: File,
    val capturedAt: Instant,
    val zoneId: ZoneId,
    val style: TemplateStyle,
    val location: GeoSnapshot?,
    val strings: StampStrings,
    val locale: Locale,
)

/**
 * Recompress-then-tag photo pipeline (CAMERA_GPS_PIPELINE). Runs off the main thread; deletes the
 * temp input on success or failure.
 */
class PhotoFinalizer @Inject constructor(
    private val stamper: PhotoStamper,
    private val mediaStoreWriter: MediaStoreWriter,
    private val exifWriter: ExifWriter,
    private val mediaRepository: MediaRepository,
    private val time: TimeSource,
    private val dispatchers: DispatcherProvider,
) {
    private companion object {
        const val MAX_DIMENSION = 4096
        val NAME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.US)
    }

    suspend fun finalize(request: FinalizePhotoRequest): Result<MediaItem> =
        withContext(dispatchers.default) {
            geoSnapRunCatching {
                val temp = request.tempFile
                try {
                    val orientation = readOrientation(temp)
                    val decoded = decodeBounded(temp)
                        ?: throw GeoSnapError.CaptureFailed("decode failed")
                    val normalized = applyOrientation(decoded, orientation)

                    val lines = TimestampOverlayFormatter.build(
                        style = request.style,
                        capturedAt = request.capturedAt,
                        zoneId = request.zoneId,
                        location = request.location,
                        strings = request.strings,
                        locale = request.locale,
                    )
                    stamper.stamp(normalized, lines)

                    val shortId = MediaId.random()
                    val displayName = "GPSCameraTimestampMap_${NAME_FORMAT.format(request.capturedAt.atZone(request.zoneId))}_" +
                        "${shortId.value.take(6)}.jpg"
                    val uri = mediaStoreWriter.writeImage(normalized, displayName)
                    exifWriter.write(uri, request.capturedAt, request.zoneId, request.location)
                    mediaStoreWriter.markImageReady(uri)

                    val now = time.now()
                    val item = MediaItem(
                        id = shortId,
                        kind = MediaKind.PHOTO,
                        status = MediaStatus.READY,
                        contentUri = uri.toString(),
                        sourceUri = null,
                        displayName = displayName,
                        mimeType = "image/jpeg",
                        capturedAt = request.capturedAt,
                        timezoneId = request.zoneId.id,
                        durationMs = null,
                        width = normalized.width,
                        height = normalized.height,
                        sizeBytes = null,
                        orientationDegrees = 0,
                        templateId = request.style.id,
                        templateVersion = request.style.version,
                        renderedStamp = TimestampOverlayFormatter.renderedStamp(lines),
                        location = request.location,
                        addressSearchText = buildSearchText(request.location),
                        thumbnailUri = uri.toString(),
                        checksumSha256 = null,
                        failureCode = null,
                        createdAt = now,
                        updatedAt = now,
                    )
                    if (normalized != decoded) decoded.recycle()
                    mediaRepository.finalizeCapture(item).getOrThrow()
                } finally {
                    runCatching { temp.delete() }
                }
            }
        }

    private fun buildSearchText(location: GeoSnapshot?): String? {
        val a = location?.address ?: return null
        return listOfNotNull(a.locality, a.adminArea, a.countryCode, a.formatted)
            .joinToString(" ").lowercase(Locale.getDefault()).ifBlank { null }
    }

    private fun readOrientation(file: File): Int =
        ExifInterface(file.absolutePath).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL,
        )

    private fun decodeBounded(file: File): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        var sample = 1
        val largest = maxOf(bounds.outWidth, bounds.outHeight)
        while (largest / sample > MAX_DIMENSION) sample *= 2
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inMutable = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated.copy(Bitmap.Config.ARGB_8888, true).also { if (it != rotated) rotated.recycle() }
    }
}
