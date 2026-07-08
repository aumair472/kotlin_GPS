package com.geosnap.core.model

/**
 * Supported per-app languages (docs/LOCALIZATION.md). `tag` is a BCP-47 language tag used with
 * AndroidX per-app locales. Display name + endonym are resolved from string resources in the UI so
 * they remain correct under any active locale.
 */
enum class AppLanguage(val tag: String, val isRtl: Boolean) {
    ENGLISH("en", false),
    URDU("ur", true),
    ARABIC("ar", true),
    HINDI("hi", false),
    FRENCH("fr", false),
    SPANISH("es", false),
    PORTUGUESE("pt", false),
    GERMAN("de", false),
    ITALIAN("it", false),
    JAPANESE("ja", false),
    CHINESE_SIMPLIFIED("zh-CN", false);

    companion object {
        val DEFAULT = ENGLISH
        fun fromTag(tag: String?): AppLanguage? {
            if (tag.isNullOrBlank()) return null
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) }
                ?: entries.firstOrNull { tag.startsWith(it.tag.substringBefore('-'), ignoreCase = true) }
        }
    }
}
