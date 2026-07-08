package com.geosnap.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_items",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("captured_at_epoch_ms"),
        Index("kind"),
        Index("status"),
        Index("location_id"),
        Index("address_search_text"),
    ],
)
data class MediaEntity(
    @PrimaryKey val id: String,
    val kind: String,
    val status: String,
    @ColumnInfo(name = "content_uri") val contentUri: String?,
    @ColumnInfo(name = "source_uri") val sourceUri: String?,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "captured_at_epoch_ms") val capturedAtEpochMs: Long,
    @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "duration_ms") val durationMs: Long?,
    val width: Int?,
    val height: Int?,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long?,
    @ColumnInfo(name = "orientation_degrees") val orientationDegrees: Int,
    @ColumnInfo(name = "template_id") val templateId: String,
    @ColumnInfo(name = "template_version") val templateVersion: Int,
    @ColumnInfo(name = "rendered_stamp") val renderedStamp: String?,
    @ColumnInfo(name = "location_id") val locationId: String?,
    @ColumnInfo(name = "address_search_text") val addressSearchText: String?,
    @ColumnInfo(name = "thumbnail_uri") val thumbnailUri: String?,
    @ColumnInfo(name = "checksum_sha256") val checksumSha256: String?,
    @ColumnInfo(name = "created_at_epoch_ms") val createdAtEpochMs: Long,
    @ColumnInfo(name = "updated_at_epoch_ms") val updatedAtEpochMs: Long,
    @ColumnInfo(name = "failure_code") val failureCode: String?,
)

/** Room relation projection: a media row with its (optional) location, used for reads. */
data class MediaWithLocation(
    @androidx.room.Embedded val media: MediaEntity,
    @androidx.room.Relation(parentColumn = "location_id", entityColumn = "id")
    val location: LocationEntity?,
)
