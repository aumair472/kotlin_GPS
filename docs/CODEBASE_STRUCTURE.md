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
