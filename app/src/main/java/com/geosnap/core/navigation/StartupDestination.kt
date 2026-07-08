package com.geosnap.core.navigation

import com.geosnap.core.datastore.UserPreferences

/** Startup decision (docs/NAVIGATION.md). */
sealed interface StartupDestination {
    data object Language : StartupDestination
    data object Onboarding : StartupDestination
    data object Camera : StartupDestination
}

object StartupResolver {
    /**
     * 1) no confirmed language → Language; 2) onboarding incomplete → Onboarding; 3) → Camera.
     * Pure function so it is unit-testable without Android.
     */
    fun resolve(prefs: UserPreferences): StartupDestination = when {
        !prefs.languageConfirmed -> StartupDestination.Language
        !prefs.onboardingCompleted -> StartupDestination.Onboarding
        else -> StartupDestination.Camera
    }

    fun startRoute(prefs: UserPreferences): String = when (resolve(prefs)) {
        StartupDestination.Language -> Routes.language(first = true)
        StartupDestination.Onboarding -> Routes.ONBOARDING
        StartupDestination.Camera -> Routes.CAMERA
    }
}
