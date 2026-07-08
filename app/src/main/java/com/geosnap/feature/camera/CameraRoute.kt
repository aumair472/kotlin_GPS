package com.geosnap.feature.camera

import androidx.compose.runtime.Composable

@Composable
fun CameraRoute(
    onOpenSettings: () -> Unit,
    onOpenCollection: () -> Unit,
    onOpenMedia: (String) -> Unit,
) {
    CameraScreen(
        onOpenSettings = onOpenSettings,
        onOpenCollection = onOpenCollection,
        onOpenMedia = onOpenMedia,
    )
}
