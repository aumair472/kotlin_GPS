# Project State

_Living status doc. Updated as phases progress._

## Baseline (2026-06-17)

- **Toolchain verified:** JDK 17, Gradle 8.11.1 (wrapper), AGP 8.7.3, Kotlin 2.0.21, KSP1, Hilt 2.52. Local Android SDK platforms 34/35/36.1, build-tools to 37.
- **First build:** `./gradlew :app:assembleDebug` → **BUILD SUCCESSFUL** (debug APK produced). No pre-existing test baseline (project was docs-only before P1).
- **Known warnings:** `statusBarColor`/`navigationBarColor` setters deprecated on API 35 (cosmetic; edge-to-edge already applied). Tracked for cleanup.
- **Env limitation:** no device/emulator — hardware acceptance items implemented against real APIs, verified by compile + unit/Robolectric tests (see DECISIONS.md).

## Phase status

| Phase | State | Evidence |
|---|---|---|
| P0 Reconnaissance | ✅ done | STITCH_ASSET_MAP.md, DECISIONS.md, this file |
| P1 Foundation | ✅ done | builds; Room+Hilt+DataStore+repositories+design system+nav infra; Robolectric Room test green |
| P2 Startup/localization | ✅ done | splash + 11-language picker + onboarding + startup resolver + nav shell; per-app locale + RTL (ar/ur); `testDebugUnitTest` green (StartupResolver, model, LanguageVM, Room repo). Other locales fall back to en (drafts). |
| P3 Camera/location/photo | 🟡 code-complete, device-pending | Real CameraX preview/controls (flash/zoom/focus/lens), FusedLocation gateway + capture policy, recompress→stamp→MediaStore→EXIF→Room photo finalizer, contextual permissions. Pure logic unit-tested (policy, coord formatter, stamp builder) — green. Live preview/capture/EXIF need a device. |
| P4 Video | 🟡 code-complete, device-pending | CameraX Recorder start/stop with optional audio (silent fallback), durable Media3 Transformer overlay WorkManager job with PROCESSING/READY/FAILED + retry, thumbnail/duration extraction. Transcode/codec behaviour needs a device. |
| P4 Video | ⬜ | |
| P5 Collection | ✅ done | Date-grouped adaptive grid, All/Today/This Week/Videos/Photos filters, debounced search, Coil thumbnails, processing/failed/missing states, selection + secure share (content URIs) + delete, media detail. Grouping unit-tested; intents device-pending. |
| P6 Reporting/PDF | ✅ code-complete | Reporting list, editor w/ durable auto-save, GPS refresh, attach/detach picker, real `PdfDocument` multi-page export via WorkManager (QUEUED→RUNNING→READY/FAILED + retry), FileProvider share + SAF Save As. Export lifecycle Room-tested; PDF render/open device-pending. |
| P7 Templates/settings | ✅ done | Template picker w/ live stamp previews + persistence; Settings (language reuse, Share App, Privacy, Terms, real version); legal screens. |
| P8 Quality/release | 🟡 substantial | R8 minified release APK + release AAB built; lint vital + debug 0 errors; Room schema v1 exported; `testDebugUnitTest` green (8 classes); androidTest/Compose UI tests compile; `:benchmark` module builds. Device matrix, instrumentation/benchmark runs, signed AAB, Play submission = device/account-pending. |

## Release artifacts (built this session)

- `app/build/outputs/apk/release/app-release-unsigned.apk` — R8 minified + resource-shrunk.
- `app/build/outputs/bundle/release/app-release.aab` — unsigned (upload signing supplied at release time).
- `app/schemas/com.geosnap.core.database.GeoSnapDatabase/1.json` — exported Room schema.

## Verification commands

```bash
./gradlew :app:testDebugUnitTest      # unit + Robolectric (green)
./gradlew :app:lintDebug              # 0 errors
./gradlew :app:assembleRelease        # R8 APK
./gradlew :app:bundleRelease          # AAB
./gradlew :benchmark:assembleDebug    # macrobenchmark module
# device-only:
./gradlew :app:connectedDebugAndroidTest
./gradlew :benchmark:connectedBenchmarkAndroidTest
```
