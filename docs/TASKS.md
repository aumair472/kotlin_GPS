# GeoSnap Master Task Tracker

Use task states: `[ ]` not started, `[-]` active, `[x]` verified, `[!]` blocked. A task becomes `[x]` only when its acceptance evidence is recorded.

## P0 — Reconnaissance

- [ ] P0.1 Inspect complete repository and current Gradle configuration.
- [ ] P0.2 Inspect every file under `stitch_geosnap/`; create `docs/STITCH_ASSET_MAP.md`.
- [ ] P0.3 Map each supplied reference screenshot to its Stitch source and target Compose route.
- [ ] P0.4 Run baseline build, lint, and tests; record failures.
- [ ] P0.5 Confirm package ID, minSdk, targetSdk, version strategy, and app ownership.

## P1 — Foundation

- [ ] P1.1 Add version catalog and stable dependency set.
- [ ] P1.2 Establish architecture/module/package boundaries.
- [ ] P1.3 Add Hilt and dispatcher qualifiers.
- [ ] P1.4 Add Room database, schema export, and migration test foundation.
- [ ] P1.5 Add DataStore preferences.
- [ ] P1.6 Add navigation graph and bottom navigation shell.
- [ ] P1.7 Implement design tokens and reusable components.
- [ ] P1.8 Add CI quality commands and local verification script.

## P2 — Startup and localization

- [ ] P2.1 Implement Android system splash plus branded GeoSnap splash.
- [ ] P2.2 Implement startup destination resolver and tests.
- [ ] P2.3 Implement language catalog and per-app locale persistence.
- [ ] P2.4 Implement first-launch language screen matching reference.
- [ ] P2.5 Add all locale resource structures.
- [ ] P2.6 Verify Arabic/Urdu RTL and pseudolocales.
- [ ] P2.7 Implement 3-page onboarding with swipe, skip, next, and completion state.
- [ ] P2.8 Add screenshot/UI tests for startup flow.

## P3 — Camera/location/photo

- [ ] P3.1 Implement camera and location permission state reducers/rationales.
- [ ] P3.2 Implement lifecycle-safe CameraX preview.
- [ ] P3.3 Implement photo/video mode UI and camera controls.
- [ ] P3.4 Implement foreground location gateway and UI status.
- [ ] P3.5 Implement capture-time location policy.
- [ ] P3.6 Implement photo capture to temporary file.
- [ ] P3.7 Implement visible photo stamp renderer.
- [ ] P3.8 Write final photo to MediaStore and EXIF.
- [ ] P3.9 Persist media/location/template metadata transactionally.
- [ ] P3.10 Verify on physical devices and inspect final JPEG metadata.

## P4 — Video

- [ ] P4.1 Implement microphone permission on demand.
- [ ] P4.2 Implement CameraX Recorder start/status/stop/error flow.
- [ ] P4.3 Select and document OverlayEffect vs Media3 post-processing.
- [ ] P4.4 Implement stamped final video.
- [ ] P4.5 Add WorkManager state, retry, cancellation, and cleanup where needed.
- [ ] P4.6 Test silent recording, low storage, interruption, and playback.

## P5 — Collection

- [ ] P5.1 Implement DAO/repository queries for all filters and grouping.
- [ ] P5.2 Implement thumbnail generation/loading.
- [ ] P5.3 Implement screenshot-matched collection UI.
- [ ] P5.4 Implement search debounce and filter combinations.
- [ ] P5.5 Implement media detail.
- [ ] P5.6 Implement selection/share/delete.
- [ ] P5.7 Implement missing/failed/processing media states.
- [ ] P5.8 Performance-test large fixture dataset.

## P6 — Reporting

- [ ] P6.1 Implement report entities, DAOs, repositories, and migrations.
- [ ] P6.2 Implement new report creation and durable auto-save.
- [ ] P6.3 Implement collection attachment picker and ordering.
- [ ] P6.4 Implement GPS summary rule and refresh.
- [ ] P6.5 Implement reporting list, search, and status filters.
- [ ] P6.6 Implement report validation and status machine.
- [ ] P6.7 Implement PDF export worker and pagination.
- [ ] P6.8 Implement Save As and secure share.
- [ ] P6.9 Add export retry/version/stale behavior.
- [ ] P6.10 Verify PDFs with multilingual and large reports.

## P7 — Templates/settings

- [ ] P7.1 Implement built-in template domain configs.
- [ ] P7.2 Implement real preview renderer for Minimal, Classic, Detailed, Reporter.
- [ ] P7.3 Implement template selection persistence and camera update.
- [ ] P7.4 Implement settings screen.
- [ ] P7.5 Reuse language screen from settings.
- [ ] P7.6 Implement privacy/terms/share app/version.
- [ ] P7.7 Decide and implement or defer custom template editor.

## P8 — Quality and release

- [ ] P8.1 Complete unit/integration/Compose/instrumentation test coverage.
- [ ] P8.2 Run physical-device matrix.
- [ ] P8.3 Complete accessibility, font-scale, RTL, and adaptive audit.
- [ ] P8.4 Complete security/privacy audit.
- [ ] P8.5 Complete Macrobenchmark and Baseline Profile.
- [ ] P8.6 Verify R8 release build and app bundle.
- [ ] P8.7 Verify database migrations and upgrade path.
- [ ] P8.8 Prepare Play privacy/data safety and location disclosures.
- [ ] P8.9 Test signed AAB through internal track/bundletool.
- [ ] P8.10 Complete `DEFINITION_OF_DONE.md` evidence table.

## Evidence template

For each completed task append:

```text
Task:
Files changed:
Tests added:
Commands run:
Result:
Device/API verified:
Screenshot/reference checked:
Known residual risk:
```
