package com.geosnap.core.media

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.geosnap.core.model.GeoSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Writes supported EXIF datetime + GPS tags into a finalized JPEG (AndroidX ExifInterface). Written
 * AFTER recompression so tags survive (CAMERA_GPS_PIPELINE). Never invents coordinates.
 */
class ExifWriter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val exifDate = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss", Locale.US)

    fun write(uri: Uri, capturedAt: Instant, zoneId: ZoneId, location: GeoSnapshot?) {
        context.contentResolver.openFileDescriptor(uri, "rw")?.use { pfd ->
            val exif = ExifInterface(pfd.fileDescriptor)
            val local = capturedAt.atZone(zoneId)
            val stamp = exifDate.format(local)
            exif.setAttribute(ExifInterface.TAG_DATETIME, stamp)
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, stamp)
            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, stamp)
            val offset = local.offset.id // e.g. +05:00
            exif.setAttribute(ExifInterface.TAG_OFFSET_TIME_ORIGINAL, offset)
            exif.setAttribute(ExifInterface.TAG_SOFTWARE, "GPS Camera: Timestamp & Map")

            if (location != null) {
                exif.setLatLong(location.latitude, location.longitude)
                location.altitudeMeters?.let { exif.setAltitude(it) }
                exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP,
                    DateTimeFormatter.ofPattern("yyyy:MM:dd", Locale.US).format(local))
                location.horizontalAccuracyMeters?.let {
                    exif.setAttribute(ExifInterface.TAG_GPS_DOP, it.toString())
                }
            }
            exif.saveAttributes()
        }
    }
}
