package com.geosnap.feature.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapEmptyState
import com.geosnap.core.designsystem.component.GeoSnapMediaThumbnail
import com.geosnap.core.designsystem.component.GeoSnapPrimaryButton
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.model.MediaId
import com.geosnap.core.model.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerSheet(
    media: List<MediaItem>,
    onConfirm: (List<MediaId>) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(emptySet<MediaId>()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md).padding(bottom = Spacing.lg)) {
            Text(stringResource(R.string.report_attached_photos), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            if (media.isEmpty()) {
                GeoSnapEmptyState(message = stringResource(R.string.collection_empty), modifier = Modifier.heightIn(min = 160.dp))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    modifier = Modifier.heightIn(max = 360.dp).padding(vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(media, key = { it.id.value }) { item ->
                        GeoSnapMediaThumbnail(
                            item = item,
                            locationLabel = null,
                            selected = item.id in selected,
                            modifier = Modifier.clickable {
                                selected = if (item.id in selected) selected - item.id else selected + item.id
                            },
                        )
                    }
                }
            }
            GeoSnapPrimaryButton(
                text = stringResource(R.string.action_continue),
                onClick = { onConfirm(selected.toList()) },
                enabled = selected.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
