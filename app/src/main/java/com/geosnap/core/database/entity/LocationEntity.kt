package com.geosnap.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "altitude_m") val altitudeM: Double?,
    @ColumnInfo(name = "accuracy_m") val accuracyM: Float?,
    @ColumnInfo(name = "provider_time_ms") val providerTimeMs: Long?,
    @ColumnInfo(name = "observed_at_ms") val observedAtMs: Long,
    @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "is_approximate") val isApproximate: Boolean,
    @ColumnInfo(name = "is_mock") val isMock: Boolean,
    val provider: String?,
    val locality: String?,
    @ColumnInfo(name = "admin_area") val adminArea: String?,
    @ColumnInfo(name = "country_code") val countryCode: String?,
    @ColumnInfo(name = "formatted_address") val formattedAddress: String?,
)
