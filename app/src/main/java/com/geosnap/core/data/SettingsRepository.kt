package com.geosnap.core.data

import com.geosnap.core.datastore.GeoSnapPreferences
import com.geosnap.core.datastore.UserPreferences
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.TemplateStyle
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    val preferences: Flow<UserPreferences>
    val selectedTemplate: Flow<TemplateStyle>
    suspend fun confirmLanguage(tag: String)
    suspend fun setSelectedLanguage(tag: String)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setSelectedTemplate(style: TemplateStyle)
    suspend fun setDefaultMode(mode: MediaKind)
    suspend fun setVideoAudioEnabled(enabled: Boolean)
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val prefs: GeoSnapPreferences,
) : SettingsRepository {
    override val preferences: Flow<UserPreferences> = prefs.preferences
    override val selectedTemplate: Flow<TemplateStyle> =
        prefs.preferences.map { TemplateStyle.fromId(it.selectedTemplateId) }

    override suspend fun confirmLanguage(tag: String) { prefs.setConfirmedLanguage(tag) }
    override suspend fun setSelectedLanguage(tag: String) { prefs.setSelectedLanguage(tag) }
    override suspend fun setOnboardingCompleted(completed: Boolean) { prefs.setOnboardingCompleted(completed) }
    override suspend fun setSelectedTemplate(style: TemplateStyle) { prefs.setSelectedTemplate(style.id) }
    override suspend fun setDefaultMode(mode: MediaKind) { prefs.setDefaultMode(mode) }
    override suspend fun setVideoAudioEnabled(enabled: Boolean) { prefs.setVideoAudioEnabled(enabled) }
}
