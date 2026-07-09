package com.geosnap.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geosnap.R
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.MonoDataFamily
import com.geosnap.core.designsystem.theme.Spacing
import kotlinx.coroutines.delay

private const val MIN_SPLASH_MS = 900L

/**
 * Branded splash (docs reference 01_splash). Resolves the startup destination, enforces a minimum
 * display time so it is not a jarring flash, then hands off to [onResolved] which clears the
 * back stack.
 */
@Composable
fun SplashScreen(
    onResolved: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val startRoute by viewModel.startRoute.collectAsStateWithLifecycle()

    LaunchedEffect(startRoute) {
        val route = startRoute ?: return@LaunchedEffect
        delay(MIN_SPLASH_MS)
        onResolved(route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoSnapPalette.Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.gps_icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = GeoSnapPalette.PrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.85f).padding(top = Spacing.md),
            )
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = GeoSnapPalette.NeutralGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50)),
                color = GeoSnapPalette.PrimaryContainer,
                trackColor = GeoSnapPalette.Divider,
            )
            Text(
                text = "v1.0.0",
                fontFamily = MonoDataFamily,
                fontSize = 11.sp,
                color = GeoSnapPalette.NeutralGray,
                modifier = Modifier.padding(top = Spacing.sm),
            )
        }
    }
}
