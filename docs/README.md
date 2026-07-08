# GeoSnap Android Engineering Pack

GeoSnap is a production Android camera application for capturing photos and videos with verifiable time and location context, organizing captured media, generating field reports, and applying timestamp/GPS templates.

This pack is designed for a coding agent working inside the real GeoSnap repository. It is not a mockup-only specification. Every implemented feature must use real Android APIs, persistent storage, lifecycle-safe state, explicit error handling, and automated tests.

## Product scope

The required user journey is:

`Splash → Language → Onboarding (3 pages) → Camera`

The persistent application shell contains four primary destinations:

1. Camera
2. Collection
3. Reporting
4. Templates

Settings is opened from the camera header and language can be changed later from Settings.

## Required implementation stack

- Kotlin
- Jetpack Compose and Material 3
- Single-activity architecture
- Navigation Compose
- CameraX for preview, image capture, and video recording
- Fused Location Provider behind a location abstraction
- Room as the local source of truth
- DataStore for preferences and onboarding state
- Hilt for dependency injection
- WorkManager for durable export and post-processing jobs
- MediaStore and Storage Access Framework for user-owned media and exported files
- AndroidX ExifInterface for photo metadata
- Media3 Transformer or CameraX effects for video watermark/export processing
- Coroutines and Flow
- JUnit, Turbine, Robolectric where appropriate, Compose UI testing, and instrumentation tests

## Documentation reading order

1. `CLAUDE.md`
2. `AGENTS.md`
3. `MASTER_PROMPT.md`
4. `ARCHITECTURE.md`
5. `SYSTEM_DESIGN.md`
6. `DESIGN_SYSTEM.md`
7. `UI_SCREEN_SPECS.md`
8. `CAMERA_GPS_PIPELINE.md`
9. `DATA_MODEL.md`
10. `TASKS.md`
11. `QA_TESTING.md`
12. `DEFINITION_OF_DONE.md`

## Stitch design source

Treat the repository directory `stitch_geosnap/` as read-only design input. Inspect every subdirectory, image, export, and generated layout before implementing the corresponding screen. Do not infer that a directory is unavailable merely because a screenshot only shows its name. The coding agent must inspect the local filesystem.

Expected folders visible in the provided directory screenshot include:

- `geosnap_splash_screen`
- `select_language` and `select_language_updated`
- `onboarding_step_1`, `onboarding_step_2`, `onboarding_step_3`
- enhanced onboarding visual folders
- `main_camera_interface`
- `collection_screen`
- `new_report_screen`
- `reporting_screen`
- `timestamp_templates` and enhanced previews
- `settings_screen`
- logo and photographic asset folders

Never modify or delete `stitch_geosnap/`. Copy required assets into Android resources using stable, descriptive names and document every mapping.

## Non-negotiable product rules

- No fake camera preview, fake captures, fake location, fake exports, or hardcoded report cards.
- No broad storage permission and no background location permission for the initial release.
- A capture remains valid when location is unavailable, but it must be clearly marked as “Location unavailable” rather than fabricated.
- UI must be screenshot-accurate while remaining responsive, accessible, localized, and usable on small and large Android phones.
- All critical actions must survive configuration change; durable actions must survive process death.
- Release builds must pass lint, tests, security checks, performance checks, and a physical-device camera/location test matrix.

## Deliverable definition

The completed app must build as a signed-ready Android App Bundle, run on a real Android device, capture and save real media, persist metadata, generate and share real PDF reports, support the specified languages, and match the supplied GeoSnap visual references.
