package com.geosnap.feature.language

import app.cash.turbine.test
import com.geosnap.core.model.AppLanguage
import com.geosnap.testing.FakeLocaleManager
import com.geosnap.testing.FakeSettingsRepository
import com.geosnap.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `first launch confirm persists locale and emits Proceed`() = runTest {
        val settings = FakeSettingsRepository()
        val locale = FakeLocaleManager()
        val vm = LanguageViewModel(settings, locale)

        vm.effects.test {
            vm.onSelect(AppLanguage.URDU)
            vm.onConfirm(firstLaunch = true)
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LanguageEffect.Proceed)
        }
        assertThat(settings.state.value.languageConfirmed).isTrue()
        assertThat(settings.state.value.selectedLocaleTag).isEqualTo("ur")
        assertThat(locale.applied).contains("ur")
    }

    @Test
    fun `settings mode confirm emits Dismiss without setting confirmed flag`() = runTest {
        val settings = FakeSettingsRepository()
        val locale = FakeLocaleManager()
        val vm = LanguageViewModel(settings, locale)

        vm.effects.test {
            vm.onSelect(AppLanguage.FRENCH)
            vm.onConfirm(firstLaunch = false)
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LanguageEffect.Dismiss)
        }
        assertThat(settings.state.value.selectedLocaleTag).isEqualTo("fr")
        assertThat(settings.state.value.languageConfirmed).isFalse()
    }
}
