package com.geosnap.core.common

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import javax.inject.Inject
import javax.inject.Singleton

/** Wraps AndroidX per-app locales so ViewModels stay free of framework statics (testable). */
interface LocaleManager {
    fun applyLanguageTag(tag: String)
    fun currentTag(): String?
}

@Singleton
class AppCompatLocaleManager @Inject constructor() : LocaleManager {
    override fun applyLanguageTag(tag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    override fun currentTag(): String? =
        AppCompatDelegate.getApplicationLocales().toLanguageTags().takeIf { it.isNotEmpty() }
}
