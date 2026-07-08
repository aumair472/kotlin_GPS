package com.geosnap.core.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

/** Result of a Save-to-Gallery request (FINAL-03). */
sealed interface GalleryResult {
    data object Saved : GalleryResult
    data object AlreadyAvailable : GalleryResult
    data object Failed : GalleryResult
}

/**
 * Copies app media into the public gallery via MediaStore (scoped storage, no storage permission).
 * App captures are already written to public Pictures/Movies, so those report [AlreadyAvailable];
 * any non-public source is copied with IS_PENDING and the original MIME preserved.
 */
class GalleryExporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val resolver get() = context.contentResolver

    fun export(item: MediaItem): GalleryResult {
        val source = item.contentUri?.let(Uri::parse) ?: return GalleryResult.Failed
        // Already a public MediaStore item → visible in gallery already.
        if (source.authority == MediaStore.AUTHORITY) return GalleryResult.AlreadyAvailable

        return try {
            val isVideo = item.kind == MediaKind.VIDEO
            val collection = if (isVideo) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            val dir = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueName(item))
                put(MediaStore.MediaColumns.MIME_TYPE, item.mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "$dir/GeoSnap")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            val target = resolver.insert(collection, values) ?: return GalleryResult.Failed
            try {
                resolver.openOutputStream(target)?.use { out ->
                    resolver.openInputStream(source)?.use { it.copyTo(out) } ?: error("source open failed")
                } ?: error("target open failed")
            } catch (e: Throwable) {
                runCatching { resolver.delete(target, null, null) }
                return GalleryResult.Failed
            }
            resolver.update(target, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
            GalleryResult.Saved
        } catch (e: Exception) {
            GalleryResult.Failed
        }
    }

    private fun uniqueName(item: MediaItem): String {
        val ext = if (item.kind == MediaKind.VIDEO) "mp4" else "jpg"
        val base = item.displayName.substringBeforeLast('.', item.displayName)
        return "${base}_${System.currentTimeMillis().toString(36).uppercase(Locale.US)}.$ext"
    }
}
