package com.geosnap.core.navigation

/** Stable route IDs (docs/NAVIGATION.md). IDs are encoded; never file paths or raw URIs. */
object Routes {
    // Startup graph
    const val LANGUAGE_FIRST = "language?first={first}"
    const val ONBOARDING = "onboarding"

    // Main destinations
    const val CAMERA = "camera"
    const val COLLECTION = "collection"
    const val REPORTING = "reporting"
    const val TEMPLATES = "templates"

    // Secondary
    const val SETTINGS = "settings"
    const val LANGUAGE_SETTINGS = "language?first=false"
    const val MEDIA_DETAIL = "media/{mediaId}"
    const val REPORT_DETAIL = "report/{reportId}"
    const val REPORT_EDIT = "report/{reportId}/edit"
    const val NEW_REPORT = "report/new"

    object Arg {
        const val FIRST = "first"
        const val MEDIA_ID = "mediaId"
        const val REPORT_ID = "reportId"
    }

    fun mediaDetail(mediaId: String) = "media/$mediaId"
    fun reportDetail(reportId: String) = "report/$reportId"
    fun reportEdit(reportId: String) = "report/$reportId/edit"
    fun language(first: Boolean) = "language?first=$first"
}

/** The four bottom-bar destinations, in fixed order. */
enum class MainDestination(val route: String) {
    CAMERA(Routes.CAMERA),
    COLLECTION(Routes.COLLECTION),
    REPORTING(Routes.REPORTING),
    TEMPLATES(Routes.TEMPLATES),
}
