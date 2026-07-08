# GeoSnap System Design

## Core subsystems

### Capture subsystem

Coordinates CameraX preview, image capture, video recording, camera controls, orientation, visible overlays, and output targets. It emits capture lifecycle events and never writes directly to Room.

### Location subsystem

Provides foreground-only location state and point-in-time capture snapshots. It normalizes latitude, longitude, altitude, accuracy, provider, timestamp, and whether the result is approximate.

### Media finalization subsystem

Finalizes captured output in this order:

1. Validate output and available storage.
2. Associate the capture-time location snapshot.
3. Apply the selected visible timestamp/GPS template.
4. Write final output to MediaStore.
5. Write supported EXIF metadata for photos.
6. Generate a thumbnail.
7. Persist the media record and metadata transactionally.
8. Emit success only after the database record exists.

For video, post-processing may continue in WorkManager. The database must expose `PROCESSING`, `READY`, and `FAILED` states so the collection never pretends a file is ready.

### Collection subsystem

Queries media from Room using reactive paging or bounded flows. It supports search, date groups, type filters, location text, status, sort order, selection, delete, and share.

### Reporting subsystem

Creates durable report drafts, attaches media through a cross-reference table, snapshots relevant GPS metadata, validates required fields, exports PDF, stores export records, and invokes Android sharing through secure content URIs.

### Template subsystem

Ships built-in templates and supports custom templates later. The selected template is stored in DataStore and copied by ID/version into capture metadata so old media remains reproducible after a template changes.

## Important workflows

### First launch

```text
Process starts
→ Android splash API displays app icon
→ app-level branded splash loads preferences/database
→ if language not selected: Language
→ else if onboarding incomplete: Onboarding page 1
→ else: Camera
```

Do not delay startup with an arbitrary timer. The progress bar may animate for a minimum visual duration, but navigation is gated by actual initialization and a strict maximum timeout.

### Photo capture

```text
Tap shutter
→ lock capture request state
→ obtain current location snapshot using freshness/accuracy policy
→ CameraX ImageCapture to app-owned temporary destination
→ apply selected overlay to image
→ save final JPEG to MediaStore
→ write EXIF time/GPS where available
→ persist MediaItem + LocationSnapshot + template snapshot
→ update latest thumbnail and collection
```

### Video capture

```text
Tap record
→ verify audio permission only if audio enabled
→ snapshot start location/time
→ CameraX Recorder starts
→ UI shows elapsed time and stop control
→ stop/finalize
→ persist provisional media record
→ enqueue overlay/export finalization if required
→ update processing state and final URI
```

The app must recover from process death while a post-processing job is active.

### Report export

```text
Submit/export
→ validate report
→ snapshot attached media ordering and metadata
→ enqueue unique export work
→ render PDF pages using bounded bitmap decoding
→ write to app export storage or user-selected SAF URI
→ persist checksum, size, created time, URI, status
→ expose share action using content URI
```

## Data consistency rules

- A ready media record must reference an existing readable content URI.
- A report attachment must reference an existing media record.
- Deleting media used by a report requires explicit confirmation and a defined policy: detach or keep an exported copy.
- Report status is derived from draft/export/share events, not from UI-only flags.
- Capture timestamp uses an instant plus timezone ID/offset; never store only a formatted string.
- Display address is optional and may be resolved later. Coordinates remain the primary location record.

## Recovery and idempotency

- WorkManager jobs use stable unique names derived from media/report IDs.
- Retrying finalization must not create duplicate MediaStore files.
- Export retries either replace the failed internal output or create a new version intentionally.
- Database migrations are mandatory; destructive fallback is forbidden in production.
- Incomplete temporary files are cleaned only after checking no active job references them.

## Future cloud architecture

No backend is required for the initial release. If cloud sync is later added:

- keep local-first repositories;
- use user authentication and tenant isolation;
- encrypt transport;
- upload by content hash with resumable jobs;
- never upload media without explicit opt-in and disclosure;
- resolve conflicts using versioned records rather than last-write-wins for reports.
