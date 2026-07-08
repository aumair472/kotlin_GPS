package com.geosnap.feature.report

import androidx.compose.runtime.Composable

@Composable
fun ReportEditorRoute(
    reportId: String,
    onBack: () -> Unit,
    onPickMedia: () -> Unit,
) {
    // reportId is read from SavedStateHandle by the ViewModel; media picking is in-screen.
    ReportEditorScreen(onBack = onBack)
}
