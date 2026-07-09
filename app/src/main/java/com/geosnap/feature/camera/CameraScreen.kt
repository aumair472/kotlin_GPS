package com.geosnap.feature.camera

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.geosnap.R
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.MonoDataFamily
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.media.CaptureMode
import com.geosnap.core.media.TimestampOverlayFormatter
import com.geosnap.core.model.LocationQuality
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onOpenSettings: () -> Unit,
    onOpenCollection: () -> Unit,
    onOpenMedia: (String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermission = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
    )
    val audioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val stampStrings = rememberStampStrings()
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    val zoom = remember { mutableFloatStateOf(0f) }
    val snackbarHostState = remember { SnackbarHostState() }
    val savedText = stringResource(R.string.capture_saved)
    val failedText = stringResource(R.string.capture_failed)
    val videoSavedText = stringResource(R.string.video_saved_success)
    val videoFailedText = stringResource(R.string.video_record_failed)

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }
    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted && !locationPermission.allPermissionsGranted) {
            locationPermission.launchMultiplePermissionRequest()
        }
    }
    LaunchedEffect(locationPermission.allPermissionsGranted, locationPermission.permissions.any { it.status.isGranted }) {
        viewModel.onLocationPermissionResult(locationPermission.permissions.any { it.status.isGranted })
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CameraEffect.CaptureSaved ->
                    snackbarHostState.showSnackbar(savedText, duration = SnackbarDuration.Short)
                is CameraEffect.CaptureFailed ->
                    snackbarHostState.showSnackbar(failedText, duration = SnackbarDuration.Short)
                is CameraEffect.VideoSaved ->
                    snackbarHostState.showSnackbar(videoSavedText, duration = SnackbarDuration.Short)
                is CameraEffect.VideoFailed ->
                    snackbarHostState.showSnackbar(videoFailedText, duration = SnackbarDuration.Short)
                is CameraEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.text, duration = SnackbarDuration.Short)
                is CameraEffect.LocationUnavailableForCapture -> Unit // already shown as "Location unavailable" stamp
            }
        }
    }

    if (!cameraPermission.status.isGranted) {
        CameraPermissionRationale(onGrant = { cameraPermission.launchPermissionRequest() }, onOpenSettings = onOpenSettings)
        return
    }

    val pv = previewView
    LaunchedEffect(pv, state.mode) { viewModel.bindCamera(lifecycleOwner, pv) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { pv.apply { scaleType = PreviewView.ScaleType.FILL_CENTER } },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoomChange, _ ->
                        zoom.floatValue = (zoom.floatValue + (zoomChange - 1f)).coerceIn(0f, 1f)
                        viewModel.setZoom(zoom.floatValue)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset -> viewModel.focusAt(pv, offset.x, offset.y) })
                },
        )

        CameraHeader(
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        val overlayNow = java.time.Instant.now()
        val baseLines = TimestampOverlayFormatter.build(
            style = state.template,
            capturedAt = overlayNow,
            zoneId = ZoneId.systemDefault(),
            location = state.latestFix?.toSnapshot(
                overlayNow,
                isApproximate = state.gpsQuality != LocationQuality.PRECISE,
                address = state.resolvedAddress,
            ),
            strings = stampStrings,
            locale = locale,
        )
        // Live-only transient: show "Resolving address…" for templates that want an address while it
        // is being reverse-geocoded (never persisted, never fabricated).
        val wantsAddress = com.geosnap.core.model.TemplateCatalog.spec(state.template).showAddress
        val overlayLines = if (
            wantsAddress && state.resolvedAddress == null &&
            state.addressStatus == AddressStatus.RESOLVING && state.latestFix != null
        ) {
            baseLines + stringResource(R.string.template_resolving_address)
        } else {
            baseLines
        }
        MetadataOverlay(
            lines = overlayLines,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp, start = Spacing.md, end = Spacing.md),
        )

        CameraControlPanel(
            state = state,
            onModeChange = viewModel::setMode,
            onToggleFlash = viewModel::toggleTorch,
            onSwitchLens = { viewModel.switchLens(lifecycleOwner, pv) },
            onCapture = {
                if (state.mode == CaptureMode.PHOTO) {
                    viewModel.onCapture(stampStrings, locale)
                } else {
                    // Request audio contextually at record start; recording proceeds silently if denied.
                    if (!state.isRecording && state.audioEnabled && !audioPermission.status.isGranted) {
                        audioPermission.launchPermissionRequest()
                    }
                    viewModel.onToggleRecording(stampStrings, locale)
                }
            },
            onOpenGallery = onOpenCollection,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (state.isRecording) {
            RecordingTimer(
                seconds = state.recordingSeconds,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 56.dp),
            )
        }

        state.countdown?.let { value ->
            CountdownOverlay(value = value, modifier = Modifier.align(Alignment.Center))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp),
        )
    }
}

private fun formatTimer(totalSeconds: Long): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
private fun RecordingTimer(seconds: Long, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(GeoSnapPalette.OverlayScrim)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GeoSnapPalette.Error))
        Text(
            text = formatTimer(seconds),
            color = Color.White,
            fontFamily = MonoDataFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = Spacing.sm),
        )
    }
}

@Composable
private fun CountdownOverlay(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
        )
    }
}

@Composable
private fun CameraPermissionRationale(onGrant: () -> Unit, onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoSnapPalette.Background)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = stringResource(R.string.camera_permission_rationale),
            style = MaterialTheme.typography.bodyLarge,
            color = GeoSnapPalette.OnSurface,
            modifier = Modifier.padding(vertical = Spacing.lg),
        )
        com.geosnap.core.designsystem.component.GeoSnapPrimaryButton(
            text = stringResource(R.string.action_continue),
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.action_open_settings),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = Spacing.md).clickable(onClick = onOpenSettings),
        )
    }
}

@Composable
private fun CameraHeader(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GeoSnapPalette.CameraHeader.copy(alpha = 0.85f))
            .statusBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.action_settings), tint = Color.White)
        }
    }
}

@Composable
private fun MetadataOverlay(lines: List<String>, modifier: Modifier = Modifier) {
    if (lines.isEmpty()) return
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GeoSnapPalette.OverlayScrim)
            .padding(Spacing.sm),
    ) {
        lines.forEach { line ->
            Text(text = line, color = Color.White, fontFamily = MonoDataFamily, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CameraControlPanel(
    state: CameraUiState,
    onModeChange: (CaptureMode) -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchLens: () -> Unit,
    onCapture: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Compact control panel: rounded white strip kept low-profile so the preview dominates and the
    // global bottom navigation sits directly below (HP-06).
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Color.White)
            .padding(vertical = Spacing.sm, horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            ModeTab(stringResource(R.string.camera_mode_photo), state.mode == CaptureMode.PHOTO) { onModeChange(CaptureMode.PHOTO) }
            ModeTab(stringResource(R.string.camera_mode_video), state.mode == CaptureMode.VIDEO) { onModeChange(CaptureMode.VIDEO) }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GeoSnapPalette.ChipBackground)
                    .clickable(onClick = onOpenGallery),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = stringResource(R.string.camera_open_gallery), tint = GeoSnapPalette.NeutralGray)
            }

            // Shutter: filled circle for photo / record; white stop-square while recording.
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(5.dp)
                    .clip(if (state.isRecording) RoundedCornerShape(6.dp) else CircleShape)
                    .background(if (state.mode == CaptureMode.VIDEO) GeoSnapPalette.Error else MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !state.capturing, onClick = onCapture),
                contentAlignment = Alignment.Center,
            ) {}

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.hasFlash) {
                    IconButton(onClick = onToggleFlash) {
                        Icon(
                            if (state.torchEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                            contentDescription = stringResource(R.string.camera_flash),
                            tint = GeoSnapPalette.NeutralGray,
                        )
                    }
                }
                if (state.canSwitchLens) {
                    IconButton(onClick = onSwitchLens) {
                        Icon(Icons.Filled.Cameraswitch, contentDescription = stringResource(R.string.camera_switch_lens), tint = GeoSnapPalette.NeutralGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) MaterialTheme.colorScheme.primary else GeoSnapPalette.NeutralGray,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}
