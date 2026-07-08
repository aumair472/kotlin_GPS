package com.geosnap.feature

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.geosnap.core.designsystem.theme.GeoSnapTheme
import com.geosnap.feature.templates.TemplatesScreen
import com.geosnap.feature.templates.TemplatesViewModel
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
class TemplatesScreenRobolectricTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun selectingDetailed_persistsTemplate() {
        val settings = FakeSettingsRepository()

        rule.setContent {
            GeoSnapTheme { TemplatesScreen(viewModel = TemplatesViewModel(settings)) }
        }

        rule.onNodeWithText("Detailed").performClick()
        rule.waitForIdle()

        assertThat(settings.state.value.selectedTemplateId).isEqualTo("detailed")
    }
}
