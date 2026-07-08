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
