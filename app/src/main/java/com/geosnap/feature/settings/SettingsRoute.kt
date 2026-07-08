package com.geosnap.feature.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    SettingsScreen(
        onBack = onBack,
        onOpenLanguage = onOpenLanguage,
        onOpenPrivacy = onOpenPrivacy,
        onOpenTerms = onOpenTerms,
    )
}
