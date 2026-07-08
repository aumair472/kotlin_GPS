# GeoSnap Master Engineering Blueprint

This single file combines the core documentation pack. Individual files remain authoritative for editing and task execution.

---

## Included file: `README.md`

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

---

## Included file: `CLAUDE.md`

# Claude Code Operating Contract — GeoSnap

You are the senior Android engineer responsible for completing GeoSnap. Work as an implementation agent, not as a conversational adviser.

## Startup protocol

Before changing code:

1. Read `README.md`, `AGENTS.md`, `ARCHITECTURE.md`, `SYSTEM_DESIGN.md`, `TASKS.md`, `SECURITY.md`, `PERFORMANCE.md`, `QA_TESTING.md`, and `DEFINITION_OF_DONE.md`.
2. Inspect the complete repository tree.
3. Inspect `stitch_geosnap/` recursively and create or refresh `docs/STITCH_ASSET_MAP.md`.
4. Run the current build and tests to establish a baseline.
5. Record existing failures separately from failures introduced by your work.
6. Select the first unblocked task from `TASKS.md`.

## Execution rules

- Implement one coherent vertical slice at a time.
- Use production APIs and real persistence.
- Keep UI state immutable and event driven.
- Do not put business logic in composables.
- Do not access Room, DataStore, CameraX, location APIs, or file APIs directly from composables.
- Do not create duplicate navigation graphs, duplicate design tokens, duplicate repositories, or duplicate models.
- Do not suppress warnings without documenting the exact reason.
- Do not replace a failing implementation with a hardcoded demo.
- Do not claim completion until acceptance tests pass.
- Preserve working behavior while refactoring.

## Required loop for every task

1. **Understand** — read the task, related design screenshot, code, tests, and contracts.
2. **Plan** — list files to modify, interfaces involved, risks, and test cases.
3. **Implement** — make the smallest complete production change.
4. **Build** — compile the affected module and then the app.
5. **Test** — run unit tests, UI/instrumentation tests where applicable, and manual device checks for hardware features.
6. **Inspect** — review logs, lint output, database state, generated files, and screenshot comparison.
7. **Fix** — continue until the task passes; do not defer obvious defects.
8. **Document** — update task state, decision log, and any changed contract.
9. **Commit-ready summary** — state changed files, commands run, results, and remaining risks.

## Stop conditions

Stop and report a blocker only when one of these is true:

- A required secret, signing key, or external account is unavailable.
- A hardware-only behavior cannot be verified in the current environment.
- A design asset referenced by the repository is genuinely missing after recursive inspection.
- A product decision has two materially different outcomes not resolved by the documentation.

Even when blocked, complete all unblocked work and provide exact reproduction steps.

## Definition of honesty

“Implemented” means code exists and compiles.

“Verified” means the relevant automated test or real-device workflow passed.

“Pixel matched” means a captured implementation screenshot was compared against the reference and differences are within the tolerances in `DESIGN_SYSTEM.md`.

Never use these terms interchangeably.

---

## Included file: `AGENTS.md`

# Agent Roles and Responsibilities

The same coding agent may perform all roles, but each responsibility must be explicitly covered.

## 1. Lead Android architect

Owns module boundaries, dependency direction, navigation, state management, lifecycle behavior, and technical decisions. Prevents feature code from bypassing abstractions.

## 2. Camera and media engineer

Owns CameraX initialization, preview, image capture, recording, flash, lens switching, zoom, focus, orientation, MediaStore writes, thumbnails, EXIF, video post-processing, and low-storage behavior.

## 3. Location and integrity engineer

Owns foreground location acquisition, freshness/accuracy rules, permission handling, GPS snapshot creation, visible stamps, metadata consistency, and “location unavailable” behavior. Never invents coordinates.

## 4. Compose UI engineer

Owns screenshot-faithful layouts, adaptive sizing, system insets, accessibility semantics, focus behavior, animations, RTL layout, previews, and reusable design-system components.

## 5. Data engineer

Owns Room schema, migrations, transactions, search/filter queries, relationship integrity, DataStore preferences, and backup policy.

## 6. Reporting engineer

Owns report drafts, media attachment, validation, PDF generation, export state, share intents, FileProvider/SAF behavior, and recovery from interrupted work.

## 7. Security and privacy engineer

Owns least-privilege permissions, secure URI sharing, release configuration, backup exclusions, log redaction, dependency review, privacy disclosures, and Play policy readiness.

## 8. QA engineer

Owns unit, integration, Compose, instrumentation, migration, screenshot, performance, and physical-device testing. A task cannot close without evidence.

## 9. Performance engineer

Owns startup, camera readiness, frame/jank behavior, memory use, thumbnail loading, report export time, battery usage, Baseline Profiles, and Macrobenchmark.

## 10. Context engineer

Maintains a small, accurate working context. Reads authoritative files first, records decisions, avoids stale assumptions, and links each implementation change to a product requirement.

## 11. Loop engineer

Enforces the understand → implement → test → inspect → fix cycle. Detects false completion, skipped checks, and regressions.

## Cross-agent handoff format

Every handoff must contain:

- Goal and task ID
- Relevant contracts and screenshots
- Files changed
- Public interfaces changed
- Tests added and commands run
- Observed results
- Known limitations or unverified hardware behavior
- Next task and prerequisites

## Forbidden agent behavior

- Generating placeholder screens and calling the feature complete
- Using sample images as captured camera output
- Hardcoding Karachi coordinates or sample report data
- Requesting permissions at app launch without context
- Adding background location “just in case”
- Using `MANAGE_EXTERNAL_STORAGE`
- Loading full-resolution images into collection grids
- Exporting `file://` URIs
- Writing secrets into source control
- Editing design reference files to make comparison easier

---

## Included file: `ARCHITECTURE.md`

# GeoSnap Architecture

## Architectural style

GeoSnap uses a single-activity, multi-module, offline-first architecture with unidirectional data flow.

- **UI layer:** Compose screens, stateless components, screen ViewModels, immutable `UiState`, user `Action` events, one-off `Effect` events.
- **Domain layer:** use cases for capture, stamping, filtering, report creation, export, and settings. Domain is included where logic is reused or non-trivial.
- **Data layer:** repositories coordinating Room, DataStore, CameraX/media gateways, location gateway, file/export services, and optional future network sources.
- **Source of truth:** Room for media/report/template records; DataStore for small preferences.

## Recommended module graph

```text
:app
:core:common
:core:model
:core:designsystem
:core:navigation
:core:database
:core:datastore
:core:media
:core:location
:core:files
:core:testing
:feature:splash
:feature:language
:feature:onboarding
:feature:camera
:feature:collection
:feature:reporting
:feature:templates
:feature:settings
:benchmark
```

For an initially small repository, modules may be consolidated, but package boundaries and dependency rules must remain equivalent. Do not create modules solely for appearance if they slow iteration without protecting a boundary.

## Dependency rules

```text
feature:* -> core:model, core:designsystem, core:navigation, domain contracts
core media/location/files -> platform APIs
core database/datastore -> persistence APIs
app -> all feature entry points and DI wiring
```

Forbidden dependencies:

- Core modules cannot depend on feature modules.
- Database entities cannot be exposed directly to UI.
- Composables cannot depend directly on DAOs, CameraX classes, location clients, or Android file streams.
- Feature ViewModels cannot own long-lived Android resources such as camera providers or open file descriptors.

## State management

Each screen exposes:

```kotlin
data class ScreenUiState(...)
sealed interface ScreenAction
sealed interface ScreenEffect
```

The ViewModel exposes `StateFlow<ScreenUiState>` and accepts `onAction(action)`. One-time navigation, snackbar, picker, and share operations use an effect stream or a clearly scoped event mechanism.

Use `SavedStateHandle` for navigation arguments and small transient restoration. Persist drafts and meaningful user work in Room immediately; do not rely on ViewModel memory.

## Navigation

Use typed routes where supported. Root graph:

```text
StartupGraph
  Splash
  Language
  Onboarding
MainGraph
  Camera
  Collection
  Reporting
  Templates
Secondary
  NewReport(reportId?)
  ReportDetail(reportId)
  MediaDetail(mediaId)
  TemplateEditor(templateId?)
  Settings
  LanguageSettings
  PrivacyPolicy
  Terms
```

The bottom bar is visible only on the four main destinations. It is hidden on onboarding, settings, editors, detail screens, system pickers, and full-screen media preview.

## Offline-first behavior

All core functionality works without a network connection:

- capture photo/video
- store GPS/time metadata
- browse and filter collection
- create and edit report drafts
- export PDF locally
- share local files
- choose language and templates

A future cloud sync feature must be additive and must not replace the local source of truth.

## Dependency injection

Use Hilt for application-scoped repositories and gateway implementations. Scope camera session objects to the camera feature lifecycle rather than the application. Use assisted or factory creation for jobs requiring runtime IDs/URIs.

## Concurrency

- Main dispatcher: UI state and platform calls requiring main thread.
- IO dispatcher: Room/file operations and metadata reads.
- Default dispatcher: CPU-heavy image composition calculations.
- Media codecs/export APIs: use their supported callbacks/executors.
- Never decode, stamp, export, or query a large collection on the main thread.

## Error model

Use typed domain failures, for example:

```text
PermissionDenied
CameraUnavailable
CaptureFailed
LocationUnavailable
InsufficientStorage
MediaWriteFailed
MetadataWriteFailed
ExportFailed
InvalidReport
FileShareFailed
```

Convert failures to user-safe messages at the UI boundary. Log technical details without coordinates, notes, filenames, or other private content in release builds.

## Extensibility

Interfaces must allow later additions such as cloud backup, organization accounts, digital signatures, custom branding, map views, and report synchronization without rewriting the capture pipeline.

---

## Included file: `SYSTEM_DESIGN.md`

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

---

## Included file: `CODEBASE_STRUCTURE.md`

# Suggested Codebase Structure

```text
GeoSnap/
├── app/
│   └── src/main/java/.../GeoSnapApplication.kt, MainActivity.kt
├── core/
│   ├── common/          Result types, dispatchers, utilities
│   ├── model/           Domain value objects
│   ├── designsystem/    Theme and reusable Compose components
│   ├── navigation/      Typed routes and navigator
│   ├── database/        Room entities, DAO, migrations
│   ├── datastore/       Preferences and locale state
│   ├── media/           CameraX, MediaStore, EXIF, thumbnails, video processing
│   ├── location/        Location gateway and geocoder
│   ├── files/           PDF, SAF, FileProvider, checksums
│   └── testing/         Fakes, fixture builders, test dispatchers
├── feature/
│   ├── splash/
│   ├── language/
│   ├── onboarding/
│   ├── camera/
│   ├── collection/
│   ├── reporting/
│   ├── templates/
│   └── settings/
├── benchmark/
├── docs/
├── scripts/
├── stitch_geosnap/      read-only design source
├── AGENTS.md
├── CLAUDE.md
└── TASKS.md
```

## Feature package pattern

```text
feature/camera/
├── CameraRoute.kt
├── CameraScreen.kt
├── CameraUiState.kt
├── CameraAction.kt
├── CameraEffect.kt
├── CameraViewModel.kt
├── components/
├── navigation/
└── test/
```

Platform-heavy implementations belong in core media/location, not in feature packages. Features depend on interfaces/use cases and domain models.

## Naming

- `Entity` only for Room storage model.
- `Dto` only for external/network representations.
- Domain types have plain names, e.g. `MediaItem`, `Report`.
- Use value classes for IDs where practical.
- Avoid generic names such as `Utils`, `Helper`, `Manager` unless the responsibility is truly a lifecycle coordinator; prefer precise names.

## Resource naming

```text
ic_geosnap_logo
img_onboarding_gps_stamp
img_onboarding_realtime_location
img_onboarding_reports
```

Use lower snake case and document source mapping. Do not use the long generated Stitch folder prompt names as final Android resource names.

---

## Included file: `BUILD_SETUP.md`

# Build Setup Blueprint

## Baseline configuration

- JDK compatible with selected stable AGP.
- `compileSdk`/`targetSdk` at current Play requirement or higher after compatibility testing.
- Recommended `minSdk` 26 for this product unless business requirements demand broader support; document the decision.
- Kotlin JVM target/toolchain consistent across modules.
- Compose compiler/tooling aligned through the official BOM/toolchain.
- Version catalog for all dependencies/plugins.

Current research baseline in June 2026 found stable CameraX 1.6.1, AndroidX ExifInterface 1.4.2, and Media3 Transformer examples using 1.10.1. Resolve and lock a compatible stable set in the actual project rather than mixing arbitrary alpha versions.

## Core dependencies

- Compose BOM, UI, Material3, tooling preview
- lifecycle-runtime-compose, ViewModel Compose
- Navigation Compose
- Hilt Android + Compose navigation integration
- Room runtime/ktx/compiler
- DataStore Preferences or Proto
- WorkManager KTX + Hilt integration
- CameraX core/camera2/lifecycle/video/view or compose/effects
- Play services location if using FusedLocationProviderClient
- ExifInterface
- Media3 Transformer/effect/common and ExoPlayer for preview if needed
- Paging Compose if dataset warrants it
- image loader with content URI support
- testing libraries

## Build quality

Enable:

- Compose build feature;
- BuildConfig only where needed;
- Room schema export;
- lint warnings as errors selectively after baseline cleanup;
- release R8/resource shrinking;
- reproducible dependency versions;
- packaging exclusions only when documented.

## Manifest baseline

- single exported launcher activity;
- non-exported providers/services/workers as appropriate;
- FileProvider restricted paths;
- `supportsRtl=true`;
- camera feature declaration;
- permissions only from `PERMISSIONS_PRIVACY.md`;
- backup/data extraction rules;
- no cleartext traffic.

## Verification

After setup:

```bash
./gradlew clean assembleDebug test lint
```

Then install on a device and confirm launch before adding feature complexity.

---

## Included file: `DESIGN_SYSTEM.md`

# GeoSnap Design System

The supplied Stitch assets and screenshots are the visual source of truth. The values below are an implementation baseline extracted from the references and must be refined through screenshot comparison against the actual local exports.

## Visual character

- Clean white field-documentation interface
- Strong royal-blue primary actions
- Near-black headings
- Cool gray secondary text and surfaces
- Rounded cards and buttons
- Thin neutral borders
- Minimal shadows
- Photography is the main visual content

## Base tokens

```kotlin
object GeoSnapColors {
    val Primary = Color(0xFF2B67E8)
    val PrimaryPressed = Color(0xFF1F54C9)
    val PrimaryContainer = Color(0xFFEAF2FF)
    val Background = Color(0xFFFFFFFF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF4F5F8)
    val TextPrimary = Color(0xFF0B0D1C)
    val TextSecondary = Color(0xFF687084)
    val Border = Color(0xFFD4D8E3)
    val Success = Color(0xFF12B76A)
    val SuccessContainer = Color(0xFFD1FAE5)
    val Warning = Color(0xFFF59E0B)
    val WarningContainer = Color(0xFFFFF1C2)
    val Error = Color(0xFFD92D20)
    val Scrim = Color(0x99000000)
}
```

Do not introduce gradients unless present in a supplied asset. Do not enable dynamic color because it would break visual parity.

## Typography

Use a single sans-serif family matching the Stitch export. Prefer an included, properly licensed font asset; otherwise use Android's default sans/Roboto. Never download or embed an unlicensed font.

Suggested scale:

- Display logo wordmark: asset, not plain text where available
- Screen title: 28–32sp, bold
- Onboarding title: 32–38sp, bold, centered
- Section title: 20–24sp, semibold
- Card title: 18–22sp, semibold/bold
- Body: 16–18sp
- Supporting text: 14–16sp
- Label/caption: 12–14sp

Respect system font scaling. At 200% font size, essential actions must remain reachable and text must not overlap.

## Spacing and shape

Use an 8dp base grid with 4dp exceptions.

- Screen horizontal padding: 24dp on compact phones, adaptive on larger width
- Card internal padding: 20–24dp
- Vertical section gap: 24–32dp
- Small element gap: 8–12dp
- Standard card radius: 16dp
- Hero image radius: 16–20dp
- Primary button radius: 16–28dp depending on reference
- Filter chip radius: 14–18dp
- Minimum interactive target: 48dp

## Core components

Implement reusable components in `core:designsystem`:

- `GeoSnapTopBar`
- `GeoSnapBottomBar`
- `GeoSnapPrimaryButton`
- `GeoSnapOutlinedCard`
- `GeoSnapFilterChip`
- `GeoSnapSearchField`
- `GeoSnapRadioRow`
- `GeoSnapStatusBadge`
- `GeoSnapEmptyState`
- `GeoSnapPermissionRationale`
- `GeoSnapGpsStatus`
- `GeoSnapMediaThumbnail`
- `GeoSnapLoadingOverlay`
- `GeoSnapErrorBanner`

Do not create screen-local copies of the same button, chip, card, or top bar.

## Camera-specific layout

The preview occupies the main available region edge to edge under a dark header. The metadata overlay is semi-transparent and readable over light or dark imagery. The lower control panel is white with rounded top corners and includes mode tabs, gallery thumbnail, shutter/record control, and secondary camera controls.

## Bottom navigation

- Four equal items: Camera, Collection, Reporting, Templates
- Selected icon/text use primary blue and a small indicator where shown
- Unselected items use dark cool gray
- Background is white with a subtle top divider
- Labels remain visible
- Preserve item order across every screen

## Screenshot matching process

For each screen:

1. Render at the reference aspect ratio and at one smaller/larger device.
2. Capture a deterministic screenshot with stable sample data.
3. Overlay or diff against the supplied reference.
4. Correct structure first, then spacing, typography, color, and icon alignment.
5. Acceptable tolerance: no structural mismatch; generally within 2dp for key alignment and 4dp for non-critical spacing. Text wrapping must match semantically even when font metrics differ slightly.

## Adaptive and accessibility rules

- Use `WindowInsets` and edge-to-edge correctly.
- Use `start`/`end`, never left/right, for RTL support.
- Use `LazyColumn`/`LazyVerticalGrid` for long content.
- Collection grid adapts column count based on width while preserving square thumbnails.
- Do not rely on color alone for status or selection.
- All icons need content descriptions unless decorative.
- Camera shutter, record, stop, flash, lens switch, and back actions require explicit accessibility labels.

---

## Included file: `UI_SCREEN_SPECS.md`

# UI Screen Specifications

## 1. Splash

Reference: `reference_screenshots/01_splash.png` and Stitch folder `geosnap_splash_screen`.

Required behavior:

- White full-screen background.
- Centered circular GeoSnap camera/location logo, product name, and tagline.
- Bottom progress track, animated primary progress, and version text.
- Use Android 12+ SplashScreen API for process launch, then the branded composable screen.
- Read language/onboarding preferences and initialize required local services.
- No arbitrary multi-second wait. Apply a short minimum visual duration only to avoid a flash.
- Navigate exactly once and remove Splash from the back stack.

## 2. Language selection

References: `02_language.png` and `11_language_extended.png`.

Languages for the initial UI:

- English `en`
- Urdu `ur` — RTL
- Arabic `ar` — RTL
- Hindi `hi`
- French `fr`
- Spanish `es`
- Portuguese `pt`
- German `de`
- Italian `it`
- Japanese `ja`
- Simplified Chinese `zh-CN`

Behavior:

- Header with close/back icon and title.
- Optional logo/intro section on first launch; settings variant may omit it.
- Single-select bordered language cards.
- Selected card uses primary border, light-blue background, and selected radio.
- Continue is disabled only when no selection exists.
- Apply app locale through supported per-app locale APIs and persist selection.
- UI changes immediately and handles RTL without restart loops.

## 3. Onboarding pager

References: `03`, `04`, and `05` onboarding screenshots.

Pages:

1. Stamp every photo with GPS.
2. Real-time location and time data.
3. Generate reports instantly.

Behavior:

- Horizontal swipe in both directions.
- Pager indicator follows current page.
- Skip goes to the final app entry flow and marks onboarding complete.
- Next advances one page; final button is “Get Started”.
- Back gesture/page handling is predictable.
- Page state survives rotation.
- Do not request permissions inside a swipe page. Ask in context when entering the camera or through a dedicated rationale step if added.

## 4. Camera

Reference: `06_camera.png` and Stitch `main_camera_interface`.

Required content:

- Dark top header with current coordinate summary, GeoSnap title, and Settings icon.
- Full camera preview.
- Focus framing guides.
- Visible timestamp/location overlay preview.
- White bottom control sheet with four main navigation destinations.
- Photo and Video mode controls.
- Latest thumbnail, shutter/record control, and secondary control.

Required behavior:

- Real rear camera preview; optional front lens switch if supported.
- Photo capture and video recording.
- Tap-to-focus, pinch-to-zoom, flash/torch state, orientation handling.
- Real location/altitude/accuracy state; no hardcoded values.
- Location indicator states: acquiring, accurate, approximate, unavailable, disabled.
- Capture works when location is unavailable after clear user feedback.
- Selected timestamp template is previewed and applied to final output.
- Prevent double capture while finalizing.
- Recording shows duration, audio state, pause/resume only if implemented reliably, and stop.
- Camera resources release when destination/lifecycle stops.

## 5. Collection

Reference: `07_collection.png`.

Required content:

- Title and back/up/filter action as in reference.
- Search by location/date.
- Filter chips: All, Today, This Week, Videos, Photos.
- Date-sectioned media grid.
- Video badge, location label overlay, missing/processing state.
- Persistent bottom navigation.

Required behavior:

- Data is queried from Room, not hardcoded.
- Search is debounced and cancellable.
- Filters combine correctly.
- New captures appear reactively.
- Tap opens media detail; long press enters selection mode.
- Share/delete actions use real files and confirmation.
- Failed/broken URI cards offer repair/remove rather than showing a generic broken image forever.

## 6. Reporting list

Reference: `09_reporting.png`.

Required content:

- Top bar title and plus action.
- Report search field.
- Status chips: All Reports, Draft, Exported, Shared.
- Cards with title, location, date/time, preview thumbnails, attachment count, status badge, share action.
- Persistent bottom navigation.

Behavior:

- Plus creates a persistent draft and navigates to New Report.
- Search and status filters query Room.
- Share is enabled only when an export exists; otherwise prompt export.
- Status reflects real lifecycle events.

## 7. New/Edit Report

Reference: `08_new_report.png`.

Fields:

- Report title
- Location with refresh
- Date
- Time
- Notes/description
- Attached media
- GPS summary

Behavior:

- Save Draft persists immediately.
- Add photos opens an in-app collection picker; optional system picker may import external media only through user selection.
- GPS card derives from report location or selected media according to a documented rule.
- Validate title and at least one attachment before final submission unless product requirements later change.
- Submit creates/updates report and offers export.
- Process death restores draft by ID.

## 8. Timestamp Templates

Reference: `10_templates.png`.

Required content:

- Top bar with title and plus action.
- Filter chips: All, Minimal, Detailed, Classic.
- Template cards with preview, title, description, and radio/check selection.
- Built-ins: Minimal, Classic, Detailed, Reporter.
- Persistent bottom navigation.

Behavior:

- Selecting a card updates DataStore and camera preview.
- Preview uses real formatter logic, not a static screenshot.
- Plus opens a custom template editor only after built-ins are complete; it may be feature-flagged for a later phase.

## 9. Settings

Reference: `12_settings.png`.

Required rows:

- Language
- Share App
- Privacy Policy
- Terms & Conditions
- App logo, product name, and version

Behavior:

- Language opens the extended selection screen.
- Share App uses a chooser and does not crash before the Play listing exists; use a configurable URL.
- Privacy and terms open local content or secure HTTPS URLs.
- Version comes from build configuration.
- Settings is not a bottom-navigation destination; the reference bottom bar shown in the mockup must not create inconsistent navigation state. When opened from Camera, either retain the shell correctly or hide it consistently according to the final Stitch flow.

## Common states required on every data screen

- Loading
- Empty
- Content
- Recoverable error with retry
- Permission-required state where relevant
- Offline state only if a future network feature exists
- Accessibility and RTL layouts

---

## Included file: `NAVIGATION.md`

# Navigation Contract

## Startup decision

```kotlin
sealed interface StartupDestination {
    data object Language : StartupDestination
    data object Onboarding : StartupDestination
    data object Camera : StartupDestination
}
```

Decision order:

1. If no language has ever been confirmed, open Language.
2. Else if onboarding is incomplete, open Onboarding.
3. Else open Camera.

## Main destinations

Stable route IDs:

```text
camera
collection
reporting
templates
```

The bottom bar must use `launchSingleTop`, restore state, and pop to the main graph start destination so repeated taps do not create duplicate destinations.

## Secondary routes

```text
settings
language-settings
media/{mediaId}
report/{reportId}
report/{reportId}/edit
template/{templateId}/edit
legal/privacy
legal/terms
```

Use encoded IDs, not file paths or raw URIs, as route arguments.

## Back behavior

- Splash: no back navigation.
- First-launch Language: close/back exits only if product explicitly permits; otherwise it stays until selection.
- Onboarding: system back moves to the previous page before leaving the flow.
- Main destinations: back follows Android navigation expectations; Camera is the main start.
- Editors: prompt before discarding unsaved changes, though report changes should normally auto-save.
- System picker/share sheet: return to the originating screen without duplicate navigation.

## Deep links

Not required for MVP. If added, deep links must validate IDs, never accept arbitrary file URIs, and route through repository lookups.

## Navigation tests

Automate:

- first launch path;
- returning-user path;
- language change from Settings;
- onboarding skip and completion;
- bottom-bar state restoration;
- plus → new report → back;
- process recreation on report edit;
- invalid route ID handling.

---

## Included file: `FEATURES.md`

# Functional Feature Contract

## Camera controls

- Initialize rear camera with lifecycle binding.
- Switch lens when more than one appropriate camera exists.
- Capture JPEG photo.
- Record MP4 video with optional microphone audio.
- Flash modes for photo: off, on, auto where supported.
- Torch for video/preview where supported.
- Tap-to-focus and focus feedback.
- Pinch-to-zoom with bounded ratio.
- Correct output rotation.
- Prevent concurrent capture/finalization conflicts.
- Show errors and recovery actions.

## GPS/time capture

- Foreground precise location request only when camera is being used and user consent exists.
- Continue with approximate location if that is all the user grants.
- Display latitude, longitude, optional altitude, accuracy, timestamp, and timezone.
- Store capture-time instant and location snapshot.
- Resolve address asynchronously and optionally.
- Mark stale or unavailable location clearly.

## Media stamping

- Template preview in camera.
- Visible overlay burned into photos.
- GPS/time metadata written into supported EXIF tags after image processing.
- Video overlay included in the finalized video using CameraX effect or Media3 Transformer; if post-processed, expose processing status.
- Preserve an unformatted machine-readable metadata record in Room.
- Stamp formatter supports locale-aware date/time and coordinate formatting.

## Collection

- Reactive grouped grid.
- Search by address text, coordinate string, report tag, or date text as defined by indexed query strategy.
- Filter by today, current week, photo, video, processing, and favorites if later added.
- View, select, share, and delete media.
- Deletion coordinates MediaStore and Room safely.

## Reports

- Create, edit, auto-save, duplicate, delete.
- Attach/reorder media.
- Add title, notes, report location, date/time.
- Save draft.
- Validate and export PDF.
- Track export versions.
- Share exported file through Android chooser.
- Statuses: Draft, Exported, Shared; maintain an event history rather than relying only on a single mutable label.

## Templates

- Minimal: date/time.
- Classic: date/time and coordinates.
- Detailed: full date/time, coordinates, altitude, and address.
- Reporter: GeoSnap/site report branding plus core metadata.
- Persist selected template.
- Format safely when altitude/address is absent.

## Localization

- All user-facing strings are resources.
- Per-app language preference.
- RTL for Arabic and Urdu.
- Locale-aware dates, times, numerals where appropriate, and translated plurals.
- User-entered report content is never machine-translated automatically.

## Settings/legal

- Change language.
- Share app.
- Open privacy policy and terms.
- Show app version.
- Future-safe entries for units, timestamp format, default template, and capture quality may be added only when designed and tested.

---

## Included file: `CAMERA_GPS_PIPELINE.md`

# Camera, GPS, and Stamp Pipeline

## CameraX use cases

Bind the smallest compatible set needed for the current mode:

- `Preview`
- `ImageCapture` in Photo mode
- `VideoCapture<Recorder>` in Video mode
- optional `ImageAnalysis` only if a real feature requires it

Avoid keeping expensive use cases bound without purpose. Query device capabilities and apply a fallback quality strategy.

## Compose integration

Use the stable CameraX Compose/viewfinder path available in the repository toolchain. Encapsulate it behind `CameraPreviewHost` so a tested `PreviewView` fallback can be used on problematic devices.

The UI layer owns only rendering and gestures. A lifecycle-aware camera controller/session object owns binding and controls.

## Capture-time location policy

A location is acceptable for stamping when:

- it was produced recently, default target ≤10 seconds old;
- its accuracy is within a configurable threshold, default target ≤25 m for a “verified” badge;
- it is not a mocked location when integrity mode rejects mocks;
- the user has granted available foreground permission.

If the cached sample is stale or inaccurate, request a current high-accuracy location with a bounded timeout. Do not block the shutter indefinitely. Persist the exact age and accuracy so the report can distinguish high-confidence and low-confidence captures.

Suggested domain model:

```kotlin
data class GeoSnapshot(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val horizontalAccuracyMeters: Float?,
    val providerTimestamp: Instant,
    val capturedAt: Instant,
    val timezoneId: String,
    val isApproximate: Boolean,
    val isMock: Boolean,
    val provider: String?,
    val address: PostalAddress?
)
```

## Photo finalization

Recommended robust path:

1. CameraX writes to an app-owned temporary JPEG.
2. Read dimensions/orientation without decoding full bitmap.
3. Decode a bounded mutable bitmap at final required resolution.
4. Normalize orientation.
5. Draw the selected stamp using `Canvas` and density-independent layout calculations.
6. Compress to a pending MediaStore item.
7. Open final output with AndroidX `ExifInterface` and write supported datetime/GPS tags.
8. Mark MediaStore item no longer pending.
9. Persist record in Room.
10. Delete temporary input.

If a device/API combination supports a direct effect that preserves quality and metadata, it may be used after verification. Never write metadata before recompression because it may be lost.

## Visible overlay rules

- Use safe margins relative to final media dimensions.
- Scale typography and padding by output size, not screen dp.
- Use a semi-transparent dark/light container chosen by template.
- Wrap or omit optional address to avoid clipping.
- Include only real available fields.
- Store template ID, template version, and rendered text in metadata for auditability.

## Video finalization

Two acceptable production strategies:

### A. CameraX OverlayEffect

Apply a CameraX effect to preview and recording frames so the resulting recording already contains the overlay. Verify support across the selected CameraX version and test devices.

### B. Media3 Transformer post-process

Record a clean source, then enqueue a durable job that adds text/bitmap overlay and exports a finalized MP4. Persist:

- source URI/path;
- processing state and progress;
- finalized URI;
- failure reason and retry count.

Collection shows a processing card until finalization succeeds. The raw source remains private and is deleted only after a verified final output exists.

## MediaStore naming

Use sanitized, collision-resistant names, for example:

```text
GeoSnap_20260617_143205_<short-id>.jpg
GeoSnap_20260617_143205_<short-id>.mp4
```

Store display names separately from report titles. Never derive a filesystem path from user-entered report text.

## Audio

Request `RECORD_AUDIO` only when the user starts a video mode where audio is enabled. If denied, offer silent recording rather than blocking all video capture.

## Location/address resolution

Reverse geocoding is optional and asynchronous. Captures must not fail because an address cannot be resolved. Cache normalized address fields in the database and allow “Coordinates only”.

## Integrity limitations

GPS and device clocks can be manipulated on consumer devices. GeoSnap can record evidence and consistency signals but must not claim cryptographic proof of physical presence without a server-backed attestation/signature design. Product copy must say “GPS-tagged” or “location recorded,” not “legally impossible to alter.”

---

## Included file: `DATA_MODEL.md`

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

---

## Included file: `DATABASE.md`

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

---

## Included file: `PERMISSIONS_PRIVACY.md`

# Permissions and Privacy Design

## Manifest permissions for MVP

Required or conditional:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Declare camera hardware appropriately with `uses-feature`; decide whether it is required based on distribution strategy.

Do not request:

- `ACCESS_BACKGROUND_LOCATION`
- `MANAGE_EXTERNAL_STORAGE`
- legacy write storage permissions on modern targets
- notification permission unless a real notification feature requires it

## Runtime request sequence

### Camera

Ask when the user enters Camera and attempts to use preview. Precede the system dialog with a concise in-app rationale explaining that the camera is needed to capture field evidence.

### Location

Ask in context after camera access is available and before enabling GPS stamping. Request coarse and fine together according to platform guidance. The app must function with approximate location and must handle the process restart that can occur when precise access is downgraded.

### Microphone

Ask only when the user starts video recording with audio enabled. On denial, offer silent recording.

## Denial handling

- First denial: show rationale and retry action.
- “Don’t ask again”: show Settings action and a no-permission mode where possible.
- Location denied: camera still captures with explicit “No GPS” status.
- Camera denied: collection/reporting/templates/settings remain accessible.
- Permission revoked while app is paused: re-check on resume and stop affected hardware safely.

## Storage behavior

Write app-created photos/videos with MediaStore. The app can access its own media without broad media-read permission on modern Android. To attach media created by other apps, use Android Photo Picker or Storage Access Framework and persist only user-granted URI access where supported.

## Privacy principles

- Default to local-only processing.
- Do not upload coordinates, media, report notes, or identifiers in MVP.
- Do not log exact coordinates or user notes in production analytics/crash breadcrumbs.
- Explain visible stamps and embedded metadata.
- Provide deletion behavior and clarify exported/shared copies are outside app control.
- Include a privacy policy in app and Play listing before release.

## User-facing disclosure

Before the location permission prompt, disclose:

- location is used while the camera screen is active or at capture time;
- coordinates, time, altitude/accuracy when available may be stored with media and reports;
- stamped/exported/shared files may reveal location to recipients;
- the app does not need background location for MVP.

## Play policy stance

Foreground-only capture-time location is the intended scope. Any future background tracking feature requires a separate product justification, prominent disclosure, Play declaration, and policy review; it must not be added silently.

---

## Included file: `SECURITY.md`

# Security Engineering Plan

## Threat model

Protect against:

- unauthorized reading of private drafts or source media;
- malicious or malformed external URIs;
- path traversal or unsafe filenames;
- leaked secrets/signing configuration;
- insecure file sharing;
- accidental location disclosure in logs or analytics;
- database corruption or destructive migration;
- tampered or missing media referenced by reports;
- cleartext network traffic if network features are added;
- exported Android components being invoked unexpectedly.

GeoSnap cannot fully prevent a device owner with root access or clock/location spoofing from manipulating evidence. Document this limitation honestly.

## Storage security

- Keep intermediate media and thumbnails in app-private storage.
- Use MediaStore only for user-visible finalized media according to product settings.
- Exclude sensitive private caches/drafts from backup when appropriate through data extraction rules.
- If encryption at rest is added for sensitive private files, keep keys in Android Keystore and use supported cryptography; never hardcode keys.
- Validate every persisted URI before use and handle revoked grants.

## Sharing

- Use `content://` URIs.
- Use `FileProvider` for app-private exports.
- Grant temporary read permission through the share intent.
- Never expose internal paths or `file://` URIs.
- Restrict provider paths to the exact export directory.
- Use Android chooser for user control.

## Components and intents

- Set `android:exported="false"` unless a component intentionally needs external access.
- Validate all incoming intents and MIME types.
- Do not accept arbitrary output paths from intents.
- Avoid mutable PendingIntents; if later required, choose explicit mutability.

## Network

MVP should not require a backend. If HTTPS links or future APIs are used:

- disable cleartext traffic;
- use Network Security Configuration;
- keep secrets on a server, not in the APK;
- apply timeouts, certificate validation, and safe error handling;
- never pin certificates without an operational rotation plan.

## Input validation

- Bound report title and notes lengths.
- Normalize control characters in filenames and PDF text.
- Validate coordinate ranges, dates, enum values, and media MIME types.
- Reject unexpectedly huge imported files before decoding.
- Decode images with sampled bounds to avoid memory exhaustion.

## Database

- Use foreign keys and transactions.
- Test migrations.
- Do not enable destructive migration in release.
- Parameterize all queries through Room.
- Treat database content as untrusted when rendering/exporting.

## Logging

Release logs must redact:

- exact latitude/longitude;
- addresses;
- report notes/titles when potentially sensitive;
- content URIs and local paths;
- filenames containing user text;
- signing data, tokens, and device identifiers.

Use event codes and coarse diagnostics instead.

## Supply chain

- Use version catalogs.
- Prefer stable AndroidX/Google libraries.
- Enable dependency verification/locking where practical.
- Run dependency vulnerability review before release.
- Remove unused libraries and debug tooling.

## Build security

- Keep upload keystore outside source control.
- Load signing values from local/CI secrets.
- Enable R8 minification and resource shrinking for release after tests pass.
- Verify release manifest, exported components, backup configuration, and network policy.
- Produce SBOM/dependency report if the organization requires it.

## Security acceptance tests

- Share URI works for recipient and expires with temporary grant semantics.
- No internal path appears in logs or intents.
- All components have intentional export state.
- Cleartext traffic is rejected.
- Imported malformed URI/file fails safely.
- Revoked permission/URI access produces recoverable UI.
- Release APK/AAB contains no test credentials or local absolute paths.

---

## Included file: `PERFORMANCE.md`

# Performance and Reliability Plan

## Performance goals

Use measured baselines rather than guesses. Initial targets on a representative mid-range device:

- Cold startup to first usable non-camera screen: target <1.5 s where hardware permits.
- Returning launch to camera shell: target <2.0 s; camera stream readiness measured separately.
- Camera control response: no main-thread blocking.
- Photo shutter feedback: immediate; finalization completes in background with visible state.
- Collection scrolling: smooth with thumbnails, not full-resolution media.
- Search/filter update: target <300 ms for expected local dataset.
- Report export: progress shown; no ANR; bounded memory.
- Crash-free capture/export flows across device matrix.

These are engineering targets, not guarantees; record actual benchmark hardware and results.

## Startup

- Use Android splash API.
- Initialize only essential preferences/database on startup.
- Lazy initialize camera/location/media exporters.
- Avoid reading the entire media database before first frame.
- Add Startup and Baseline Profiles covering startup and critical journeys.

## Camera

- Bind/unbind with lifecycle.
- Reuse executors responsibly and shut them down.
- Avoid per-frame allocations.
- Do not run reverse geocoding on every location update.
- Throttle coordinate UI updates while retaining the best latest snapshot.
- Select sensible resolution/quality with device fallback.

## Images

- Query image bounds before decode.
- Generate and cache small thumbnails.
- Use an image loader with size constraints and content URI support.
- Never hold multiple full-resolution bitmaps in a collection/report list.
- Recycle/release temporary resources through structured scopes.

## Video

- Do not transcode on main thread.
- Use WorkManager for durable post-processing.
- Expose progress and cancellation.
- Check free storage before record/export.
- Preserve raw source until final output validation.
- Test thermal and long-recording behavior.

## Database

- Add indexes for filter/sort columns.
- Use paging or bounded queries for large collections.
- Avoid N+1 relation queries.
- Use Flow distinctness and debounced search.
- Profile queries with realistic 1k, 10k, and larger media metadata fixtures.

## Compose

- Keep state stable/immutable.
- Provide keys to lazy lists/grids.
- Avoid reading rapidly changing camera/location state high in the entire composition tree.
- Isolate timer/recording updates.
- Use derived state and memoization where measured.
- Test jank using Macrobenchmark and traces rather than premature micro-optimization.

## Battery

- No background location in MVP.
- Stop high-accuracy updates when camera is not active.
- Do not keep camera, microphone, or wake locks after lifecycle stop.
- Constrain non-urgent WorkManager tasks.
- Avoid repeated media rescans.

## Reliability cases

Test and recover from:

- low storage during capture/export;
- camera disconnect/in-use by another app;
- location provider disabled;
- permission revoked mid-session;
- process death during report edit/video processing;
- corrupted/missing media URI;
- device rotation and multi-window;
- app update with database migration;
- long collection and report lists.

## Benchmark suite

Macrobenchmark critical journeys:

1. Cold startup to Camera shell.
2. Navigate Camera → Collection and scroll.
3. Navigate Reporting and scroll cards.
4. Open Templates and select a template.
5. Open existing report draft.

Hardware capture latency is additionally measured with instrumented timestamps on physical devices.

---

## Included file: `LOCALIZATION.md`

# Localization and RTL Plan

## Supported locales

`en`, `ur`, `ar`, `hi`, `fr`, `es`, `pt`, `de`, `it`, `ja`, `zh-CN`.

## Resource rules

- Every user-facing string lives in resources.
- Use placeholders, plurals, and quantity strings correctly.
- Never concatenate translatable sentence fragments.
- Keep technical coordinate values separate from surrounding translated labels.
- Date/time formatting follows locale while the stored instant/timezone remains language independent.
- Provide translator comments for ambiguous field-report terminology.

## Per-app language

Use AndroidX AppCompat/per-app locale support appropriate to the selected architecture. The first-launch language screen writes the locale and confirmation preference. Settings reuses the same locale catalog and selection component.

## RTL

Arabic and Urdu must:

- set application RTL support;
- use `start`/`end` padding and alignment;
- mirror navigation arrows and directional icons where semantically correct;
- preserve latitude/longitude numeric readability using bidi-safe formatting;
- keep camera controls intuitive and tested rather than blindly mirrored.

## Fonts and glyphs

Use a font stack that contains Urdu, Arabic, Devanagari, Japanese, and Chinese glyphs. If a custom Latin font lacks glyphs, define locale-aware fallback families or use system fonts. Never ship missing-square glyphs.

## Layout testing

Test:

- long German and French labels;
- Urdu and Arabic at normal and 200% font scale;
- Japanese/Chinese line wrapping;
- pseudolocales `en-XA` and `ar-XB`;
- 320dp width compact phone;
- landscape and split-screen where supported.

## Translation quality gate

Machine-generated translations may be used only as drafts. Before public release, each locale requires human review for product terms, permissions, privacy disclosures, reporting labels, and legal pages.

## Coordinate and timestamp formatting

- Provide decimal degree display with a consistent precision policy.
- Do not localize the underlying decimal representation stored in metadata.
- Support N/S/E/W labels in UI resources.
- Use user-selected 12/24-hour preference through platform format unless product explicitly overrides it.

---

## Included file: `REPORTING_EXPORT.md`

# Reporting and PDF Export Design

## Report lifecycle

```text
DRAFT → EXPORT_QUEUED → EXPORTING → EXPORTED → SHARED
                         ↘ FAILED (retryable)
```

The report remains editable after export. Editing creates a stale-export indicator until a new export version is generated.

## Draft behavior

- Create a report ID immediately when entering New Report.
- Debounce text writes but flush on lifecycle stop.
- Media additions/removals persist transactionally.
- “Save Draft” confirms persistence; it is not the only point where data is saved.
- Empty abandoned drafts may be cleaned only after a defined retention policy and confirmation logic.

## Attachment selection

Primary picker: in-app Collection selection by media IDs. Optional external attachment: Android Photo Picker/SAF with explicit user selection and persisted read permission where available.

Store order explicitly. Show processing/failed media and prevent final export until required attachments are ready.

## GPS summary rule

Use this deterministic priority:

1. Explicit report location selected/refreshed by user.
2. Location of the first attached media.
3. No location.

Never average coordinates silently. A future map/bounds summary can be added as a separate field.

## PDF content

Minimum professional PDF:

- GeoSnap/report header
- report title
- generated date/time and timezone
- location summary and coordinates/accuracy where available
- notes
- ordered media sections with image, capture timestamp, location, and caption
- page numbers
- optional disclaimer about location/device accuracy

Video attachments use a generated thumbnail and metadata; PDF cannot embed playable video by default.

## Rendering

- Use a deterministic page size and margins.
- Decode images to page-required dimensions.
- Preserve aspect ratio.
- Handle portrait/landscape media.
- Escape/control user text safely.
- Break long notes across pages.
- Keep report generation off main thread.
- Close all streams and PDF documents in `finally`/structured resource scopes.

## Output

Two supported flows:

1. Internal export file exposed through FileProvider for quick sharing.
2. “Save As” using `ACTION_CREATE_DOCUMENT` so the user chooses a destination.

Persist the resulting URI and metadata. Never assume a raw filesystem path.

## Sharing

Use `ACTION_SEND` with MIME `application/pdf`, a content URI, temporary read permission, and chooser. Record `shared_at` when the share intent is successfully launched; do not claim the recipient completed sharing because Android generally cannot guarantee that.

## Export tests

- no location;
- approximate location;
- 1, 8, 50+ photos;
- portrait/landscape images;
- video thumbnails;
- long multilingual notes including RTL;
- missing attachment URI;
- low storage;
- cancellation/retry;
- open exported PDF in at least two reader apps;
- share to email/messaging app.

---

## Included file: `OBSERVABILITY.md`

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

---

## Included file: `CI_CD.md`

# Build, CI, and Release Automation

## Toolchain

Use a current stable Android Studio/AGP/Kotlin combination supported by the repository. Do not upgrade blindly during feature work. Record JDK, Gradle, AGP, Kotlin, compileSdk, targetSdk, and dependency versions in the verification log.

## CI stages

1. Checkout and dependency verification.
2. Formatting/ktlint and static analysis/detekt if configured.
3. Android lint.
4. Unit tests.
5. Debug assemble.
6. Instrumentation tests on managed/emulated devices for non-hardware paths.
7. Room migration tests.
8. Release bundle compilation with CI secret signing only on protected branches/tags.
9. Artifact retention: reports, test results, mapping file, AAB, benchmark results.

## Suggested commands

```bash
./gradlew --version
./gradlew lint test assembleDebug
./gradlew connectedCheck
./gradlew bundleRelease
```

Add module-specific and benchmark tasks based on the actual repository.

## Signing

- Generate and protect a separate upload key.
- Never commit keystore or passwords.
- Read signing values from environment/CI secret store.
- Use Play App Signing.
- Back up upload key recovery material according to organization policy.

## Build types

- `debug`: debuggable, verbose sanitized diagnostics, test endpoints only if later needed.
- `release`: non-debuggable, R8 minification/resource shrink, no debug menus, strict network/security configuration.
- optional `benchmark`: profileable and configured for Macrobenchmark without weakening release.

## Versioning

- monotonically increasing `versionCode`;
- semantic user-facing `versionName`;
- version shown in Settings from build config;
- tag release commit and preserve schema/export evidence.

## Distribution

- Build AAB.
- Test with bundletool or Play internal track.
- Run pre-launch report.
- Stage rollout and monitor vitals.
- Keep rollback decision and previous stable artifact available.

---

## Included file: `PLAY_STORE_RELEASE.md`

# Google Play and Release Compliance

## Target API

At implementation time, use at least the current Google Play-required target API and recheck immediately before release. The documented 2025 requirement is Android 15/API 35 for new apps and updates, but policy can change.

## Location

MVP uses foreground capture-time location only. Do not declare background location. Provide an in-app disclosure immediately before the runtime prompt and explain how stamped/shared files reveal location.

## User data and privacy

Before publication:

- host an active privacy policy URL and link it inside the app;
- complete Data safety accurately;
- disclose camera, microphone, and location use;
- document local processing and any SDK collection;
- provide deletion instructions for app-managed data;
- avoid claims that GPS evidence cannot be altered.

## Storage

Do not request all-files access. Use MediaStore, Photo Picker, and SAF. Explain media access only where required.

## Store assets

Prepare:

- final app icon and adaptive icon;
- feature graphic;
- phone screenshots showing real UI/data without exposing private coordinates;
- short and full description;
- privacy policy;
- content rating;
- app access instructions if future login is introduced.

## Release checklist

- package name registered/verified as required;
- signing configured with Play App Signing;
- version code increased;
- release notes localized where needed;
- AAB inspected;
- target API compliant;
- permissions match actual features;
- no debug/test content;
- pre-launch report reviewed;
- internal/closed test completed;
- staged rollout plan and rollback criteria approved.

---

## Included file: `FUTURE_CLOUD_ARCHITECTURE.md`

# Future Cloud Architecture — Not MVP

GeoSnap MVP is local-first and requires no backend. This document prevents premature cloud coupling while defining a safe expansion path.

## Candidate capabilities

- authenticated backup/sync;
- organization/workspace accounts;
- shared report links;
- server-generated evidence signatures;
- team templates and branding;
- web dashboard;
- device-to-cloud resumable upload.

## Suggested shape

```text
Android app
→ authenticated HTTPS API
→ object storage for media/PDF
→ relational metadata database
→ async media/report workers
→ audit/event store
```

## Principles

- Local Room remains source of immediate UI truth.
- Sync state is explicit per record.
- Upload is opt-in and disclosed.
- Tenant ID is enforced server-side on every request.
- Object keys are server-generated, not raw filenames.
- Use signed, short-lived URLs.
- Encrypt transport and provider-managed storage; consider app-level encryption only with a defined key recovery model.
- Add idempotency keys and resumable uploads.
- Preserve capture metadata and server receipt time separately.

## Evidence integrity option

A stronger future design can hash finalized media plus canonical metadata on-device, upload the hash, and obtain a server timestamp/signature. This provides evidence that a specific byte sequence existed by server receipt time, but it still does not prove GPS was physically truthful without stronger attestation and operational controls.

## Do not add yet

- login screen;
- analytics SDKs;
- cloud permissions;
- background sync;
- remote database;
- subscriptions;
- web dashboard.

These require explicit product approval and privacy/security review.

---

## Included file: `CONTEXT_ENGINEER.md`

# Context Engineering Protocol

## Objective

Keep the coding agent grounded in the current repository, authoritative product requirements, and verified results while avoiding stale assumptions and context overload.

## Context priority

1. Current user requirement and accepted product decisions.
2. Actual repository code and configuration.
3. Local `stitch_geosnap/` design exports.
4. This documentation pack.
5. Official Android/Google documentation.
6. Agent assumptions, which must be labeled and minimized.

## Required context files maintained in the repo

- `docs/PROJECT_STATE.md` — current phase, build health, active task.
- `docs/DECISIONS.md` — architecture/product decisions with date and rationale.
- `docs/STITCH_ASSET_MAP.md` — source folder → Android asset/screen mapping.
- `docs/KNOWN_ISSUES.md` — reproducible defects and severity.
- `docs/VERIFICATION_LOG.md` — commands, device checks, screenshot checks.

## Session start context packet

Read only what is needed in this order:

```text
PROJECT_STATE
active TASKS section
relevant architecture/design contract
relevant code and tests
relevant Stitch folder/screenshots
```

Do not reread every document before every small change. Refresh source files whenever they change.

## Decision record format

```markdown
## ADR-XXX: Title
Date:
Status: proposed/accepted/superseded
Context:
Decision:
Alternatives:
Consequences:
Verification:
```

## Assumption handling

When a requirement is missing:

- infer only a reversible implementation detail;
- record it as an assumption;
- choose least-privilege and platform-standard behavior;
- do not invent visible product scope, data collection, subscriptions, accounts, or cloud sync.

## Context compression

At the end of each task, reduce the working state to:

- what changed;
- contracts now true;
- tests/evidence;
- unresolved risks;
- next task.

Discard exploration noise and superseded plans. Keep exact error messages and commands only in verification logs where useful.

## Screenshot context

For UI work, always pair:

- target route;
- reference screenshot;
- relevant Stitch source folder;
- design token values;
- implementation screenshot/diff;
- device dimensions and font scale.

Never judge visual parity from memory.

---

## Included file: `LOOP_ENGINEER.md`

# Loop Engineering Protocol

## Purpose

Prevent incomplete AI-generated implementation by enforcing measurable feedback after every change.

## Core loop

```text
Observe → Define success → Implement → Execute → Measure → Compare → Diagnose → Fix → Re-run → Record
```

## Task loop

### Observe

Read the active task, implementation, tests, logs, and design source. Reproduce the current failure or missing behavior.

### Define success

Write explicit acceptance checks before coding. Example:

- real CameraX capture succeeds;
- final content URI opens;
- database record exists;
- JPEG visible stamp matches template;
- EXIF coordinates equal the capture snapshot within formatting precision;
- UI returns to ready state.

### Implement

Make the smallest complete vertical change. Avoid unrelated cleanup.

### Execute

Run the narrowest tests first, then broader gates. Hardware work requires a device workflow; do not substitute compilation for verification.

### Measure and compare

- Compare expected vs actual state.
- Inspect logs and generated files.
- Query Room when needed.
- Open exported media/PDF.
- Compare UI screenshot.
- Measure latency/memory when the task is performance-sensitive.

### Diagnose and fix

Identify root cause, not just symptom. Add a regression test when possible. Repeat until success conditions pass.

### Record

Update task evidence and project state. Distinguish verified, implemented-only, and blocked portions.

## Automated loop command concept

Create a repository script such as `scripts/verify.sh` that runs formatting, lint, unit tests, debug build, and selected module tests. It must return non-zero on failure and print a compact summary.

## Guardrails

- Maximum two speculative fixes without new evidence; after that, gather logs/traces or simplify reproduction.
- Never mark a task done with skipped tests unless the task is explicitly blocked.
- Never delete a failing test to make the build green unless the requirement was intentionally removed and documented.
- Never silence lint globally for a local issue.
- Never use sample/hardcoded data to bypass a hardware or persistence defect.

## Phase completion loop

At phase end:

1. Run all phase acceptance tests.
2. Run regression suite.
3. Perform design screenshot checks.
4. Run security/permission checks relevant to phase.
5. Test upgrade/process-death paths.
6. Update known issues.
7. Only then advance the phase.

---

## Included file: `PHASES.md`

# Development Phases

## Phase 0 — Repository and design reconnaissance

- Inventory project, toolchain, existing code, and `stitch_geosnap/`.
- Create asset map and screen comparison checklist.
- Establish baseline build/tests.
- Confirm package ID, min/target SDK, app name, versioning, and release constraints.

Exit: documented repository map and green baseline or clearly recorded pre-existing failures.

## Phase 1 — Foundation

- Gradle/version catalog and modules/packages.
- Compose theme/design system.
- Hilt, Room, DataStore, navigation shell.
- Core models, result/error types, dispatchers, test fakes.
- CI lint/test/build.

Exit: app launches into a placeholder navigation shell built from final architecture, with tests.

## Phase 2 — Startup, language, onboarding

- Branded splash.
- Locale catalog and per-app selection.
- Full translations structure and RTL.
- Three-page swipe onboarding.
- Startup decision persistence.

Exit: first-launch and returning-user flows pass UI tests and match screenshots.

## Phase 3 — Camera and location core

- Permission rationale/state machine.
- Camera preview and controls.
- Photo capture to MediaStore.
- Foreground location state and capture snapshot.
- Basic visible photo stamp and EXIF.
- Persistent media record.

Exit: real device captures a GPS-stamped photo and it appears in collection data.

## Phase 4 — Video and finalization

- Video recording and audio permission.
- Video overlay/finalization strategy.
- WorkManager processing, progress, retry, cleanup.
- Low-storage and lifecycle handling.

Exit: real device produces playable finalized video with expected metadata/overlay behavior.

## Phase 5 — Collection

- Reactive Room queries, search, filters, grouping.
- Thumbnail pipeline and detail view.
- Selection, share, delete, missing-media recovery.
- Visual matching.

Exit: collection remains smooth and correct with a large fixture dataset and real captures.

## Phase 6 — Reports

- Report schema/repositories.
- New/Edit Report with auto-save.
- Attachment picker/reordering.
- Reporting list/status filters.
- PDF export, Save As, share, retry.

Exit: a real multi-page PDF opens and shares successfully; report status is persistent.

## Phase 7 — Templates and settings

- Built-in template formatter/preview.
- Selection persistence and camera integration.
- Settings, legal, share app, language change.
- Optional custom template editor only if accepted.

Exit: all references implemented and language/template changes propagate correctly.

## Phase 8 — Hardening

- Full test matrix and failure injection.
- Security/privacy audit.
- Performance profiling and Baseline Profile.
- Accessibility, RTL, large font, adaptive layouts.
- Database migration rehearsal.

Exit: no open P0/P1 defects and all quality gates pass.

## Phase 9 — Release readiness

- Final target SDK/Play policy check.
- Privacy policy/data safety form inputs.
- App icon, screenshots, store copy.
- Release signing configuration.
- AAB, bundletool/device tests, internal track rollout.
- Crash/ANR monitoring plan.

Exit: production-candidate AAB passes Play pre-launch checks and staged rollout checklist.

---

## Included file: `TASKS.md`

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

---

## Included file: `QA_TESTING.md`

# QA and Testing Strategy

## Test pyramid

### Unit tests

Cover:

- startup destination resolver;
- location freshness/accuracy policy;
- coordinate/date/time formatters;
- stamp layout/field inclusion rules;
- capture filename sanitizer;
- media filter/date grouping logic;
- report validation and status transitions;
- template selection/configuration;
- permission state reducers;
- error mapping;
- PDF pagination calculations.

### Data/integration tests

Cover:

- Room DAO queries and relations;
- every database migration;
- DataStore serialization/migration;
- repository behavior with fake gateways;
- media finalization transaction;
- report export state and retry idempotency;
- deletion policy;
- WorkManager with test driver.

### Compose UI tests

Cover:

- language selection and Continue state;
- onboarding swipes, dots, Skip, Next, Get Started;
- bottom navigation selection/restoration;
- collection filters/search/empty/error states;
- report form validation and draft restoration;
- reporting status chips;
- template selection;
- settings navigation;
- RTL and large-font critical layouts.

### Instrumentation/physical-device tests

Cover:

- CameraX preview and capture;
- front/rear switch if supported;
- flash/torch and focus;
- real video recording with/without audio permission;
- MediaStore visibility;
- EXIF GPS/time after photo finalization;
- visible stamp in final image/video;
- location off/on, approximate/precise, weak GPS;
- share URI and PDF open;
- lifecycle background/foreground and rotation.

## Device matrix

At minimum:

- Android 8/9 class device if minSdk allows;
- Android 11/12;
- Android 13 photo/media permission behavior;
- Android 14/15+ foreground service/permission behavior;
- one Samsung device;
- one Pixel/AOSP-like device;
- one lower-memory device;
- at least one device with multiple rear cameras;
- tablet/large-screen layout sanity check.

Use real devices for camera and GPS acceptance. Emulators are supplementary.

## Permission matrix

Test every combination:

- camera granted/denied/permanently denied;
- coarse only/fine location/location disabled;
- microphone granted/denied;
- permissions revoked from Settings while app is paused;
- selected external URI grant later revoked.

## Failure injection

Inject:

- camera initialization exception;
- image capture failure;
- video finalize error;
- stale/no location;
- geocoder failure;
- MediaStore write failure;
- insufficient storage;
- corrupted image;
- Room write failure;
- WorkManager retry and process death;
- PDF write/share failure.

## Visual regression

Create deterministic screenshot tests for all non-camera screens using fake data matching references. For the camera screen, use a fake preview surface in screenshot tests and separately verify real preview on device.

Reference images are under `reference_screenshots/`. Keep comparison artifacts out of release resources.

## Build gates

Before closing a phase, run as applicable:

```bash
./gradlew clean
./gradlew lint
./gradlew test
./gradlew connectedCheck
./gradlew assembleDebug
./gradlew bundleRelease
./gradlew :benchmark:connectedCheck
```

Use exact module tasks if the repository differs. A release bundle may require configured signing; unsigned release compilation still must be validated in CI.

## Bug severity

- P0: data loss, privacy leak, crash in primary capture/export, invalid shared URI.
- P1: core feature unusable, incorrect location/stamp, broken navigation, persistent corruption.
- P2: major UI mismatch, accessibility blocker, recoverable feature defect.
- P3: minor visual/text issue.

No P0/P1 remains at release candidate. P2 requires explicit documented acceptance.

---

## Included file: `DEFINITION_OF_DONE.md`

# Definition of Done

GeoSnap is not done because screens exist. It is done when the following evidence is true.

## Product

- First launch follows Splash → Language → Onboarding → Camera.
- Returning launch opens Camera without replaying completed setup.
- All four bottom destinations work and preserve expected state.
- Real photos and videos are captured on physical devices.
- Capture-time location is real, optional when unavailable, and never fabricated.
- Final media contains the selected visible stamp according to product rules.
- Photos contain supported EXIF time/GPS metadata when location exists.
- Collection displays actual persisted media with working filters/search.
- Reports persist drafts, attach media, export real PDF, and share securely.
- Templates alter camera/final output formatting.
- Language change works for all specified locales including RTL.

## Visual

- Every supplied screen has an implementation screenshot comparison.
- No structural mismatch remains.
- Design tokens/components are centralized.
- UI is responsive at compact and large widths.
- 200% font scaling remains usable.
- Accessibility labels and focus order are verified.

## Architecture/code

- Dependency rules are respected.
- No business logic in composables.
- No hardcoded sample records in production paths.
- Room migrations are explicit and tested.
- Durable work uses WorkManager and is idempotent.
- Errors are typed and recoverable.
- Public interfaces are documented.

## Privacy/security

- No background location or broad storage permission in MVP.
- Permission requests are contextual and disclosed.
- No exact coordinates/user content in release logs.
- Shared files use content URIs and temporary grants.
- Release contains no credentials, test keys, or local paths.
- Exported components and backup/network policies are reviewed.
- Privacy policy and Play data safety information are prepared.

## Quality

- Lint passes.
- Unit tests pass.
- Integration/migration tests pass.
- Compose UI tests pass.
- Required instrumentation tests pass.
- Real-device camera/GPS/video/export matrix passes.
- No open P0/P1 defects.
- Performance benchmarks are recorded and acceptable.
- Release build is minified, installs, launches, and completes critical journeys.

## Release

- Version code/name are correct.
- Upload signing is configured securely.
- Signed AAB is generated.
- Bundle/device testing succeeds.
- Play target API and current policy requirements are rechecked at release time.
- Internal test rollout feedback is resolved or explicitly accepted.

## Final evidence table

| Area | Evidence link/file | Status | Owner | Date |
|---|---|---|---|---|
| Build |  |  |  |  |
| Tests |  |  |  |  |
| Device matrix |  |  |  |  |
| Visual diffs |  |  |  |  |
| Security audit |  |  |  |  |
| Performance |  |  |  |  |
| AAB/internal track |  |  |  |  |

---

## Included file: `ASSET_INTEGRATION.md`

# Stitch Asset Integration Procedure

## Inventory

Run a recursive inventory of `stitch_geosnap/` and record:

- folder name;
- contained image/vector/font/layout files;
- pixel dimensions;
- apparent target screen;
- whether it is final, enhanced, duplicate, or unused;
- destination Android resource name;
- license/source status.

Create `docs/STITCH_ASSET_MAP.md` from this inventory.

## Priority

When multiple variants exist:

1. Use explicit “updated” or “enhanced” export when it matches the supplied latest screenshot.
2. Compare variants visually rather than choosing by name alone.
3. Record the selected source and why.
4. Keep original Stitch files unchanged.

## Conversion

- Raster images: preserve quality, crop only according to screenshot, consider WebP/AVIF where supported and visually lossless.
- SVG/vector: convert to Android VectorDrawable only if features are supported; otherwise use raster/vector asset safely.
- Logos: prefer provided vector or high-resolution transparent asset.
- Fonts: use only licensed included fonts; otherwise system fallback.
- Generated HTML/CSS: use for measurement/reference only, not as runtime WebView UI.

## Image loading

Onboarding images are packaged resources and should be decoded efficiently. Collection/report thumbnails are runtime content URIs and must use size-aware async loading.

## Screenshot reconstruction

Do not use the full screenshot as one background image. Build real controls, text, lists, and navigation in Compose. Only photographic/artwork areas should use source imagery.

## Asset validation

- no accidental EXIF/private metadata in bundled images;
- no huge unnecessary dimensions;
- no copyrighted third-party trademarks without rights;
- no duplicate assets increasing APK size;
- dark/light contrast and crop tested on target aspect ratios.

---

## Included file: `SCREENSHOT_INDEX.md`

# Reference Screenshot Index

| File | Intended screen | Notes |
|---|---|---|
| `reference_screenshots/01_splash.png` | Splash | Logo, tagline, progress, version |
| `02_language.png` | First-launch language | Intro block and six visible choices |
| `03_onboarding_gps_stamp.png` | Onboarding 1 | GPS stamping |
| `04_onboarding_realtime_location.png` | Onboarding 2 | coordinates/time |
| `05_onboarding_reports.png` | Onboarding 3 | report generation |
| `06_camera.png` | Main camera | preview, metadata overlay, controls |
| `07_collection.png` | Collection | search, chips, date-grouped grid |
| `08_new_report.png` | New/Edit Report | fields, attachments, GPS, submit |
| `09_reporting.png` | Reporting list | search, statuses, report cards |
| `10_templates.png` | Timestamp Templates | categories and preview cards |
| `11_language_extended.png` | Settings language | extended language catalog |
| `12_settings.png` | Settings | preferences/legal/version |
| `13_stitch_directory.png` | Local design directory | expected Stitch folder names |

The screenshots demonstrate intended design and sample content, not production data. Coordinates, dates, cities, media, report cards, and statuses shown in them must not be hardcoded into app behavior.

---

## Included file: `SOURCE_RESEARCH.md`

# Official Research Sources

Research baseline checked in June 2026. Recheck versions and Play policy immediately before implementation/release.

## Android architecture and state

- Android app architecture guide: https://developer.android.com/topic/architecture
- Architecture recommendations: https://developer.android.com/topic/architecture/recommendations
- UI layer and ViewModel/StateFlow: https://developer.android.com/topic/architecture/ui-layer
- Compose architecture/UDF: https://developer.android.com/develop/ui/compose/architecture
- Offline-first data layer: https://developer.android.com/topic/architecture/data-layer/offline-first
- Hilt: https://developer.android.com/training/dependency-injection/hilt-android

## Camera and media

- CameraX overview: https://developer.android.com/media/camera/camerax
- CameraX releases: https://developer.android.com/jetpack/androidx/releases/camera
- CameraX architecture: https://developer.android.com/media/camera/camerax/architecture
- Video capture: https://developer.android.com/media/camera/camerax/video-capture
- CameraX Compose API: https://developer.android.com/reference/kotlin/androidx/camera/compose/package-summary
- CameraX OverlayEffect: https://developer.android.com/reference/androidx/camera/effects/OverlayEffect
- Media3 Transformer: https://developer.android.com/media/media3/transformer
- Media3 editing/overlays: https://developer.android.com/media/implement/editing-app
- ExifInterface releases: https://developer.android.com/jetpack/androidx/releases/exifinterface

## Location and permissions

- Location permissions: https://developer.android.com/develop/sensors-and-location/location/permissions
- Runtime location access: https://developer.android.com/develop/sensors-and-location/location/permissions/runtime
- Play background location guidance: https://support.google.com/googleplay/android-developer/answer/9799150
- Play user data policy: https://support.google.com/googleplay/android-developer/answer/10144311

## Storage and sharing

- MediaStore/shared media: https://developer.android.com/training/data-storage/shared/media
- Storage use cases/scoped storage: https://developer.android.com/training/data-storage/use-cases
- Storage Access Framework: https://developer.android.com/training/data-storage/shared/documents-files
- FileProvider: https://developer.android.com/reference/androidx/core/content/FileProvider

## Security

- Android security checklist: https://developer.android.com/privacy-and-security/security-tips
- Security best practices: https://developer.android.com/privacy-and-security/security-best-practices
- Android Keystore: https://developer.android.com/privacy-and-security/keystore
- Network security configuration: https://developer.android.com/privacy-and-security/security-config
- Cryptography: https://developer.android.com/privacy-and-security/cryptography

## Localization

- Localization: https://developer.android.com/guide/topics/resources/localization
- Language/culture support: https://developer.android.com/training/basics/supporting-devices/languages
- Pseudolocales: https://developer.android.com/guide/topics/resources/pseudolocales
- Translation editor and RTL: https://developer.android.com/studio/write/translations-editor

## Performance/testing/release

- Baseline Profiles: https://developer.android.com/topic/performance/baselineprofiles/overview
- Macrobenchmark: https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview
- App bundles: https://developer.android.com/guide/app-bundle
- App signing: https://developer.android.com/studio/publish/app-signing
- Play target API requirements: https://support.google.com/googleplay/android-developer/answer/11926878

---

## Included file: `MASTER_PROMPT.md`

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

---

## Included file: `COMPACT_GOAL_PROMPT.md`

# Claude Code `/goal` Prompt — under 4000 characters

Complete GeoSnap as a production Kotlin/Jetpack Compose Android app in the current repository. First recursively inspect the repo and read every root MD file. Treat `stitch_geosnap/` and supplied screenshots as read-only visual truth; create `docs/STITCH_ASSET_MAP.md`, reuse its assets, and match layouts responsively. Do not build a fake/mock app or hardcode sample media, reports, coordinates, or statuses.

Implement the full flow: branded splash → first-launch language selection → 3 swipeable onboarding pages → main shell. Languages: English, Urdu, Arabic, Hindi, French, Spanish, Portuguese, German, Italian, Japanese, Simplified Chinese; support per-app locale, RTL, long text, and persistence. Main bottom destinations: Camera, Collection, Reporting, Templates. Settings opens from Camera.

Use single activity, Compose/Material3, Navigation Compose, ViewModel + immutable StateFlow/UDF, Hilt, Room source of truth, DataStore preferences, WorkManager durable jobs, MediaStore/SAF/FileProvider, CameraX, foreground location, ExifInterface, and Media3/CameraX effects where required. Keep platform/data/business logic out of composables and enforce repository boundaries.

Camera must use real preview and support real photo capture, video recording, optional audio, flash/torch, tap focus, pinch zoom, rotation, lifecycle cleanup, lens switch where supported, and error recovery. Request permissions contextually. Use only foreground coarse/fine location; no background location or broad storage permission. Show acquiring/precise/approximate/unavailable states. Record real latitude, longitude, optional altitude/accuracy, instant, timezone, freshness, and provider state; never fabricate GPS.

Apply the selected Minimal/Classic/Detailed/Reporter timestamp-GPS template to final photos and videos. Write supported EXIF GPS/time to photos. For video, use a verified CameraX overlay or durable Media3 Transformer + WorkManager pipeline with PROCESSING/READY/FAILED states. Save final app-owned media through MediaStore and persist metadata in Room.

Collection must show real captures, grouped by date, with search and All/Today/This Week/Videos/Photos filters, thumbnails, detail, selection, secure share/delete, and missing/processing/error states. Reporting must support persistent auto-saved drafts, media attachment/reordering, title/location/date/time/notes/GPS summary, list search and Draft/Exported/Shared filters, real multi-page PDF export, SAF Save As, FileProvider sharing, retry/version state, and process-death recovery. Settings must implement language, Share App, Privacy, Terms, and real version.

Follow `TASKS.md` in order. For every task run: understand → plan → implement → build → test → inspect output and screenshot diff → fix → rerun → record evidence. Add unit, Room migration, repository, Compose UI, WorkManager, instrumentation, screenshot, Macrobenchmark, and Baseline Profile tests. Real-device verification is mandatory for camera, GPS, video, EXIF, MediaStore, PDF open, and sharing. Never call a task complete without evidence. Finish only when `DEFINITION_OF_DONE.md` passes, lint/tests/build/AAB succeed, no P0/P1 defects remain, and Play privacy/permission requirements are satisfied.
