package com.geosnap.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.model.MediaItem
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.MediaStatus

/** Square media tile with video/GPS badges and processing/failed states. Never loads full-res. */
@Composable
fun GeoSnapMediaThumbnail(
    item: MediaItem,
    locationLabel: String?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(GeoSnapPalette.SurfaceContainer),
    ) {
        when (item.status) {
            MediaStatus.READY -> {
                AsyncImage(
                    model = item.thumbnailUri ?: item.contentUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            MediaStatus.PROCESSING -> CenterContent { CircularProgressIndicator(strokeWidth = 2.dp) }
            MediaStatus.FAILED, MediaStatus.MISSING -> CenterContent {
                Icon(Icons.Filled.BrokenImage, contentDescription = null, tint = GeoSnapPalette.NeutralGray)
            }
        }

        if (item.kind == MediaKind.VIDEO && item.status == MediaStatus.READY) {
            Icon(
                Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(28.dp),
            )
        }

        if (locationLabel != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x99000000))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            ) {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Text(
                        text = locationLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }
            }
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.CenterContent(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().align(Alignment.Center), contentAlignment = Alignment.Center) { content() }
}
