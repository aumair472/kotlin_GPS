package com.geosnap.feature.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.geosnap.R
import com.geosnap.core.media.StampStrings

/** Resolves localized [StampStrings] from resources so the pure stamp builder stays Android-free. */
@Composable
fun rememberStampStrings(): StampStrings {
    val north = "N"
    val south = "S"
    val east = "E"
    val west = "W"
    val brand = stringResource(R.string.app_name)
    val unavailable = stringResource(R.string.camera_gps_unavailable)
    val altLabel = stringResource(R.string.value_meters, "%s")
    val accLabel = stringResource(R.string.value_accuracy, "%s")
    return StampStrings(
        north = north, south = south, east = east, west = west,
        brandLabel = brand,
        locationUnavailable = unavailable,
        altitudeLabel = { v -> altLabel.replace("%s", v) },
        accuracyLabel = { v -> accLabel.replace("%s", v) },
    )
}
