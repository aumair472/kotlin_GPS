package com.geosnap.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Type scale from docs/DESIGN_SYSTEM.md.
 *
 * The design specifies Hanken Grotesk (UI) + Geist (mono data). Per LOCALIZATION.md the font
 * stack MUST contain Urdu, Arabic, Devanagari, Japanese and Chinese glyphs and must never render
 * missing-square glyphs. Bundled Latin-only faces cannot satisfy that, so we use the platform
 * font family (full multilingual coverage) for UI text and the platform monospace family for
 * coordinate/timestamp readouts. See DECISIONS.md D-fonts.
 */
private val UiFontFamily = FontFamily.Default

/** Monospace family for coordinate strings and timestamps (character alignment). */
val MonoDataFamily = FontFamily.Monospace

internal val GeoSnapTypography = Typography(
    // headline-lg
    displaySmall = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.02).em,
    ),
    // headline-lg-mobile
    headlineLarge = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.02).em,
    ),
    // headline-md
    headlineMedium = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    // body-lg
    bodyLarge = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    // body-sm
    bodyMedium = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    // label-caps
    labelLarge = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.05.em,
    ),
    labelSmall = TextStyle(
        fontFamily = UiFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.05.em,
    ),
)

/** mono-data style for coordinates and timestamps. */
val monoDataStyle: TextStyle
    @Composable get() = TextStyle(
        fontFamily = MonoDataFamily, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp,
    )
