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
| Build (debug) | `:app:assembleDebug` → debug APK | ✅ pass | agent | 2026-06-18 |
| Build (R8 release) | `app/build/outputs/apk/release/app-release-unsigned.apk` (minified+shrunk) | ✅ pass | agent | 2026-06-18 |
| Lint | `:app:lintDebug` / `lintVitalRelease` — 0 errors (172 non-fatal warnings) | ✅ pass | agent | 2026-06-18 |
| Unit/Robolectric tests | `:app:testDebugUnitTest` — **36 tests / 11 classes, 0 failures** (StartupResolver, model, LanguageVM, CaptureLocationPolicy, StampTextBuilder, DateLabeler, Media/Report Room) | ✅ pass | agent | 2026-06-18 |
| Migration tests | schema v1 exported `app/schemas/.../1.json`; no migrations yet (v1). Fresh-create exercised by Room tests | ✅ baseline | agent | 2026-06-18 |
| Compose UI tests | **Executed in JVM via Robolectric** (`LanguageScreenRobolectricTest`, `OnboardingScreenRobolectricTest`, `TemplatesScreenRobolectricTest`) — render catalog, selection→persist, locale apply, onboarding complete, template persist all pass. Plus `app/src/androidTest` component tests compile (run on device). | ✅ partial / 🟡 connected-pending | agent | 2026-06-18 |
| Device matrix (camera/GPS/video/EXIF/MediaStore/PDF open/share) | implemented vs real APIs; no device in env | 🟡 device-pending | — | — |
| Visual diffs | reference screenshots in `docs/reference_screenshots/`; on-device capture/diff needs AVD | 🟡 device-pending | — | — |
| Security audit | no background location / broad storage / cleartext; FileProvider content URIs; backup excludes db+datastore; no secrets in VCS; release log redaction via typed errors | ✅ reviewed | agent | 2026-06-18 |
| Performance | `:benchmark` Macrobenchmark + BaselineProfile module builds; runs need device | 🟡 device-pending | — | — |
| AAB/internal track | `app/build/outputs/bundle/release/app-release.aab` (unsigned; signing config reads `keystore.properties` at release) | 🟡 unsigned-built | agent | 2026-06-18 |

**Honesty note (per CLAUDE.md):** items marked ✅ are *Verified* by the named automated command in this environment. Items marked 🟡 device-pending are *Implemented* against real production APIs and compile, but cannot be hardware-*Verified* without a physical device/emulator and signing key (see `DECISIONS.md` → Environment limitation). No feature uses mock/fake capture, fabricated GPS, or hardcoded sample records.
