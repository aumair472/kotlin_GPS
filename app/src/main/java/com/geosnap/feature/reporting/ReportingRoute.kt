package com.geosnap.feature.reporting

import androidx.compose.runtime.Composable

@Composable
fun ReportingRoute(
    onNewReport: (String) -> Unit,
    onOpenReport: (String) -> Unit,
) {
    ReportingScreen(onNewReport = onNewReport, onOpenReport = onOpenReport)
}
