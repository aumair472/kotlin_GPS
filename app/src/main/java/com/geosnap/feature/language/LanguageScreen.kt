package com.geosnap.feature.language

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapPrimaryButton
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.core.model.AppLanguage
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    isFirstLaunch: Boolean,
    onProceed: () -> Unit,
    onClose: () -> Unit,
    viewModel: LanguageViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                LanguageEffect.Proceed -> onProceed()
                LanguageEffect.Dismiss -> onClose()
            }
        }
    }

    Scaffold(
        topBar = {
            GeoSnapTopBar(
                title = stringResource(R.string.language_title),
                onNavigateBack = onClose,
                navigationContentDescription = stringResource(R.string.action_close),
                actions = {},
            )
        },
        bottomBar = {
            GeoSnapPrimaryButton(
                text = stringResource(R.string.action_continue),
                onClick = { viewModel.onConfirm(isFirstLaunch) },
                enabled = !state.saving,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.gps_icon),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                    )
                    Text(
                        text = stringResource(R.string.language_heading),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = Spacing.md),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.language_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GeoSnapPalette.NeutralGray,
                        modifier = Modifier.padding(top = Spacing.xs),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            items(languageCatalog, key = { it.language.name }) { display ->
                LanguageCard(
                    display = display,
                    selected = state.selected == display.language,
                    onSelect = { viewModel.onSelect(display.language) },
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(
    display: LanguageDisplay,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else GeoSnapPalette.Divider
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .border(
                width = if (selected) 2.dp else 1.dp,
                brush = SolidColor(borderColor),
                shape = RoundedCornerShape(8.dp),
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) GeoSnapPalette.SurfaceContainer else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(display.nameRes), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(display.endonymRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GeoSnapPalette.NeutralGray,
                )
            }
            RadioButton(selected = selected, onClick = onSelect)
        }
    }
}
