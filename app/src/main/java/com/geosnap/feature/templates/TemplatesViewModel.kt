package com.geosnap.feature.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.model.TemplateStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val settings: SettingsRepository,
) : ViewModel() {

    val selected: StateFlow<TemplateStyle> = settings.selectedTemplate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TemplateStyle.DEFAULT)

    val styles: List<TemplateStyle> = TemplateStyle.entries

    fun select(style: TemplateStyle) {
        viewModelScope.launch { settings.setSelectedTemplate(style) }
    }
}
