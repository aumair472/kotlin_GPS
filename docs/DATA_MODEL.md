# Local Data Model

Use Room with stable UUID string IDs or a consistent binary UUID strategy. Store instants as epoch milliseconds and timezone IDs separately.

## `media_items`

| Column | Type | Notes |
|---|---|---|
| id | TEXT PK | UUID |
| kind | TEXT | PHOTO / VIDEO |
| status | TEXT | PROCESSING / READY / FAILED / MISSING |
| content_uri | TEXT nullable | finalized content URI |
| source_uri | TEXT nullable | private source during processing |
| display_name | TEXT | sanitized system name |
| mime_type | TEXT | image/jpeg, video/mp4 |
| captured_at_epoch_ms | INTEGER | authoritative app capture instant |
| timezone_id | TEXT | IANA timezone |
| duration_ms | INTEGER nullable | video |
| width | INTEGER nullable | pixels |
| height | INTEGER nullable | pixels |
| size_bytes | INTEGER nullable | final file size |
| orientation_degrees | INTEGER | normalized rotation |
| template_id | TEXT | selected template |
| template_version | INTEGER | formatter version |
| rendered_stamp | TEXT nullable | audit/debug display text |
| location_id | TEXT nullable FK | capture location |
| address_search_text | TEXT nullable | normalized search field |
| thumbnail_uri | TEXT nullable | private/app cache URI |
| checksum_sha256 | TEXT nullable | optional integrity aid |
| created_at_epoch_ms | INTEGER | record creation |
| updated_at_epoch_ms | INTEGER | last update |
| failure_code | TEXT nullable | typed failure |

Indexes: captured time, kind, status, location ID, normalized search text.

## `locations`

| Column | Type | Notes |
|---|---|---|
| id | TEXT PK | UUID |
| latitude | REAL | validated range |
| longitude | REAL | validated range |
| altitude_m | REAL nullable | never substitute 0 for missing |
| accuracy_m | REAL nullable | horizontal accuracy |
| provider_time_ms | INTEGER nullable | source time |
| observed_at_ms | INTEGER | app observation |
| timezone_id | TEXT | capture timezone |
| is_approximate | INTEGER | boolean |
| is_mock | INTEGER | boolean where detectable |
| provider | TEXT nullable | sanitized |
| locality | TEXT nullable | city/locality |
| admin_area | TEXT nullable | state/province |
| country_code | TEXT nullable | ISO |
| formatted_address | TEXT nullable | display only |

## `reports`

| Column | Type | Notes |
|---|---|---|
| id | TEXT PK | UUID |
| title | TEXT | trimmed, bounded |
| notes | TEXT | bounded |
| status | TEXT | DRAFT / EXPORTED / SHARED |
| report_location_id | TEXT nullable FK | explicit report location |
| report_instant_ms | INTEGER | selected date/time |
| timezone_id | TEXT | timezone |
| created_at_ms | INTEGER | |
| updated_at_ms | INTEGER | |

## `report_media`

Composite key `(report_id, media_id)` with `sort_order`, caption, and optional inclusion flags.

## `report_exports`

| Column | Type | Notes |
|---|---|---|
| id | TEXT PK | export version |
| report_id | TEXT FK | |
| status | TEXT | QUEUED/RUNNING/READY/FAILED |
| output_uri | TEXT nullable | content URI or private FileProvider target |
| mime_type | TEXT | application/pdf |
| size_bytes | INTEGER nullable | |
| checksum_sha256 | TEXT nullable | |
| created_at_ms | INTEGER | |
| completed_at_ms | INTEGER nullable | |
| shared_at_ms | INTEGER nullable | |
| error_code | TEXT nullable | |

## `templates`

Built-in templates can be seeded. Store ID, version, category, localized resource keys/config JSON, built-in flag, and active state. Do not store translated UI copy as one fixed language string.

## `capture_sessions` optional

Useful for grouping sequential worksite captures. Add only if a real UI or reporting requirement uses it.

## DataStore preferences

- selected app locale
- language confirmed flag
- onboarding completed flag
- selected template ID
- default photo/video mode
- video audio enabled
- units preference if introduced
- privacy disclosure version accepted if required

## Transactions

Use transactions for:

- final media record + location linkage;
- report creation + attachments;
- media deletion + report relationship policy;
- report export status transition and output record.

## Migrations

Every schema change requires:

- explicit migration;
- migration test from every supported production schema path;
- no destructive fallback in release;
- updated schema export committed to source control.
