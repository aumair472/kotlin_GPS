package com.geosnap.core.database.mapper

import com.geosnap.core.database.dao.ReportSummaryView
import com.geosnap.core.database.entity.LocationEntity
import com.geosnap.core.database.entity.MediaEntity
import com.geosnap.core.database.entity.MediaWithLocation
import com.geosnap.core.database.entity.ReportEntity
import com.geosnap.core.database.entity.ReportExportEntity
import com.geosnap.core.model.ExportId
import com.geosnap.core.model.ExportStatus
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationId
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import com.geosnap.core.model.PostalAddress
import com.geosnap.core.model.Report
import com.geosnap.core.model.ReportExport
import com.geosnap.core.model.ReportId
import com.geosnap.core.model.ReportStatus
import com.geosnap.core.model.ReportSummary
import java.time.Instant

fun LocationEntity.toDomain(): GeoSnapshot = GeoSnapshot(
    id = LocationId(id),
    latitude = latitude,
    longitude = longitude,
    altitudeMeters = altitudeM,
    horizontalAccuracyMeters = accuracyM,
    providerTimestamp = providerTimeMs?.let(Instant::ofEpochMilli),
    capturedAt = Instant.ofEpochMilli(observedAtMs),
    timezoneId = timezoneId,
    isApproximate = isApproximate,
    isMock = isMock,
    provider = provider,
    address = if (locality == null && adminArea == null && countryCode == null && formattedAddress == null) {
        null
    } else {
        PostalAddress(locality, adminArea, countryCode, formattedAddress)
    },
)

fun GeoSnapshot.toEntity(): LocationEntity = LocationEntity(
    id = id.value,
    latitude = latitude,
    longitude = longitude,
    altitudeM = altitudeMeters,
    accuracyM = horizontalAccuracyMeters,
    providerTimeMs = providerTimestamp?.toEpochMilli(),
    observedAtMs = capturedAt.toEpochMilli(),
    timezoneId = timezoneId,
    isApproximate = isApproximate,
    isMock = isMock,
    provider = provider,
    locality = address?.locality,
    adminArea = address?.adminArea,
    countryCode = address?.countryCode,
    formattedAddress = address?.formatted,
)

fun MediaWithLocation.toDomain(): MediaItem = MediaItem(
    id = MediaId(media.id),
    kind = MediaKind.valueOf(media.kind),
    status = MediaStatus.valueOf(media.status),
    contentUri = media.contentUri,
    sourceUri = media.sourceUri,
    displayName = media.displayName,
    mimeType = media.mimeType,
    capturedAt = Instant.ofEpochMilli(media.capturedAtEpochMs),
    timezoneId = media.timezoneId,
    durationMs = media.durationMs,
    width = media.width,
    height = media.height,
    sizeBytes = media.sizeBytes,
    orientationDegrees = media.orientationDegrees,
    templateId = media.templateId,
    templateVersion = media.templateVersion,
    renderedStamp = media.renderedStamp,
    location = location?.toDomain(),
    addressSearchText = media.addressSearchText,
    thumbnailUri = media.thumbnailUri,
    checksumSha256 = media.checksumSha256,
    failureCode = media.failureCode,
    createdAt = Instant.ofEpochMilli(media.createdAtEpochMs),
    updatedAt = Instant.ofEpochMilli(media.updatedAtEpochMs),
)

fun MediaItem.toEntity(): MediaEntity = MediaEntity(
    id = id.value,
    kind = kind.name,
    status = status.name,
    contentUri = contentUri,
    sourceUri = sourceUri,
    displayName = displayName,
    mimeType = mimeType,
    capturedAtEpochMs = capturedAt.toEpochMilli(),
    timezoneId = timezoneId,
    durationMs = durationMs,
    width = width,
    height = height,
    sizeBytes = sizeBytes,
    orientationDegrees = orientationDegrees,
    templateId = templateId,
    templateVersion = templateVersion,
    renderedStamp = renderedStamp,
    locationId = location?.id?.value,
    addressSearchText = addressSearchText,
    thumbnailUri = thumbnailUri,
    checksumSha256 = checksumSha256,
    createdAtEpochMs = createdAt.toEpochMilli(),
    updatedAtEpochMs = updatedAt.toEpochMilli(),
    failureCode = failureCode,
)

fun ReportEntity.toDomain(location: GeoSnapshot?): Report = Report(
    id = ReportId(id),
    title = title,
    notes = notes,
    status = ReportStatus.valueOf(status),
    location = location,
    reportInstant = Instant.ofEpochMilli(reportInstantMs),
    timezoneId = timezoneId,
    createdAt = Instant.ofEpochMilli(createdAtMs),
    updatedAt = Instant.ofEpochMilli(updatedAtMs),
)

fun Report.toEntity(): ReportEntity = ReportEntity(
    id = id.value,
    title = title,
    notes = notes,
    status = status.name,
    reportLocationId = location?.id?.value,
    reportInstantMs = reportInstant.toEpochMilli(),
    timezoneId = timezoneId,
    createdAtMs = createdAt.toEpochMilli(),
    updatedAtMs = updatedAt.toEpochMilli(),
)

fun ReportSummaryView.toDomain(previewThumbnails: List<String>): ReportSummary = ReportSummary(
    id = ReportId(id),
    title = title,
    status = ReportStatus.valueOf(status),
    locationLabel = locationLabel,
    reportInstant = Instant.ofEpochMilli(reportInstantMs),
    timezoneId = timezoneId,
    photoCount = photoCount,
    videoCount = videoCount,
    previewThumbnailUris = previewThumbnails,
)

fun ReportExportEntity.toDomain(): ReportExport = ReportExport(
    id = ExportId(id),
    reportId = ReportId(reportId),
    status = ExportStatus.valueOf(status),
    outputUri = outputUri,
    mimeType = mimeType,
    sizeBytes = sizeBytes,
    checksumSha256 = checksumSha256,
    createdAt = Instant.ofEpochMilli(createdAtMs),
    completedAt = completedAtMs?.let(Instant::ofEpochMilli),
    sharedAt = sharedAtMs?.let(Instant::ofEpochMilli),
    errorCode = errorCode,
)
