package com.geosnap.feature.mediadetail

import androidx.compose.runtime.Composable

@Composable
fun MediaDetailRoute(mediaId: String, onBack: () -> Unit) {
    // mediaId is read from SavedStateHandle by the ViewModel.
    MediaDetailScreen(onBack = onBack)
}
