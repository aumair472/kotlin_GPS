package com.geosnap.feature.settings

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geosnap.BuildConfig
import com.geosnap.R
import com.geosnap.core.designsystem.component.GeoSnapTopBar
import com.geosnap.core.designsystem.theme.GeoSnapPalette
import com.geosnap.core.designsystem.theme.Spacing
import com.geosnap.feature.language.languageCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val languageName = languageCatalog.firstOrNull { it.language == language }?.nameRes?.let { stringResource(it) }

    Scaffold(
        topBar = {
            GeoSnapTopBar(
                title = stringResource(R.string.settings_title),
                onNavigateBack = onBack,
                navigationContentDescription = stringResource(R.string.action_back),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(Spacing.md)) {
            Text(
                stringResource(R.string.settings_preferences),
                style = MaterialTheme.typography.labelMedium,
                color = GeoSnapPalette.NeutralGray,
                modifier = Modifier.padding(vertical = Spacing.sm),
            )
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .border(1.dp, GeoSnapPalette.Divider, RoundedCornerShape(8.dp)),
            ) {
                SettingRow(Icons.Filled.Language, stringResource(R.string.settings_language), languageName, onOpenLanguage)
                HorizontalDivider(color = GeoSnapPalette.Divider)
                SettingRow(Icons.Filled.Share, stringResource(R.string.settings_share_app), null) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_text, context.getString(R.string.app_name)))
                    }
                    ContextCompat.startActivity(context, Intent.createChooser(intent, context.getString(R.string.settings_share_app)), null)
                }
                HorizontalDivider(color = GeoSnapPalette.Divider)
                SettingRow(Icons.Filled.Shield, stringResource(R.string.settings_privacy), null, onOpenPrivacy)
                HorizontalDivider(color = GeoSnapPalette.Divider)
                SettingRow(Icons.Filled.Description, stringResource(R.string.settings_terms), null, onOpenTerms)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(R.drawable.gps_icon), contentDescription = null, modifier = Modifier.size(56.dp))
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = Spacing.sm))
                Text(
                    stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoSnapPalette.NeutralGray,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClick = onClick).padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f).padding(start = Spacing.md))
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = GeoSnapPalette.NeutralGray, modifier = Modifier.padding(end = Spacing.sm))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = GeoSnapPalette.NeutralGray)
    }
}
