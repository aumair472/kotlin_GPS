package com.geosnap.feature.reporting

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapEmptyState
import com.geosnap.core.designsystem.component.GeoSnapFilterChipRow
import com.geosnap.core.designsystem.component.GeoSnapSearchField
import com.geosnap.core.designsystem.component.GeoSnapStatusBadge
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.model.ReportFilter
import com.geosnap.core.model.ReportStatus
import com.geosnap.core.model.ReportSummary
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportingScreen(
    onNewReport: (String) -> Unit,
    onOpenReport: (String) -> Unit,
    viewModel: ReportingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            GeoSnapTopBar(
                title = stringResource(R.string.reporting_title),
                actions = {
                    androidx.compose.material3.IconButton(onClick = { viewModel.createReport(onNewReport) }) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_report_title))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.createReport(onNewReport) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_report_title))
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            GeoSnapSearchField(
                value = state.search,
                onValueChange = viewModel::setSearch,
                placeholder = stringResource(R.string.reporting_search_hint),
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
            GeoSnapFilterChipRow(
                options = ReportFilter.entries,
                selected = state.filter,
                label = { reportFilterLabel(it) },
                onSelect = viewModel::setFilter,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
            if (state.isEmpty) {
                GeoSnapEmptyState(message = stringResource(R.string.reporting_empty))
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    item {
                        Text(
                            stringResource(R.string.reporting_recent),
                            style = MaterialTheme.typography.labelMedium,
                            color = GeoSnapPalette.NeutralGray,
                        )
                    }
                    items(state.reports, key = { it.id.value }) { report ->
                        ReportCard(report = report, onClick = { onOpenReport(report.id.value) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(report: ReportSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .border(1.dp, GeoSnapPalette.Divider, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    report.title.ifBlank { stringResource(R.string.new_report_title) },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(report.status)
            }
            report.locationLabel?.let {
                IconText(Icons.Filled.LocationOn, it)
            }
            IconText(Icons.Filled.CalendarMonth, formatInstant(report))
            if (report.previewThumbnailUris.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = Spacing.sm), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    report.previewThumbnailUris.take(3).forEach { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)),
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.report_media_counts, report.photoCount, report.videoCount),
                style = MaterialTheme.typography.bodySmall,
                color = GeoSnapPalette.NeutralGray,
                modifier = Modifier.padding(top = Spacing.sm),
            )
        }
    }
}

@Composable
private fun IconText(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = Spacing.xs)) {
        Icon(icon, contentDescription = null, tint = GeoSnapPalette.NeutralGray, modifier = Modifier.size(14.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = GeoSnapPalette.NeutralGray, modifier = Modifier.padding(start = Spacing.xs))
    }
}

@Composable
private fun StatusBadge(status: ReportStatus) {
    val (text, content, container) = when (status) {
        ReportStatus.DRAFT -> Triple(stringResource(R.string.report_status_draft), GeoSnapPalette.StatusDraft, GeoSnapPalette.StatusDraftContainer)
        ReportStatus.EXPORTED -> Triple(stringResource(R.string.report_status_exported), GeoSnapPalette.StatusExported, GeoSnapPalette.StatusExportedContainer)
        ReportStatus.SHARED -> Triple(stringResource(R.string.report_status_shared), GeoSnapPalette.StatusShared, GeoSnapPalette.StatusSharedContainer)
    }
    GeoSnapStatusBadge(text = text, contentColor = content, containerColor = container)
}

private fun formatInstant(report: ReportSummary): String =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withZone(ZoneId.of(report.timezoneId))
        .format(report.reportInstant)

@Composable
private fun reportFilterLabel(filter: ReportFilter): String = stringResource(
    when (filter) {
        ReportFilter.ALL -> R.string.filter_all_reports
        ReportFilter.DRAFT -> R.string.filter_draft
        ReportFilter.EXPORTED -> R.string.filter_exported
        ReportFilter.SHARED -> R.string.filter_shared
    },
)
