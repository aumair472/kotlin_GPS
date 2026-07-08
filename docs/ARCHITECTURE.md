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
