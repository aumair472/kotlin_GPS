package com.geosnap.core.navigation

import com.geosnap.core.datastore.UserPreferences
import com.geosnap.core.model.MediaKind
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartupResolverTest {

    private fun prefs(
        confirmed: Boolean,
        onboarded: Boolean,
    ) = UserPreferences(
        selectedLocaleTag = if (confirmed) "en" else null,
        languageConfirmed = confirmed,
        onboardingCompleted = onboarded,
        selectedTemplateId = "classic",
        defaultMode = MediaKind.PHOTO,
        videoAudioEnabled = true,
        privacyDisclosureVersionAccepted = 0,
    )

    @Test
    fun `no confirmed language routes to language`() {
        assertThat(StartupResolver.resolve(prefs(confirmed = false, onboarded = false)))
            .isEqualTo(StartupDestination.Language)
    }

    @Test
    fun `confirmed language but onboarding incomplete routes to onboarding`() {
        assertThat(StartupResolver.resolve(prefs(confirmed = true, onboarded = false)))
            .isEqualTo(StartupDestination.Onboarding)
    }

    @Test
    fun `confirmed and onboarded routes to camera`() {
        assertThat(StartupResolver.resolve(prefs(confirmed = true, onboarded = true)))
            .isEqualTo(StartupDestination.Camera)
    }

    @Test
    fun `start route maps to first-launch language with first flag`() {
        assertThat(StartupResolver.startRoute(prefs(confirmed = false, onboarded = false)))
            .isEqualTo("language?first=true")
    }
}
