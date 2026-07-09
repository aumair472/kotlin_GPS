package com.geosnap.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.common.LocaleManager
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    settings: SettingsRepository,
    localeManager: LocaleManager,
) : ViewModel() {

    val currentLanguage: StateFlow<AppLanguage> = settings.preferences
        .map { prefs ->
            AppLanguage.fromTag(localeManager.currentTag())
                ?: AppLanguage.fromTag(prefs.selectedLocaleTag)
                ?: AppLanguage.DEFAULT
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.DEFAULT)
}
