package com.geosnap.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapPrimaryButton
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val body: Int,
    @DrawableRes val image: Int,
)

// Real onboarding visuals derived from stitch_geosnap/onboarding_step_*_enhanced_visuals, converted
// to WebP (docs/STITCH_ASSET_MAP.md). Stitch sources left untouched.
private val pages = listOf(
    OnboardingPage(R.string.onboarding_1_title, R.string.onboarding_1_body, R.drawable.img_onboarding_gps_stamp),
    OnboardingPage(R.string.onboarding_2_title, R.string.onboarding_2_body, R.drawable.img_onboarding_realtime_location),
    OnboardingPage(R.string.onboarding_3_title, R.string.onboarding_3_body, R.drawable.img_onboarding_reports),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.completed.collectLatest { onFinished() }
    }

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = viewModel::onFinish,
                    modifier = Modifier.heightIn(min = 32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xs),
                ) {
                    Text(stringResource(R.string.action_skip), color = GeoSnapPalette.NeutralGray)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { index ->
                OnboardingPageContent(pages[index])
            }

            PageIndicator(
                count = pages.size,
                selected = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = Spacing.md),
            )

            val isLast = pagerState.currentPage == pages.lastIndex
            GeoSnapPrimaryButton(
                text = stringResource(if (isLast) R.string.action_get_started else R.string.action_next),
                onClick = {
                    if (isLast) {
                        viewModel.onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.onboarding_step_indicator, pagerState.currentPage + 1, pages.size),
                style = MaterialTheme.typography.bodySmall,
                color = GeoSnapPalette.NeutralGray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = Spacing.md),
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    // Image hugs the top and flexes with available height (weight) so title/body/indicators/button
    // all stay visible on short screens; no large centered whitespace above the image (FINAL-06).
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(page.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = Spacing.sm)
                .clip(RoundedCornerShape(16.dp))
                .background(GeoSnapPalette.SecondaryContainer),
        )
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.md, start = Spacing.md, end = Spacing.md),
        )
        Text(
            text = stringResource(page.body),
            style = MaterialTheme.typography.bodyLarge,
            color = GeoSnapPalette.NeutralGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.sm, start = Spacing.md, end = Spacing.md),
        )
    }
}

@Composable
private fun PageIndicator(count: Int, selected: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        repeat(count) { index ->
            val isActive = index == selected
            Box(
                modifier = Modifier
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) MaterialTheme.colorScheme.primary else GeoSnapPalette.Divider),
            )
        }
    }
}
