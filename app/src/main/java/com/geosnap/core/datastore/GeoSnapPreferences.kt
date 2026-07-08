package com.geosnap.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.TemplateStyle
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Small user preferences (docs/DATA_MODEL.md DataStore section). Source of truth stays in Room. */
data class UserPreferences(
    val selectedLocaleTag: String?,
    val languageConfirmed: Boolean,
    val onboardingCompleted: Boolean,
    val selectedTemplateId: String,
    val defaultMode: MediaKind,
    val videoAudioEnabled: Boolean,
    val privacyDisclosureVersionAccepted: Int,
)

@Singleton
class GeoSnapPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val LOCALE = stringPreferencesKey("selected_locale_tag")
        val LANG_CONFIRMED = booleanPreferencesKey("language_confirmed")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
        val TEMPLATE = stringPreferencesKey("selected_template_id")
        val DEFAULT_MODE = stringPreferencesKey("default_capture_mode")
        val VIDEO_AUDIO = booleanPreferencesKey("video_audio_enabled")
        val PRIVACY_VERSION = intPreferencesKey("privacy_disclosure_version")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { p ->
        UserPreferences(
            selectedLocaleTag = p[Keys.LOCALE],
            languageConfirmed = p[Keys.LANG_CONFIRMED] ?: false,
            onboardingCompleted = p[Keys.ONBOARDING_DONE] ?: false,
            // Default to Minimal only when no preference has ever been set (FIX-02).
            selectedTemplateId = p[Keys.TEMPLATE] ?: TemplateStyle.MINIMAL.id,
            defaultMode = runCatching { MediaKind.valueOf(p[Keys.DEFAULT_MODE] ?: "") }
                .getOrDefault(MediaKind.PHOTO),
            videoAudioEnabled = p[Keys.VIDEO_AUDIO] ?: true,
            privacyDisclosureVersionAccepted = p[Keys.PRIVACY_VERSION] ?: 0,
        )
    }

    suspend fun setConfirmedLanguage(tag: String) = dataStore.edit {
        it[Keys.LOCALE] = tag
        it[Keys.LANG_CONFIRMED] = true
    }

    suspend fun setSelectedLanguage(tag: String) = dataStore.edit { it[Keys.LOCALE] = tag }

    suspend fun setOnboardingCompleted(completed: Boolean) =
        dataStore.edit { it[Keys.ONBOARDING_DONE] = completed }

    suspend fun setSelectedTemplate(id: String) = dataStore.edit { it[Keys.TEMPLATE] = id }

    suspend fun setDefaultMode(kind: MediaKind) =
        dataStore.edit { it[Keys.DEFAULT_MODE] = kind.name }

    suspend fun setVideoAudioEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.VIDEO_AUDIO] = enabled }

    suspend fun setPrivacyDisclosureVersion(version: Int) =
        dataStore.edit { it[Keys.PRIVACY_VERSION] = version }
}
