package com.geosnap.core.media

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.geosnap.core.common.GeoSnapError
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class CaptureMode { PHOTO, VIDEO }

/**
 * Owns CameraX use-case binding for the camera feature lifecycle (not the ViewModel — feature
 * lifecycle, per ARCHITECTURE). The composable supplies only the surface and gestures.
 */
@Singleton
class CameraController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    val hasFlashUnit: Boolean get() = camera?.cameraInfo?.hasFlashUnit() == true

    suspend fun hasFrontAndBack(): Boolean {
        val provider = cameraProvider ?: ProcessCameraProvider.getInstance(context).await().also { cameraProvider = it }
        return provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
            provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    }

    suspend fun bind(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        mode: CaptureMode,
        torchEnabled: Boolean,
    ): Result<Unit> = runCatching {
        val provider = ProcessCameraProvider.getInstance(context).await()
        cameraProvider = provider

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        provider.unbindAll()
        camera = when (mode) {
            CaptureMode.PHOTO -> {
                val capture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture = capture
                videoCapture = null
                provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
            }
            CaptureMode.VIDEO -> {
                val recorder = Recorder.Builder().build()
                val capture = VideoCapture.withOutput(recorder)
                videoCapture = capture
                imageCapture = null
                provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
            }
        }
        camera?.cameraControl?.enableTorch(torchEnabled && hasFlashUnit)
        Unit
    }.onFailure { Log.w("CameraController", "bind failed: ${it.message}") }

    fun videoCapture(): VideoCapture<Recorder>? = videoCapture

    private var recording: Recording? = null

    /** Records a clean source to [file]; audio only if [audioEnabled] and the mic permission is held. */
    @SuppressLint("MissingPermission")
    fun startRecording(file: File, audioEnabled: Boolean, onEvent: (VideoRecordEvent) -> Unit): Boolean {
        val capture = videoCapture ?: return false
        val options = FileOutputOptions.Builder(file).build()
        val pending = capture.output.prepareRecording(context, options).apply {
            if (audioEnabled && hasAudioPermission()) withAudioEnabled()
        }
        recording = pending.start(ContextCompat.getMainExecutor(context)) { event -> onEvent(event) }
        return true
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private fun hasAudioPermission(): Boolean =
        androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    suspend fun takePhoto(outputDir: File): File = suspendCancellableCoroutine { cont ->
        val capture = imageCapture ?: run {
            cont.resumeWithException(GeoSnapError.CameraUnavailable)
            return@suspendCancellableCoroutine
        }
        val file = File(outputDir, "capture_${System.currentTimeMillis()}.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    if (cont.isActive) cont.resume(file)
                }
                override fun onError(exception: ImageCaptureException) {
                    if (cont.isActive) cont.resumeWithException(GeoSnapError.CaptureFailed(exception.message))
                }
            },
        )
    }

    fun setTorch(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(enabled && hasFlashUnit)
    }

    fun setLinearZoom(value: Float) {
        camera?.cameraControl?.setLinearZoom(value.coerceIn(0f, 1f))
    }

    fun focusAt(factory: MeteringPointFactory, x: Float, y: Float) {
        val point = factory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point).build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    fun toggleLens(): Int {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        return lensFacing
    }

    fun currentLens(): Int = lensFacing

    fun unbind() {
        cameraProvider?.unbindAll()
        camera = null
        imageCapture = null
        videoCapture = null
    }
}
