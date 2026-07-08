package com.geosnap.testing

import com.geosnap.core.common.LocaleManager
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.datastore.UserPreferences
import com.geosnap.core.model.MediaKind
import com.geosnap.core.model.TemplateStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLocaleManager(private var tag: String? = null) : LocaleManager {
    val applied = mutableListOf<String>()
    override fun applyLanguageTag(tag: String) { this.tag = tag; applied += tag }
    override fun currentTag(): String? = tag
}

class FakeSettingsRepository(
    initial: UserPreferences = UserPreferences(
        selectedLocaleTag = null,
        languageConfirmed = false,
        onboardingCompleted = false,
        selectedTemplateId = TemplateStyle.DEFAULT.id,
        defaultMode = MediaKind.PHOTO,
        videoAudioEnabled = true,
        privacyDisclosureVersionAccepted = 0,
    ),
) : SettingsRepository {
    val state = MutableStateFlow(initial)
    override val preferences = state
    override val selectedTemplate = state.map { TemplateStyle.fromId(it.selectedTemplateId) }

    override suspend fun confirmLanguage(tag: String) {
        state.value = state.value.copy(selectedLocaleTag = tag, languageConfirmed = true)
    }
    override suspend fun setSelectedLanguage(tag: String) {
        state.value = state.value.copy(selectedLocaleTag = tag)
    }
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        state.value = state.value.copy(onboardingCompleted = completed)
    }
    override suspend fun setSelectedTemplate(style: TemplateStyle) {
        state.value = state.value.copy(selectedTemplateId = style.id)
    }
    override suspend fun setDefaultMode(mode: MediaKind) {
        state.value = state.value.copy(defaultMode = mode)
    }
    override suspend fun setVideoAudioEnabled(enabled: Boolean) {
        state.value = state.value.copy(videoAudioEnabled = enabled)
    }
}
