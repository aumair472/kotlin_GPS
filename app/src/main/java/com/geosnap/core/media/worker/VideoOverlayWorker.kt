package com.geosnap.core.media.worker

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.media3.common.MediaItem as Media3MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.TextureOverlay
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.files.CaptureWorkspace
import com.geosnap.core.media.MediaStoreWriter
import com.geosnap.core.media.PhotoStamper
import com.geosnap.core.media.VideoThumbnailExtractor
import com.geosnap.core.model.MediaId
import com.google.common.collect.ImmutableList
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Durable video finalization (CAMERA_GPS_PIPELINE strategy B): overlays the stamp onto a clean
 * recorded source with Media3 Transformer, publishes the result to MediaStore, and flips the media
 * row PROCESSING → READY (or FAILED). Idempotent: re-running re-derives output from the source.
 */
@UnstableApi
@HiltWorker
class VideoOverlayWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val stamper: PhotoStamper,
    private val mediaStoreWriter: MediaStoreWriter,
    private val mediaRepository: MediaRepository,
    private val thumbnailExtractor: VideoThumbnailExtractor,
    private val workspace: CaptureWorkspace,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_MEDIA_ID = "media_id"
        const val KEY_SOURCE_PATH = "source_path"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_STAMP_LINES = "stamp_lines"
        const val STAMP_SEPARATOR = "\n"
        const val MAX_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result {
        val mediaIdValue = inputData.getString(KEY_MEDIA_ID) ?: return Result.failure()
        val sourcePath = inputData.getString(KEY_SOURCE_PATH) ?: return failPermanently(mediaIdValue)
        val displayName = inputData.getString(KEY_DISPLAY_NAME) ?: "GPSCameraTimestampMap_video.mp4"
        val lines = inputData.getString(KEY_STAMP_LINES)?.split(STAMP_SEPARATOR)?.filter { it.isNotBlank() }.orEmpty()
        val source = File(sourcePath)
        if (!source.exists()) return failPermanently(mediaIdValue)

        val outputFile = File(workspace.videoSourceDir(), "out_${mediaIdValue}.mp4")
        return try {
            transform(source, outputFile, lines)
            val pendingUri = mediaStoreWriter.createPendingVideo(displayName)
            appContext.contentResolver.openOutputStream(pendingUri)?.use { out ->
                outputFile.inputStream().use { it.copyTo(out) }
            } ?: error("openOutputStream null")
            mediaStoreWriter.markVideoReady(pendingUri)

            val meta = thumbnailExtractor.extract(pendingUri, mediaIdValue)
            mediaRepository.markVideoReady(
                id = MediaId(mediaIdValue),
                contentUri = pendingUri.toString(),
                thumbnailUri = meta.thumbnailUri,
                durationMs = meta.durationMs,
                sizeBytes = outputFile.length(),
            ).getOrThrow()

            source.delete()
            outputFile.delete()
            Result.success()
        } catch (e: Exception) {
            outputFile.delete()
            if (runAttemptCount + 1 < MAX_ATTEMPTS) Result.retry() else failPermanently(mediaIdValue)
        }
    }

    private suspend fun failPermanently(mediaId: String): Result {
        mediaRepository.markFailed(MediaId(mediaId), "VIDEO_PROCESSING_FAILED")
        return Result.failure()
    }

    private suspend fun transform(source: File, output: File, lines: List<String>) =
        withContext(Dispatchers.Main) {
            val overlays: ImmutableList<TextureOverlay> = if (lines.isEmpty()) {
                ImmutableList.of()
            } else {
                ImmutableList.of(BitmapOverlay.createStaticBitmapOverlay(buildOverlayBitmap(source, lines)))
            }
            val effects = Effects(emptyList(), listOf(OverlayEffect(overlays)))
            val edited = EditedMediaItem.Builder(Media3MediaItem.fromUri(source.toUri()))
                .setEffects(effects)
                .build()

            suspendCancellableCoroutine { cont ->
                val transformer = Transformer.Builder(appContext)
                    .addListener(object : Transformer.Listener {
                        override fun onCompleted(composition: androidx.media3.transformer.Composition, result: ExportResult) {
                            if (cont.isActive) cont.resume(Unit)
                        }
                        override fun onError(
                            composition: androidx.media3.transformer.Composition,
                            result: ExportResult,
                            exception: ExportException,
                        ) {
                            if (cont.isActive) cont.cancel(exception)
                        }
                    })
                    .build()
                transformer.start(edited, output.absolutePath)
                cont.invokeOnCancellation { transformer.cancel() }
            }
        }

    /** Full-frame transparent overlay with the stamp burned bottom-left (reuses [PhotoStamper]). */
    private fun buildOverlayBitmap(source: File, lines: List<String>): Bitmap {
        val retriever = MediaMetadataRetriever()
        val (w, h) = try {
            retriever.setDataSource(source.absolutePath)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 1080
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 1920
            width to height
        } finally {
            runCatching { retriever.release() }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        stamper.stamp(bitmap, lines)
        return bitmap
    }
}
