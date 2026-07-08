package com.geosnap.feature

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.geosnap.core.designsystem.theme.GeoSnapTheme
import com.geosnap.feature.onboarding.OnboardingScreen
import com.geosnap.feature.onboarding.OnboardingViewModel
import com.geosnap.testing.FakeSettingsRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class OnboardingScreenRobolectricTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun firstPageRenders_andSkipCompletesOnboarding() {
        val settings = FakeSettingsRepository()
        var finished = false

        rule.setContent {
            GeoSnapTheme {
                OnboardingScreen(onFinished = { finished = true }, viewModel = OnboardingViewModel(settings))
            }
        }

        // Page 1 content present and the first-page CTA is "Next" (not "Get Started").
        rule.onNodeWithText("Stamp Every Photo with GPS").assertExists()
        rule.onNodeWithText("Next").assertExists()

        // Skip completes onboarding without paging (reliable headless).
        rule.onNodeWithText("Skip").performClick()
        rule.waitForIdle()

        assertThat(settings.state.value.onboardingCompleted).isTrue()
        assertThat(finished).isTrue()
    }
}
