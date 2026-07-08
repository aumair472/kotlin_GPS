package com.geosnap.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["report_location_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("status"), Index("report_location_id"), Index("updated_at_ms")],
)
data class ReportEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    val status: String,
    @ColumnInfo(name = "report_location_id") val reportLocationId: String?,
    @ColumnInfo(name = "report_instant_ms") val reportInstantMs: Long,
    @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long,
)

@Entity(
    tableName = "report_media",
    primaryKeys = ["report_id", "media_id"],
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["report_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("report_id"), Index("media_id")],
)
data class ReportMediaEntity(
    @ColumnInfo(name = "report_id") val reportId: String,
    @ColumnInfo(name = "media_id") val mediaId: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    val caption: String?,
    val included: Boolean = true,
)

@Entity(
    tableName = "report_exports",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["report_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("report_id")],
)
data class ReportExportEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "report_id") val reportId: String,
    val status: String,
    @ColumnInfo(name = "output_uri") val outputUri: String?,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long?,
    @ColumnInfo(name = "checksum_sha256") val checksumSha256: String?,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long,
    @ColumnInfo(name = "completed_at_ms") val completedAtMs: Long?,
    @ColumnInfo(name = "shared_at_ms") val sharedAtMs: Long?,
    @ColumnInfo(name = "error_code") val errorCode: String?,
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val version: Int,
    val category: String,
    @ColumnInfo(name = "config_json") val configJson: String,
    @ColumnInfo(name = "is_built_in") val isBuiltIn: Boolean,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
)
