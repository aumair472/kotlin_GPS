package com.geosnap.core.common

/**
 * Typed domain failures (docs/ARCHITECTURE.md error model). Converted to user-safe messages at the
 * UI boundary; technical detail is logged without coordinates/notes/filenames in release builds.
 */
sealed class GeoSnapError(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    data object PermissionDenied : GeoSnapError()
    data object CameraUnavailable : GeoSnapError()
    data class CaptureFailed(val reason: String? = null) : GeoSnapError(reason)
    data object LocationUnavailable : GeoSnapError()
    data object InsufficientStorage : GeoSnapError()
    data class MediaWriteFailed(val reason: String? = null) : GeoSnapError(reason)
    data class MetadataWriteFailed(val reason: String? = null) : GeoSnapError(reason)
    data class ExportFailed(val reason: String? = null) : GeoSnapError(reason)
    data object InvalidReport : GeoSnapError()
    data class FileShareFailed(val reason: String? = null) : GeoSnapError(reason)
    data class Unknown(val throwable: Throwable) : GeoSnapError(throwable.message, throwable)
}

/** Run [block], mapping any thrown exception into a [GeoSnapError] Result. */
inline fun <T> geoSnapRunCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: GeoSnapError) {
    Result.failure(e)
} catch (e: Throwable) {
    Result.failure(GeoSnapError.Unknown(e))
}
