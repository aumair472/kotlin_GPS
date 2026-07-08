package com.geosnap.core.media

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import com.geosnap.core.files.CaptureWorkspace
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/** Extracts a representative frame + duration/size for a finalized video. */
class VideoThumbnailExtractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workspace: CaptureWorkspace,
) {
    data class VideoMeta(val thumbnailUri: String?, val durationMs: Long?, val width: Int?, val height: Int?)

    fun extract(videoUri: Uri, mediaId: String): VideoMeta {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            val frame = retriever.getFrameAtTime(0)
            val thumbUri = frame?.let { saveThumb(it, mediaId) }
            VideoMeta(thumbUri, duration, width, height)
        } catch (e: Exception) {
            VideoMeta(null, null, null, null)
        } finally {
            runCatching { retriever.release() }
        }
    }

    private fun saveThumb(bitmap: Bitmap, mediaId: String): String {
        val dir = File(context.cacheDir, "thumbnails").apply { mkdirs() }
        val file = File(dir, "thumb_$mediaId.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it) }
        return file.toUri().toString()
    }
}
