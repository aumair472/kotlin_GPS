package com.geosnap.core.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

/** Produces FileProvider content URIs (never file://) for sharing exported reports (SECURITY.md). */
class FileSharer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val authority: String get() = "${context.packageName}.fileprovider"

    fun contentUri(file: File): Uri = FileProvider.getUriForFile(context, authority, file)

    fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read = input.read(buffer)
            while (read >= 0) {
                digest.update(buffer, 0, read)
                read = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
