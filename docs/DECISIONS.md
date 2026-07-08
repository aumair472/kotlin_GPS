# Decision Log

Chronological record of material engineering decisions. Each entry: context → decision → rationale → consequences.

## D1 — Single Gradle module with enforced package boundaries (2026-06-17)

**Context.** `ARCHITECTURE.md` recommends a `:core:*` / `:feature:*` multi-module graph but explicitly allows consolidation "for an initially small repository … provided package boundaries and dependency rules remain equivalent."

**Decision.** Ship one `:app` module. Mirror the documented module graph as packages: `com.geosnap.core.{common,model,designsystem,navigation,database,datastore,media,location,files}` and `com.geosnap.feature.{splash,language,onboarding,camera,collection,reporting,templates,settings}`.

**Rationale.** Build/iteration speed in a from-scratch environment; avoids module wiring churn while the API surface stabilises. Boundaries are still enforceable by review and (later) by lint/Konsist.

**Consequences.** If build times or boundary violations grow, promote `core/*` packages to modules. Dependency rules (no core→feature, no Room entity in UI, no platform API in composables) are honoured today at the package level.

## D2 — Toolchain & version baseline (2026-06-17)

**Decision.** JDK 17, Gradle 8.11.1 (wrapper), AGP 8.7.3, Kotlin 2.0.21 with the Compose compiler Gradle plugin, `compileSdk`/`targetSdk` 35, `minSdk` 26. All versions pinned in `gradle/libs.versions.toml`.

**Rationale.** Local SDK has platforms 34/35/36.1 and build-tools to 37; 35 is the current stable Play target. minSdk 26 per `BUILD_SETUP.md`. Stable, mutually compatible set (no alphas) per the operating contract.

**Consequences.** Re-check Play target API at release time (`PLAY_STORE_RELEASE.md`).

## D3 — Per-app locale via AndroidX AppCompat (2026-06-17)

**Decision.** Use `AppCompatDelegate.setApplicationLocales` + `autoStoreLocales` Locale service, with the confirmed-locale flag persisted in DataStore.

**Rationale.** First-class per-app language without a custom Configuration wrapper; survives process death; integrates with Settings reuse of the same catalog.

## D4 — Photo finalize = recompress-then-tag (2026-06-17)

**Decision.** CameraX → temp JPEG → bounded decode → orientation normalize → `Canvas` stamp → compress into pending MediaStore item → write EXIF → clear pending → persist Room → delete temp, per `CAMERA_GPS_PIPELINE.md`. EXIF is never written before recompression.

## D5 — Video stamping strategy (2026-06-17)

**Decision.** Record a clean source with CameraX `Recorder`, then a durable WorkManager + Media3 `Transformer` job overlays the stamp and produces the final MP4; media row carries PROCESSING/READY/FAILED. CameraX `OverlayEffect` is kept as a documented alternative pending per-device verification.

**Rationale.** Post-process pipeline is the more portable, testable, retry-able path and matches the durable-jobs requirement; raw source stays private until a verified final output exists.

## D6 — minSdk 29 for permissionless MediaStore writes (2026-06-17)

**Context.** `BUILD_SETUP.md` suggested minSdk 26 "unless business requirements demand". The privacy contract forbids `WRITE_EXTERNAL_STORAGE`/broad storage. On API 26–28, inserting into MediaStore requires `WRITE_EXTERNAL_STORAGE`.

**Decision.** minSdk 29. Use scoped storage `IS_PENDING` to write app-created photos/videos with no storage permission.

**Rationale.** Resolves the only materially different product outcome (storage permission vs none) in favour of the privacy requirement; API 29+ is near-universal in 2026.

## Environment limitation (recorded honestly)

No physical Android device or running emulator is available, and one **cannot be created** here — verified 2026-06-18:

- `adb devices` → none connected.
- No emulator system image installed (`$SDK/system-images` absent), no `cmdline-tools`/`sdkmanager`/`avdmanager` to download one.
- `Win32_Processor.VirtualizationFirmwareEnabled = False` → a hardware-accelerated AVD (x86_64 + WHPX/HAXM) cannot boot; software emulation of a modern image is not viable.
- No upload signing key and no Play account.

Therefore hardware-only acceptance items (live camera preview/capture, real GPS fix, on-device video transcode/overlay, EXIF on real captures, MediaStore round-trip on device, PDF open in a viewer, system share-sheet, device matrix, Macrobenchmark/Baseline-Profile runs, signed AAB, Play submission) are **Implemented against real production APIs** and verified as far as the JVM allows (compilation + **36 unit/Robolectric tests including 3 executed Compose UI tests**), but remain **Implemented, not device-Verified** per `CLAUDE.md`. This is the standing, genuine P8 stop condition ("a hardware-only behavior cannot be verified in the current environment" + "a required signing key/account is unavailable"). All device-independent work is complete and verified.
