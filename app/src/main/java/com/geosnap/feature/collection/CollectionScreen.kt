package com.geosnap.feature.collection

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geosnap.R
import com.geosnap.core.common.DayLabel
import com.geosnap.core.designsystem.component.GeoSnapEmptyState
import com.geosnap.core.designsystem.component.GeoSnapFilterChipRow
import com.geosnap.core.designsystem.component.GeoSnapMediaThumbnail
import com.geosnap.core.designsystem.component.GeoSnapSearchField
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.model.CollectionFilter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionScreen(
    onOpenMedia: (String) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CollectionEffect.ShareMedia -> {
                    val uris = ArrayList(effect.contentUris.map(Uri::parse))
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "*/*"
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    ContextCompat.startActivity(
                        context,
                        Intent.createChooser(intent, context.getString(R.string.action_share)),
                        null,
                    )
                }
                CollectionEffect.Deleted -> Unit
            }
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            if (state.selectionMode) {
                GeoSnapTopBar(
                    title = stringResource(R.string.selection_count, state.selectedIds.size),
                    onNavigateBack = viewModel::clearSelection,
                    navigationContentDescription = stringResource(R.string.action_close),
                    actions = {
                        IconButton(onClick = viewModel::shareSelected) {
                            Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.action_share))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    },
                )
            } else {
                GeoSnapTopBar(title = stringResource(R.string.collection_title))
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            GeoSnapSearchField(
                value = state.search,
                onValueChange = viewModel::setSearch,
                placeholder = stringResource(R.string.collection_search_hint),
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
            GeoSnapFilterChipRow(
                options = CollectionFilter.entries,
                selected = state.filter,
                label = { filterLabel(it) },
                onSelect = viewModel::setFilter,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            if (state.isEmpty) {
                GeoSnapEmptyState(message = stringResource(R.string.collection_empty))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    contentPadding = PaddingValues(Spacing.md),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
                ) {
                    state.groups.forEach { group ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = groupLabel(group.label),
                                style = MaterialTheme.typography.labelMedium,
                                color = GeoSnapPalette.NeutralGray,
                                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                            )
                        }
                        items(group.items, key = { it.id.value }) { item ->
                            GeoSnapMediaThumbnail(
                                item = item,
                                locationLabel = item.location?.address?.locality,
                                selected = item.id in state.selectedIds,
                                modifier = Modifier.combinedClickable(
                                    onClick = {
                                        if (state.selectionMode) viewModel.toggleSelection(item.id)
                                        else onOpenMedia(item.id.value)
                                    },
                                    onLongClick = { viewModel.toggleSelection(item.id) },
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_attached)) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.deleteSelected() }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun filterLabel(filter: CollectionFilter): String = stringResource(
    when (filter) {
        CollectionFilter.ALL -> R.string.filter_all
        CollectionFilter.TODAY -> R.string.filter_today
        CollectionFilter.THIS_WEEK -> R.string.filter_this_week
        CollectionFilter.VIDEOS -> R.string.filter_videos
        CollectionFilter.PHOTOS -> R.string.filter_photos
    },
)

@Composable
private fun groupLabel(label: DayLabel): String = when (label) {
    DayLabel.Today -> stringResource(R.string.collection_group_today)
    DayLabel.Yesterday -> stringResource(R.string.collection_group_yesterday)
    is DayLabel.Date -> label.text
}
