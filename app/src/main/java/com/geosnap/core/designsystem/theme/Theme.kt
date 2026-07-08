package com.geosnap.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = GeoSnapPalette.PrimaryContainer,
    onPrimary = GeoSnapPalette.OnPrimary,
    primaryContainer = GeoSnapPalette.PrimaryContainer,
    onPrimaryContainer = GeoSnapPalette.OnPrimaryContainer,
    inversePrimary = GeoSnapPalette.InversePrimary,
    secondary = GeoSnapPalette.Secondary,
    onSecondary = GeoSnapPalette.OnSecondary,
    secondaryContainer = GeoSnapPalette.SecondaryContainer,
    onSecondaryContainer = GeoSnapPalette.OnSecondaryContainer,
    tertiary = GeoSnapPalette.Tertiary,
    onTertiary = GeoSnapPalette.OnTertiary,
    error = GeoSnapPalette.Error,
    onError = GeoSnapPalette.OnError,
    errorContainer = GeoSnapPalette.ErrorContainer,
    onErrorContainer = GeoSnapPalette.OnErrorContainer,
    background = GeoSnapPalette.Background,
    onBackground = GeoSnapPalette.OnBackground,
    surface = GeoSnapPalette.Surface,
    onSurface = GeoSnapPalette.OnSurface,
    surfaceVariant = GeoSnapPalette.SurfaceVariant,
    onSurfaceVariant = GeoSnapPalette.OnSurfaceVariant,
    surfaceContainer = GeoSnapPalette.SurfaceContainer,
    surfaceContainerHigh = GeoSnapPalette.SurfaceContainerHigh,
    outline = GeoSnapPalette.Outline,
    outlineVariant = GeoSnapPalette.OutlineVariant,
    inverseSurface = GeoSnapPalette.InverseSurface,
    inverseOnSurface = GeoSnapPalette.InverseOnSurface,
)

/**
 * Single source of truth for theming. The product is a light, document-style tool; we keep one
 * deterministic light scheme so screenshot diffs are stable across devices (no dynamic color).
 */
@Composable
fun GeoSnapTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()
            window.navigationBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = GeoSnapTypography,
        shapes = GeoSnapShapes,
        content = content,
    )
}
