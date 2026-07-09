package com.geosnap.feature.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.MonoDataFamily
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.media.StampStrings
import com.geosnap.core.media.TimestampOverlayFormatter
import com.geosnap.core.model.GeoSnapshot
import com.geosnap.core.model.LocationId
import com.geosnap.core.model.PostalAddress
import com.geosnap.core.model.TemplateStyle
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@Composable
fun TemplatesScreen(viewModel: TemplatesViewModel = hiltViewModel()) {
    val selected by viewModel.selected.collectAsStateWithLifecycle()

    Scaffold(topBar = { GeoSnapTopBar(title = stringResource(R.string.templates_title)) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                stringResource(R.string.templates_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = GeoSnapPalette.NeutralGray,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(viewModel.styles, key = { it.id }) { style ->
                    TemplateCard(style = style, selected = style == selected, onSelect = { viewModel.select(style) })
                }
            }
        }
    }
}

private fun sampleStrings(brandLabel: String) = StampStrings(
    north = "N", south = "S", east = "E", west = "W",
    brandLabel = brandLabel, locationUnavailable = "Location unavailable",
    altitudeLabel = { "Alt: $it m" }, accuracyLabel = { "±$it m" },
)

private val sampleLocation = GeoSnapshot(
    id = LocationId("preview"), latitude = 24.8607, longitude = 67.0104,
    altitudeMeters = 342.0, horizontalAccuracyMeters = 3f, providerTimestamp = null,
    capturedAt = Instant.parse("2024-11-21T09:32:05Z"), timezoneId = "Asia/Karachi",
    isApproximate = false, isMock = false, provider = "fused",
    address = PostalAddress(formatted = "Karachi, Sindh, Pakistan"),
)

@Composable
private fun TemplateCard(style: TemplateStyle, selected: Boolean, onSelect: () -> Unit) {
    val titleRes = when (style) {
        TemplateStyle.MINIMAL -> R.string.template_minimal
        TemplateStyle.CLASSIC -> R.string.template_classic
        TemplateStyle.DETAILED -> R.string.template_detailed
        TemplateStyle.REPORTER -> R.string.template_reporter
    }
    val descRes = when (style) {
        TemplateStyle.MINIMAL -> R.string.template_minimal_desc
        TemplateStyle.CLASSIC -> R.string.template_classic_desc
        TemplateStyle.DETAILED -> R.string.template_detailed_desc
        TemplateStyle.REPORTER -> R.string.template_reporter_desc
    }
    val lines = TimestampOverlayFormatter.build(
        style, sampleLocation.capturedAt, ZoneId.of("Asia/Karachi"), sampleLocation,
        sampleStrings(stringResource(R.string.app_name)), Locale.getDefault(),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else GeoSnapPalette.Divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onSelect)
            .padding(Spacing.md),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium)
            if (selected) Icon(Icons.Filled.CheckCircle, contentDescription = stringResource(R.string.template_selected), tint = MaterialTheme.colorScheme.primary)
        }
        // Live preview band mirroring the camera overlay / final stamp.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.xs)
                .clip(RoundedCornerShape(6.dp))
                .background(GeoSnapPalette.OverlayScrim)
                .padding(Spacing.sm),
        ) {
            lines.forEach { line ->
                Text(line, color = Color.White, fontFamily = MonoDataFamily, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            stringResource(descRes),
            style = MaterialTheme.typography.bodySmall,
            color = GeoSnapPalette.NeutralGray,
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}
