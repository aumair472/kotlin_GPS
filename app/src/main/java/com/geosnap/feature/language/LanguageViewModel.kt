package com.geosnap.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.common.LocaleManager
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class LanguageUiState(
    val selected: AppLanguage = AppLanguage.DEFAULT,
    val saving: Boolean = false,
)

sealed interface LanguageEffect {
    /** First-launch confirmation completed; advance to onboarding. */
    data object Proceed : LanguageEffect
    /** Settings mode: locale applied, return to the previous screen. */
    data object Dismiss : LanguageEffect
}

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val localeManager: LocaleManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageUiState())
    val state: StateFlow<LanguageUiState> = _state.asStateFlow()

    private val _effects = Channel<LanguageEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val prefs = settings.preferences.first()
            val current = AppLanguage.fromTag(localeManager.currentTag())
                ?: AppLanguage.fromTag(prefs.selectedLocaleTag)
                ?: AppLanguage.DEFAULT
            _state.value = _state.value.copy(selected = current)
        }
    }

    fun onSelect(language: AppLanguage) {
        _state.value = _state.value.copy(selected = language)
    }

    /** @param firstLaunch true on the first-run language screen; false from Settings. */
    fun onConfirm(firstLaunch: Boolean) {
        if (_state.value.saving) return
        val language = _state.value.selected
        _state.value = _state.value.copy(saving = true)
        viewModelScope.launch {
            if (firstLaunch) settings.confirmLanguage(language.tag) else settings.setSelectedLanguage(language.tag)
            _effects.send(if (firstLaunch) LanguageEffect.Proceed else LanguageEffect.Dismiss)
            // Applying the locale may recreate the activity; navigation effect is sent first so the
            // back stack is correct regardless of recreation timing.
            localeManager.applyLanguageTag(language.tag)
        }
    }
}
