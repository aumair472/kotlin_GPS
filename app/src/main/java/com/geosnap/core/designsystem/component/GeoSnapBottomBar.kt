package com.geosnap.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.geosnap.R
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.navigation.MainDestination

private data class BottomItem(
    val destination: MainDestination,
    val icon: ImageVector,
    val labelRes: Int,
)

private val items = listOf(
    BottomItem(MainDestination.CAMERA, Icons.Filled.PhotoCamera, R.string.nav_camera),
    BottomItem(MainDestination.COLLECTION, Icons.Outlined.GridView, R.string.nav_collection),
    BottomItem(MainDestination.REPORTING, Icons.Outlined.Assessment, R.string.nav_reporting),
    BottomItem(MainDestination.TEMPLATES, Icons.Outlined.Description, R.string.nav_templates),
)

/**
 * Four equal items in fixed order. Selected uses primary blue; labels always visible; white
 * background with a subtle top divider (docs/DESIGN_SYSTEM.md bottom navigation).
 */
@Composable
fun GeoSnapBottomBar(
    selected: MainDestination,
    onSelect: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = GeoSnapPalette.Divider)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
            items.forEach { item ->
                val label = stringResource(item.labelRes)
                NavigationBarItem(
                    selected = selected == item.destination,
                    onClick = { onSelect(item.destination) },
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = GeoSnapPalette.SecondaryContainer,
                        unselectedIconColor = GeoSnapPalette.Secondary,
                        unselectedTextColor = GeoSnapPalette.Secondary,
                    ),
                )
            }
        }
    }
}
