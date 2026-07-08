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
