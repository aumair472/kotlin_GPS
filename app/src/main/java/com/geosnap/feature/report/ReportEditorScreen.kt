package com.geosnap.feature.report

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapPrimaryButton
import com.geosnap.core.designsystem.component.GeoSnapStatusBadge
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.MonoDataFamily
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.location.CoordinateFormatter
import com.geosnap.core.model.ExportStatus
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportEditorScreen(
    onBack: () -> Unit,
    viewModel: ReportEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingSaveAsUri by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val createDocLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { dest ->
        val src = pendingSaveAsUri
        if (dest != null && src != null) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(src))?.use { input ->
                    context.contentResolver.openOutputStream(dest)?.use { output -> input.copyTo(output) }
                }
            }
        }
        pendingSaveAsUri = null
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ReportEditorEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(effect.contentUri))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    ContextCompat.startActivity(context, Intent.createChooser(intent, context.getString(R.string.action_share)), null)
                }
                is ReportEditorEffect.SaveAs -> {
                    pendingSaveAsUri = effect.contentUri
                    createDocLauncher.launch("GPSCameraTimestampMap_Report.pdf")
                }
                ReportEditorEffect.ExportFailed -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.report_export_failed))
                }
                ReportEditorEffect.ExportReady -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.report_export_success))
                }
                ReportEditorEffect.Deleted -> onBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GeoSnapTopBar(
                title = stringResource(R.string.new_report_title),
                onNavigateBack = onBack,
                navigationContentDescription = stringResource(R.string.action_back),
                actions = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.report_save_draft)) }
                },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(Spacing.md)) {
                GeoSnapPrimaryButton(
                    text = stringResource(R.string.report_submit),
                    onClick = viewModel::exportPdf,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    stringResource(R.string.report_submit_caption),
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoSnapPalette.NeutralGray,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FieldLabel(stringResource(R.string.report_field_title))
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = { Text(stringResource(R.string.report_field_title_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            FieldLabel(stringResource(R.string.report_field_location))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    state.locationLabel ?: stringResource(R.string.report_location_unavailable),
                    modifier = Modifier.weight(1f).padding(start = Spacing.sm),
                    style = MaterialTheme.typography.bodyMedium,
                )
                IconButton(onClick = viewModel::refreshLocation) {
                    if (state.gpsLive) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    else Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.report_refresh_location), tint = MaterialTheme.colorScheme.primary)
                }
            }

            FieldLabel(stringResource(R.string.report_field_date))
            Text(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                    .withZone(ZoneId.of(state.timezoneId)).format(state.reportInstant),
                fontFamily = MonoDataFamily,
                style = MaterialTheme.typography.bodyMedium,
            )

            FieldLabel(stringResource(R.string.report_field_notes))
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                placeholder = { Text(stringResource(R.string.report_field_notes_hint)) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
            )

            AttachedPhotos(state, viewModel)

            GpsCard(state)

            ExportRow(state, viewModel, onRequestDelete = { showDeleteConfirm = true })
        }
    }

    if (state.showPicker) {
        MediaPickerSheet(
            media = state.pickerMedia,
            onConfirm = viewModel::attach,
            onDismiss = viewModel::closePicker,
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.report_delete_confirm_title)) },
            text = { Text(stringResource(R.string.report_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; viewModel.deleteReport() }) {
                    Text(stringResource(R.string.report_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun AttachedPhotos(state: ReportEditorUiState, viewModel: ReportEditorViewModel) {
    FieldLabel(stringResource(R.string.report_attached_photos))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        item {
            Box(
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .clickable(onClick = viewModel::openPicker),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.report_attached_photos), tint = MaterialTheme.colorScheme.primary) }
        }
        items(state.attachments, key = { it.id.value }) { item ->
            AsyncImage(
                model = item.thumbnailUri ?: item.contentUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = { viewModel.detach(item.id) }),
            )
        }
    }
}

@Composable
private fun GpsCard(state: ReportEditorUiState) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSnapPalette.SurfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.report_gps_data), style = MaterialTheme.typography.titleMedium)
                if (state.latitude != null) {
                    GeoSnapStatusBadge(stringResource(R.string.report_gps_live), GeoSnapPalette.GpsLive, Color(0x2210B981))
                }
            }
            HorizontalDivider(color = GeoSnapPalette.Divider, modifier = Modifier.padding(vertical = Spacing.sm))
            if (state.latitude != null && state.longitude != null) {
                GpsRow(stringResource(R.string.report_latitude), "${CoordinateFormatter.latitudeMagnitude(state.latitude)}° ${if (state.latitude >= 0) "N" else "S"}")
                GpsRow(stringResource(R.string.report_longitude), "${CoordinateFormatter.longitudeMagnitude(state.longitude)}° ${if (state.longitude >= 0) "E" else "W"}")
                state.altitude?.let { GpsRow(stringResource(R.string.report_altitude), "${it.toInt()} m") }
                state.accuracy?.let { GpsRow(stringResource(R.string.report_accuracy), "±${it.toInt()} m") }
            } else {
                Text(stringResource(R.string.report_location_unavailable), color = GeoSnapPalette.NeutralGray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun GpsRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GeoSnapPalette.NeutralGray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontFamily = MonoDataFamily)
    }
}

@Composable
private fun ExportRow(state: ReportEditorUiState, viewModel: ReportEditorViewModel, onRequestDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        TextButton(onClick = viewModel::saveAs, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.report_save_as))
        }
        TextButton(onClick = viewModel::share, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.action_share))
        }
        TextButton(onClick = onRequestDelete, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.report_delete), color = MaterialTheme.colorScheme.error)
        }
    }
    when (state.export?.status) {
        ExportStatus.QUEUED, ExportStatus.RUNNING ->
            Text(stringResource(R.string.report_export_in_progress), style = MaterialTheme.typography.bodySmall, color = GeoSnapPalette.NeutralGray)
        ExportStatus.FAILED ->
            Text(stringResource(R.string.report_export_failed), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        else -> Unit
    }
}
