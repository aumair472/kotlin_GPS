package com.geosnap.feature.language

import androidx.annotation.StringRes
import com.geosnap.R
import com.geosnap.core.model.AppLanguage

/** Maps a language to its display-name and endonym string resources (shown in their own script). */
data class LanguageDisplay(
    val language: AppLanguage,
    @StringRes val nameRes: Int,
    @StringRes val endonymRes: Int,
)

val languageCatalog: List<LanguageDisplay> = listOf(
    LanguageDisplay(AppLanguage.ENGLISH, R.string.lang_name_en, R.string.lang_endonym_en),
    LanguageDisplay(AppLanguage.URDU, R.string.lang_name_ur, R.string.lang_endonym_ur),
    LanguageDisplay(AppLanguage.ARABIC, R.string.lang_name_ar, R.string.lang_endonym_ar),
    LanguageDisplay(AppLanguage.HINDI, R.string.lang_name_hi, R.string.lang_endonym_hi),
    LanguageDisplay(AppLanguage.FRENCH, R.string.lang_name_fr, R.string.lang_endonym_fr),
    LanguageDisplay(AppLanguage.SPANISH, R.string.lang_name_es, R.string.lang_endonym_es),
    LanguageDisplay(AppLanguage.PORTUGUESE, R.string.lang_name_pt, R.string.lang_endonym_pt),
    LanguageDisplay(AppLanguage.GERMAN, R.string.lang_name_de, R.string.lang_endonym_de),
    LanguageDisplay(AppLanguage.ITALIAN, R.string.lang_name_it, R.string.lang_endonym_it),
    LanguageDisplay(AppLanguage.JAPANESE, R.string.lang_name_ja, R.string.lang_endonym_ja),
    LanguageDisplay(AppLanguage.CHINESE_SIMPLIFIED, R.string.lang_name_zh, R.string.lang_endonym_zh),
)
