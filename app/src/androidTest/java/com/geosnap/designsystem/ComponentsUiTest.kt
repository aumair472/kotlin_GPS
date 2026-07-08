package com.geosnap.designsystem

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.geosnap.core.designsystem.component.GeoSnapEmptyState
import com.geosnap.core.designsystem.component.GeoSnapPrimaryButton
import com.geosnap.core.designsystem.theme.GeoSnapTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for shared components (P8.1). Runs on a device/emulator via
 * `./gradlew :app:connectedDebugAndroidTest` (requires hardware/AVD).
 */
class ComponentsUiTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun primaryButton_click_invokesCallback() {
        var clicked = false
        rule.setContent {
            GeoSnapTheme { GeoSnapPrimaryButton(text = "Continue", onClick = { clicked = true }) }
        }
        rule.onNodeWithText("Continue").assertIsDisplayed().performClick()
        assertTrue(clicked)
    }

    @Test
    fun emptyState_showsMessage() {
        rule.setContent { GeoSnapTheme { GeoSnapEmptyState(message = "No captures yet") } }
        rule.onNodeWithText("No captures yet").assertIsDisplayed()
    }
}
