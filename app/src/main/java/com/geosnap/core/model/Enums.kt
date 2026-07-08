package com.geosnap.core.model

enum class MediaKind { PHOTO, VIDEO }

/** Lifecycle of a captured media row. PROCESSING is used while a video overlay job runs. */
enum class MediaStatus { PROCESSING, READY, FAILED, MISSING }

enum class ReportStatus { DRAFT, EXPORTED, SHARED }

enum class ExportStatus { QUEUED, RUNNING, READY, FAILED }

/** Confidence of a capture-time location fix, surfaced as the camera GPS badge. */
enum class LocationQuality { PRECISE, APPROXIMATE, UNAVAILABLE }

/** Built-in stamp styles (docs/REPORTING_EXPORT.md / templates screen). */
enum class TemplateStyle(val id: String, val version: Int) {
    MINIMAL("minimal", 1),
    CLASSIC("classic", 1),
    DETAILED("detailed", 1),
    REPORTER("reporter", 1);

    companion object {
        val DEFAULT = CLASSIC
        fun fromId(id: String?): TemplateStyle = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
