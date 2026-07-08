package com.geosnap.core.files

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Private scratch space for pre-finalization capture temp files (kept out of ViewModels). */
@Singleton
class CaptureWorkspace @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun captureDir(): File = File(context.cacheDir, "captures").apply { mkdirs() }
    fun exportDir(): File = File(context.cacheDir, "exports").apply { mkdirs() }
    fun videoSourceDir(): File = File(context.filesDir, "video_src").apply { mkdirs() }
}
