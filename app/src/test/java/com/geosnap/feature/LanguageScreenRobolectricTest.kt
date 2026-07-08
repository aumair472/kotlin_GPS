package com.geosnap.feature

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.geosnap.core.designsystem.theme.GeoSnapTheme
import com.geosnap.feature.language.LanguageScreen
import com.geosnap.feature.language.LanguageViewModel
import com.geosnap.testing.FakeLocaleManager
import com.geosnap.testing.FakeSettingsRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI test executed in the JVM via Robolectric (no device needed). Verifies the first-launch
 * language picker renders the catalog, selection works, and Continue persists + applies the locale.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class LanguageScreenRobolectricTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun firstLaunch_rendersCatalog_selectsAndConfirms() {
        val settings = FakeSettingsRepository()
        val locale = FakeLocaleManager()
        var proceeded = false

        rule.setContent {
            GeoSnapTheme {
                LanguageScreen(
                    isFirstLaunch = true,
                    onProceed = { proceeded = true },
                    onClose = {},
                    viewModel = LanguageViewModel(settings, locale),
                )
            }
        }

        rule.onNodeWithText("Choose Your Language").assertIsDisplayed()
        rule.onNodeWithText("English").assertIsDisplayed()
        rule.onNodeWithText("اردو").assertIsDisplayed() // Urdu endonym

        rule.onNodeWithText("اردو").performClick()
        rule.onNodeWithText("Continue").performClick()
        rule.waitForIdle()

        assertThat(settings.state.value.languageConfirmed).isTrue()
        assertThat(settings.state.value.selectedLocaleTag).isEqualTo("ur")
        assertThat(locale.applied).contains("ur")
        assertThat(proceeded).isTrue()
    }
}
