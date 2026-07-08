# Database Implementation Specification

This file expands `DATA_MODEL.md` into implementation rules.

## Room database

Create `GeoSnapDatabase` with exported schemas committed under `schemas/`. Start at version 1 and never use destructive migration in release. Enable foreign keys and use transactions for relationship changes.

Suggested DAOs:

- `MediaDao`
- `LocationDao`
- `ReportDao`
- `ReportMediaDao`
- `ReportExportDao`
- `TemplateDao`

## Repository contracts

```kotlin
interface MediaRepository {
    fun observeMedia(query: MediaQuery): Flow<PagingData<MediaItem>>
    fun observeMediaById(id: MediaId): Flow<MediaItem?>
    suspend fun finalizeCapture(input: FinalizedCapture): Result<MediaItem>
    suspend fun delete(ids: Set<MediaId>): Result<Unit>
}

interface ReportRepository {
    fun observeReports(query: ReportQuery): Flow<PagingData<ReportSummary>>
    fun observeReport(id: ReportId): Flow<ReportDraft?>
    suspend fun createDraft(): ReportId
    suspend fun updateDraft(change: ReportChange): Result<Unit>
    suspend fun attachMedia(reportId: ReportId, mediaIds: List<MediaId>): Result<Unit>
}
```

Domain models must not be Room entities. Map at repository/data-source boundaries.

## Query behavior

### Collection

Query filters are composable:

- media type set;
- start/end instant;
- normalized search text;
- processing state;
- sort descending by capture instant.

Group headings are computed from capture instant in the display timezone, not from stored formatted strings.

### Reporting

Filter by report status and normalized title/location search. Fetch preview attachment metadata efficiently through relation projections or bounded secondary queries.

## Full-text search

For MVP, normalized indexed columns and `LIKE` may be sufficient. If realistic performance tests show degradation, introduce Room FTS with migration and tests. Do not add FTS before query requirements are known.

## Data deletion

Deletion workflow:

1. Resolve whether media is attached to reports.
2. Ask user whether to detach or cancel; exported reports remain independent files.
3. Delete MediaStore item with recoverable security handling where required.
4. Delete database record/relations transactionally after file result is known.
5. Delete private thumbnails/source files.

Never silently leave a ready database row pointing to a deleted file.

## Schema tests

- fresh create;
- every migration path;
- foreign key cascade/restrict behavior;
- report attachment ordering;
- query filter combinations;
- process state transitions;
- date boundaries across timezones/DST.
