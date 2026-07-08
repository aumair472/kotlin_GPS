package com.geosnap.core.model

/** Collection filter chips (docs collection screen). */
enum class CollectionFilter { ALL, TODAY, THIS_WEEK, VIDEOS, PHOTOS }

data class MediaQuery(
    val filter: CollectionFilter = CollectionFilter.ALL,
    val search: String? = null,
)

/** Reporting status filter chips. */
enum class ReportFilter { ALL, DRAFT, EXPORTED, SHARED }

data class ReportQuery(
    val filter: ReportFilter = ReportFilter.ALL,
    val search: String? = null,
)
