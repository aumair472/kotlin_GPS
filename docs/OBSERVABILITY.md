# Observability and Diagnostics

## MVP stance

GeoSnap is local-first. Observability must aid reliability without collecting sensitive location/media/report content.

## Structured internal events

Use typed event codes such as:

- `camera_bind_success/failure`
- `photo_capture_started/finalized/failed`
- `video_record_started/finalized/processing_failed`
- `location_permission_state`
- `location_snapshot_available/unavailable`
- `media_db_write_failed`
- `report_export_started/completed/failed`

Do not include coordinates, addresses, report text, content URIs, filenames, or thumbnails.

## Debug diagnostics

Debug builds may expose a developer diagnostics screen or exportable sanitized log containing:

- app/build version;
- device/API/model;
- granted permission states;
- camera capabilities summary;
- location provider enabled state and coarse accuracy bucket;
- database schema version/counts;
- last typed error codes;
- WorkManager job states.

Exact private content remains excluded.

## Crash reporting

If a third-party crash reporter is later added:

- obtain appropriate disclosure/consent;
- disable automatic collection until configured as required;
- apply breadcrumb redaction;
- review SDK data safety behavior;
- avoid attaching screenshots or file paths.

## Production health

Monitor Play Android vitals for crashes, ANRs, startup, rendering, excessive wakeups, and permission denials. Tie each release to a versioned verification report.
