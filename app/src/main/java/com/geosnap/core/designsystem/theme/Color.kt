package com.geosnap.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * GeoSnap palette, sourced from [stitch_geosnap/Design/DESIGN.md] and docs/DESIGN_SYSTEM.md.
 * Precision Minimalism: flat, high-contrast, functional. No decorative gradients.
 */
internal object GeoSnapPalette {
    val Primary = Color(0xFF004AC6)
    val PrimaryContainer = Color(0xFF2563EB) // brand blue used for primary actions
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFFEEEFFF)
    val InversePrimary = Color(0xFFB4C5FF)

    val Secondary = Color(0xFF585F6C)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFDCE2F3)
    val OnSecondaryContainer = Color(0xFF5E6572)

    val Tertiary = Color(0xFF943700)
    val OnTertiary = Color(0xFFFFFFFF)

    val Error = Color(0xFFBA1A1A)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF93000A)

    val Background = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF191B23)
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF191B23)
    val SurfaceVariant = Color(0xFFF9FAFB)
    val OnSurfaceVariant = Color(0xFF434655)
    val SurfaceContainer = Color(0xFFF3F3FE)
    val SurfaceContainerHigh = Color(0xFFE7E7F3)

    val Outline = Color(0xFF737686)
    val OutlineVariant = Color(0xFFE5E7EB)

    val InverseSurface = Color(0xFF2E3039)
    val InverseOnSurface = Color(0xFFF0F0FB)

    // Functional accents
    val NeutralGray = Color(0xFF6B7280)
    val Text = Color(0xFF111827)
    val Divider = Color(0xFFE5E7EB)
    val ChipBackground = Color(0xFFF3F4F6)

    // Status semantics
    val StatusDraft = Color(0xFFB45309)
    val StatusDraftContainer = Color(0xFFFEF3C7)
    val StatusExported = Color(0xFF047857)
    val StatusExportedContainer = Color(0xFFD1FAE5)
    val StatusShared = Color(0xFF1D4ED8)
    val StatusSharedContainer = Color(0xFFDBEAFE)
    val GpsLive = Color(0xFF10B981)

    // Camera overlay surfaces (dark, semi-opaque) per DESIGN_SYSTEM camera overlay rules.
    val OverlayScrim = Color(0xCC111827)
    val CameraHeader = Color(0xFF111827)
}
