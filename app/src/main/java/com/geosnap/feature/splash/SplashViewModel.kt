package com.geosnap.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.common.LocaleManager
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.model.AppLanguage
import com.geosnap.core.navigation.StartupResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val localeManager: LocaleManager,
) : ViewModel() {

    private val _startRoute = MutableStateFlow<String?>(null)
    /** Null until startup is resolved; then the route to navigate to. */
    val startRoute: StateFlow<String?> = _startRoute.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = settings.preferences.first()
            val activeTag = localeManager.currentTag()
            if (activeTag.isNullOrEmpty()) {
                // Re-apply any persisted, valid locale so a returning user keeps their language even
                // if the per-app locale store was cleared.
                prefs.selectedLocaleTag
                    ?.let { AppLanguage.fromTag(it) }
                    ?.let { localeManager.applyLanguageTag(it.tag) }
            } else if (activeTag != prefs.selectedLocaleTag) {
                // AppCompat already has a real applied locale (e.g. auto-restored by the OS from a
                // previous session) that disagrees with our own DataStore record. The real applied
                // locale wins; sync DataStore to match so UI reading DataStore (e.g. Settings) never
                // shows a stale language while the app actually renders in a different one.
                settings.setSelectedLanguage(activeTag)
            }
            _startRoute.value = StartupResolver.startRoute(prefs)
        }
    }
}
