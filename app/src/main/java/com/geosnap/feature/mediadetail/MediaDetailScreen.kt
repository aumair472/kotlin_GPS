package com.geosnap.feature.mediadetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.geosnap.R
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.MonoDataFamily
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.location.CoordinateFormatter
import com.geosnap.core.media.GalleryResult
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailScreen(
    onBack: () -> Unit,
    viewModel: MediaDetailViewModel = hiltViewModel(),
) {
    val media by viewModel.media.collectAsStateWithLifecycle()
    val exporting by viewModel.exporting.collectAsStateWithLifecycle()
    val settingLocation by viewModel.settingLocation.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val msgPhotoSaved = stringResource(R.string.photo_saved_gallery)
    val msgVideoSaved = stringResource(R.string.video_saved_gallery)
    val msgAlready = stringResource(R.string.already_in_gallery)
    val msgFailed = stringResource(R.string.gallery_save_failed)
    val msgLocationUpdated = stringResource(R.string.location_updated)
    val msgLocationFailed = stringResource(R.string.location_update_failed)

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is MediaDetailEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(effect.contentUri))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    ContextCompat.startActivity(
                        context, Intent.createChooser(intent, context.getString(R.string.action_share)), null,
                    )
                }
                is MediaDetailEffect.GalleryResultMsg -> {
                    val text = when (effect.result) {
                        GalleryResult.Saved -> if (effect.isVideo) msgVideoSaved else msgPhotoSaved
                        GalleryResult.AlreadyAvailable -> msgAlready
                        GalleryResult.Failed -> msgFailed
                    }
                    snackbarHostState.showSnackbar(text)
                }
                is MediaDetailEffect.LocationResult -> {
                    snackbarHostState.showSnackbar(if (effect.success) msgLocationUpdated else msgLocationFailed)
                }
                MediaDetailEffect.Deleted -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            com.geosnap.core.designsystem.component.GeoSnapTopBar(
                title = stringResource(R.string.media_detail_title),
                onNavigateBack = onBack,
                navigationContentDescription = stringResource(R.string.action_back),
                actions = {
                    IconButton(onClick = viewModel::setLocation, enabled = !settingLocation) {
                        if (settingLocation) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Filled.AddLocationAlt, contentDescription = stringResource(R.string.action_set_location))
                        }
                    }
                    IconButton(onClick = viewModel::saveToGallery, enabled = !exporting) {
                        Icon(Icons.Filled.Download, contentDescription = stringResource(R.string.action_save_gallery))
                    }
                    IconButton(onClick = viewModel::share) {
                        Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.action_share))
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val item = media ?: return@Scaffold
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MediaPreview(item)
            HorizontalDivider(color = GeoSnapPalette.Divider)
            MetadataPanel(item)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_single)) },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; viewModel.delete() }) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
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
private fun MediaPreview(item: MediaItem) {
    when {
        item.kind == MediaKind.VIDEO && item.status == MediaStatus.READY && item.contentUri != null -> {
            VideoPlayer(
                uri = item.contentUri!!,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            )
        }
        item.kind == MediaKind.VIDEO -> {
            // PROCESSING / FAILED / missing source: never show a thumbnail as a fake player.
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        stringResource(R.string.video_processing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GeoSnapPalette.NeutralGray,
                        modifier = Modifier.padding(top = Spacing.sm),
                    )
                }
            }
        }
        else -> {
            AsyncImage(
                model = item.contentUri ?: item.thumbnailUri,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp).padding(Spacing.md),
            )
        }
    }
}

@Composable
private fun MetadataPanel(item: MediaItem) {
    val zone = ZoneId.of(item.timezoneId)
    val dateText = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withZone(zone).format(item.capturedAt)
    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(item.displayName, style = MaterialTheme.typography.titleMedium)
        DataRow(stringResource(R.string.report_field_date), dateText)
        item.location?.let { loc -> CoordinateRows(loc) }
            ?: DataRow(stringResource(R.string.report_field_location), stringResource(R.string.report_location_unavailable))
    }
}

@Composable
private fun CoordinateRows(loc: GeoSnapshot) {
    val latVal = "${CoordinateFormatter.latitudeMagnitude(loc.latitude)}° " +
        if (CoordinateFormatter.isNorth(loc.latitude)) "N" else "S"
    val lonVal = "${CoordinateFormatter.longitudeMagnitude(loc.longitude)}° " +
        if (CoordinateFormatter.isEast(loc.longitude)) "E" else "W"
    DataRow(stringResource(R.string.report_latitude), latVal)
    DataRow(stringResource(R.string.report_longitude), lonVal)
    loc.altitudeMeters?.let { DataRow(stringResource(R.string.report_altitude), "${it.toInt()} m") }
    loc.horizontalAccuracyMeters?.let { DataRow(stringResource(R.string.report_accuracy), "±${it.toInt()} m") }
    loc.address?.formatted?.let { DataRow(stringResource(R.string.report_field_location), it) }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GeoSnapPalette.NeutralGray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontFamily = MonoDataFamily)
    }
}
