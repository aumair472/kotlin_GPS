# Full Master Prompt for Claude Code

You are the lead engineer for **GeoSnap**, a production Android app written in Kotlin and Jetpack Compose. Complete the real application in the current repository; do not create a mockup, fake backend, or hardcoded demo.

First, recursively inspect the repository and the read-only `stitch_geosnap/` directory. Build `docs/STITCH_ASSET_MAP.md` mapping every Stitch folder/export/image to the relevant Android route and resource. The supplied screenshots and Stitch exports are the visual source of truth. Implement responsive Compose UI that matches them closely without copying web/HTML code into Android.

Read and obey all root Markdown contracts, especially `CLAUDE.md`, `AGENTS.md`, `ARCHITECTURE.md`, `SYSTEM_DESIGN.md`, `DESIGN_SYSTEM.md`, `CAMERA_GPS_PIPELINE.md`, `SECURITY.md`, `PERFORMANCE.md`, `QA_TESTING.md`, `TASKS.md`, and `DEFINITION_OF_DONE.md`.

Required product flow:

- Branded splash with progress/version.
- First-launch language selection supporting English, Urdu, Arabic, Hindi, French, Spanish, Portuguese, German, Italian, Japanese, and Simplified Chinese, including RTL.
- Three swipeable onboarding pages with Skip, Next, indicators, and Get Started.
- Main shell with Camera, Collection, Reporting, and Templates bottom destinations.
- Settings from Camera.

Required real functionality:

- CameraX preview, photo capture, video recording, flash/torch, tap focus, pinch zoom, orientation, lens switch where supported, lifecycle cleanup, and typed error recovery.
- Foreground-only coarse/fine location with acquiring/precise/approximate/unavailable states. Capture real latitude, longitude, altitude/accuracy when available, instant, and timezone. Never hardcode or fabricate location.
- Apply selected timestamp/GPS template visibly to final photos and videos. Write supported EXIF GPS/time to photos. Use a durable, tested video overlay strategy such as CameraX OverlayEffect or Media3 Transformer with WorkManager processing state.
- Save finalized user media through MediaStore; keep temporary sources private. Persist metadata in Room and preferences in DataStore.
- Collection with real captures, date grouping, search, All/Today/This Week/Video/Photo filters, thumbnails, detail, selection, secure share, delete, and processing/missing/error states.
- Reporting list with Draft/Exported/Shared states. New/Edit Report must auto-save, attach/reorder collection media, use a deterministic GPS summary, validate, export a real multi-page PDF, Save As through SAF, and share through content URI/FileProvider.
- Templates: Minimal, Classic, Detailed, Reporter with real formatter previews and persistent selection.
- Settings: language, share app, privacy, terms, version.

Architecture:

- Single activity, Compose, Navigation Compose, ViewModel + immutable StateFlow/UDF, Hilt, repository boundaries, optional domain use cases, Room source of truth, DataStore preferences, WorkManager durable jobs, coroutines/Flow.
- No business/platform/storage logic in composables. No destructive migrations. No broad storage or background location permission. No secrets or exact private data in release logs.

Execution method:

1. Establish baseline build/tests and record existing failures.
2. Work through `TASKS.md` in dependency order, one vertical slice at a time.
3. For every task: understand → plan → implement → build → test → inspect generated output/UI → fix → rerun → document evidence.
4. Use real physical-device verification for camera, GPS, video, EXIF, MediaStore, PDF opening, and sharing. Compilation alone is not verification.
5. Add unit, Room migration, repository, Compose UI, WorkManager, instrumentation, screenshot, and Macrobenchmark/Baseline Profile coverage.
6. Compare implementation screenshots to references and fix structure, spacing, typography, colors, icons, RTL, accessibility, and adaptive behavior.
7. Never mark a task complete until acceptance evidence exists. Distinguish implemented, verified, and blocked.

Finish only when `DEFINITION_OF_DONE.md` is fully evidenced, lint/tests pass, no P0/P1 defects remain, the release build/AAB succeeds, permissions/privacy are Play-ready, and all critical journeys work on real devices.
