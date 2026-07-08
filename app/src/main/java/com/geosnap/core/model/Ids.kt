package com.geosnap.core.model

import java.util.UUID

@JvmInline
value class MediaId(val value: String) {
    companion object { fun random() = MediaId(UUID.randomUUID().toString()) }
}

@JvmInline
value class LocationId(val value: String) {
    companion object { fun random() = LocationId(UUID.randomUUID().toString()) }
}

@JvmInline
value class ReportId(val value: String) {
    companion object { fun random() = ReportId(UUID.randomUUID().toString()) }
}

@JvmInline
value class ExportId(val value: String) {
    companion object { fun random() = ExportId(UUID.randomUUID().toString()) }
}
