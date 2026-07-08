package com.geosnap.feature.collection

import androidx.compose.runtime.Composable

@Composable
fun CollectionRoute(onOpenMedia: (String) -> Unit) {
    CollectionScreen(onOpenMedia = onOpenMedia)
}
