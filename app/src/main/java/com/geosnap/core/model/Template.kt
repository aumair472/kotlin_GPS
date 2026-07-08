package com.geosnap.core.model

/**
 * Describes what a built-in stamp style renders. Drives both the live camera overlay, the final
 * photo/video stamp, and the templates-screen preview so they stay consistent.
 */
data class TemplateSpec(
    val style: TemplateStyle,
    val showDateTime: Boolean,
    val showCoordinates: Boolean,
    val showAltitude: Boolean,
    val showAccuracy: Boolean,
    val showAddress: Boolean,
    val branded: Boolean,
)

object TemplateCatalog {
    val specs: Map<TemplateStyle, TemplateSpec> = mapOf(
        TemplateStyle.MINIMAL to TemplateSpec(
            TemplateStyle.MINIMAL,
            showDateTime = true, showCoordinates = false, showAltitude = false,
            showAccuracy = false, showAddress = false, branded = false,
        ),
        TemplateStyle.CLASSIC to TemplateSpec(
            TemplateStyle.CLASSIC,
            showDateTime = true, showCoordinates = true, showAltitude = false,
            showAccuracy = false, showAddress = false, branded = false,
        ),
        TemplateStyle.DETAILED to TemplateSpec(
            TemplateStyle.DETAILED,
            showDateTime = true, showCoordinates = true, showAltitude = true,
            showAccuracy = true, showAddress = true, branded = false,
        ),
        TemplateStyle.REPORTER to TemplateSpec(
            TemplateStyle.REPORTER,
            showDateTime = true, showCoordinates = true, showAltitude = true,
            showAccuracy = false, showAddress = true, branded = true,
        ),
    )

    fun spec(style: TemplateStyle): TemplateSpec = specs.getValue(style)
}
