# GPS Camera: Timestamp & Map

Android app that stamps photos and videos with GPS location, timestamp, altitude, and accuracy, and builds shareable field reports (PDF) from captured evidence.

- Privacy Policy: https://sites.google.com/view/gpscameratimestampmap/home
- Terms & Conditions: https://sites.google.com/view/gps-camera-timestamp-map/home

## Stack

- Kotlin, Jetpack Compose, Material 3
- CameraX (photo/video capture), Media3 (video overlay/export)
- Room (local persistence), DataStore (preferences), WorkManager (background export)
- Hilt (DI)
- Firebase (google-services, project-wired)

## Build

```
./gradlew :app:assembleDebug
./gradlew :app:bundleRelease
```

Release signing requires a local `keystore.properties` (gitignored, not included in this repo) pointing at a keystore file with `storeFile`, `storePassword`, `keyAlias`, `keyPassword`.

## Versioning

- versionCode: 1
- versionName: 1.0.0
