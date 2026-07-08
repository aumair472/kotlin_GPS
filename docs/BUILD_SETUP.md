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
