package com.geosnap.core.media

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.geosnap.core.common.GeoSnapError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Writes app-owned media to MediaStore via scoped storage (IS_PENDING), so no storage permission is
 * required. The pending flag is cleared only after the bytes are fully written (CAMERA_GPS_PIPELINE).
 */
class MediaStoreWriter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val resolver: ContentResolver get() = context.contentResolver

    private companion object {
        const val SUBDIR = "GPS Camera Timestamp Map"
    }

    /** Compress [bitmap] into a new pending image and return its content URI. */
    fun writeImage(bitmap: Bitmap, displayName: String, quality: Int = 92): Uri {
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$SUBDIR")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(collection, values)
            ?: throw GeoSnapError.MediaWriteFailed("insert returned null")
        try {
            resolver.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    throw GeoSnapError.MediaWriteFailed("compress failed")
                }
            } ?: throw GeoSnapError.MediaWriteFailed("openOutputStream null")
        } catch (e: Throwable) {
            runCatching { resolver.delete(uri, null, null) }
            throw e
        }
        return uri
    }

    fun markImageReady(uri: Uri) {
        val values = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
        resolver.update(uri, values, null, null)
    }

    /** Reserve a pending video URI to record into; caller writes bytes then [markVideoReady]. */
    fun createPendingVideo(displayName: String): Uri {
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/$SUBDIR")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        return resolver.insert(collection, values)
            ?: throw GeoSnapError.MediaWriteFailed("video insert returned null")
    }

    fun markVideoReady(uri: Uri) {
        val values = ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) }
        resolver.update(uri, values, null, null)
    }

    fun delete(uri: Uri) {
        runCatching { resolver.delete(uri, null, null) }
    }
}
