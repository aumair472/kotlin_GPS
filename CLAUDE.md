# GeoSnap Timestamp Template Integration Fix

## Current Defect

The Timestamp Templates screen allows the user to select:

* Minimal
* Classic
* Detailed
* Reporter

However, selecting Detailed or Reporter does not update the live camera overlay or the final exported media correctly.

The current camera overlay still displays a fixed/default format such as:

* date and time;
* coordinates;
* altitude;
* accuracy;

instead of rendering the exact selected template.

This is a functional defect, not only a UI-preview issue.

---

## Required Template Behavior

The selected timestamp template must become the single source of truth for all media stamping.

The selected template must affect:

1. Template selection screen
2. Live camera preview overlay
3. Captured photo output
4. Video overlay/export pipeline
5. Collection thumbnail where applicable
6. Photo detail preview
7. Video playback output
8. Reporting attachments and PDF output where stamped media is shown

The app must not maintain separate hardcoded overlay formats for the camera and final media.

---

## Template Definitions

### Minimal

Render:

```text
Jun 19, 2026, 3:33 AM
```

Requirements:

* date and local time only;
* no coordinates;
* no altitude;
* no accuracy;
* no address;
* no GeoSnap brand line.

---

### Classic

Render:

```text
Jun 19, 2026, 3:33 AM
33.6431° N, 72.9525° E
```

Requirements:

* full local date and time;
* latitude and longitude;
* no altitude;
* no accuracy;
* no address;
* no brand title.

---

### Detailed

Render:

```text
Jun 19, 2026, 3:33 AM
33.6431° N, 72.9525° E
Alt: 539 m
±14 m
Islamabad, Pakistan
```

Requirements:

* full local date and time;
* latitude and longitude;
* altitude when available;
* horizontal accuracy when available;
* resolved human-readable address when available.

Do not fabricate unavailable values.

If altitude is unavailable, omit the altitude line.

If accuracy is unavailable, omit the accuracy line.

If address resolution is pending, show a temporary non-fabricated state in the live preview such as:

```text
Resolving address…
```

For captured media, either:

* wait briefly for the latest valid resolved address before final stamping; or
* stamp without the address when resolution fails.

Never write a fake city or country.

---

### Reporter

Render:

```text
GeoSnap
Jun 19, 2026, 3:33 AM
33.6431° N, 72.9525° E
Alt: 539 m
Islamabad, Pakistan
```

Requirements:

* GeoSnap brand/title as the first line;
* full local date and time;
* coordinates;
* altitude when available;
* resolved address when available;
* professional field-report style;
* visually distinct but still readable.

Accuracy may be included only if Reporter design specifications require it consistently. Do not include it accidentally because the default overlay includes it.

---

## Template Persistence

Store the selected template in DataStore using a stable enum or identifier.

Example:

```kotlin
enum class TimestampTemplateType {
    MINIMAL,
    CLASSIC,
    DETAILED,
    REPORTER
}
```

Requirements:

* selection persists across app restarts;
* camera observes selection reactively;
* changing template updates camera overlay immediately;
* no app restart is required;
* ViewModel must expose the selected template as immutable state;
* do not store only the localized display name.

---

## Shared Formatter Requirement

Create one domain-level formatter responsible for generating template content.

Suggested model:

```kotlin
data class TimestampOverlayData(
    val capturedAt: Instant,
    val zoneId: ZoneId,
    val latitude: Double?,
    val longitude: Double?,
    val altitudeMeters: Double?,
    val accuracyMeters: Float?,
    val address: String?,
    val appName: String
)
```

Suggested result:

```kotlin
data class TimestampOverlayContent(
    val lines: List<String>,
    val templateType: TimestampTemplateType
)
```

Suggested interface:

```kotlin
interface TimestampOverlayFormatter {
    fun format(
        template: TimestampTemplateType,
        data: TimestampOverlayData
    ): TimestampOverlayContent
}
```

The same formatter must be used by:

* Compose live preview;
* photo bitmap stamping;
* video overlay rendering;
* report rendering where appropriate.

Do not duplicate string-building logic in multiple layers.

---

## Address Resolution

Use Android Geocoder or the existing location-address repository.

Requirements:

* reverse geocoding must not run directly inside composables;
* run asynchronously;
* cache the latest valid address;
* debounce location changes;
* do not reverse-geocode every frame;
* handle unavailable Geocoder;
* handle network/service errors;
* handle empty result lists;
* support localization where practical;
* never block camera preview.

Address resolution state should include:

* Idle
* Resolving
* Available
* Unavailable
* Failed

---

## Live Camera Overlay

The live camera overlay must observe:

* selected template;
* latest valid location;
* latest altitude;
* latest accuracy;
* current time;
* resolved address.

When the template changes, the overlay must update immediately.

The overlay layout must support variable line counts without clipping.

Requirements:

* use dynamic height;
* no fixed height designed only for Minimal or Classic;
* maintain readable padding;
* avoid covering important camera controls;
* maintain contrast over bright and dark backgrounds;
* use a translucent background;
* support long addresses safely;
* use maximum width constraints and line wrapping;
* do not show stale data as current when location freshness expires.

---

## Photo Stamping

The final captured photo must use the selected template at capture time.

The implementation must:

1. snapshot the selected template;
2. snapshot the latest valid location data;
3. snapshot address state;
4. create the overlay content through the shared formatter;
5. render the overlay onto the final photo bitmap;
6. write appropriate EXIF GPS/time metadata;
7. save final stamped output;
8. persist the template type used in Room.

Do not merely save the live preview screenshot.

Do not apply the currently selected template later if the user changes templates after capture.

Persist the exact template used for each media record.

---

## Video Stamping

The selected template must be applied to video output.

Use the existing verified CameraX effect or Media3 Transformer pipeline.

Requirements:

* snapshot template selection when recording starts;
* use that template consistently throughout the recording;
* render date/time dynamically if the design requires a changing timer/time;
* otherwise use the recording start timestamp consistently;
* include real GPS/address data according to product design;
* persist PROCESSING, READY, or FAILED status;
* store template type used in Room;
* final playable video must visibly contain the selected template.

Do not show Detailed or Reporter only in preview while exporting an unstamped or default-stamped video.

---

## Room Media Metadata

Ensure each captured media record stores at least:

```text
templateType
capturedAt
latitude
longitude
altitude
accuracy
address
timezone
locationFreshness
```

Use a Room migration if the schema changes.

Do not use destructive migration in production.

---

## UI Verification

For every template:

1. select it from Templates;
2. return to Camera;
3. verify the live overlay changes;
4. capture a photo;
5. open it from Collection;
6. verify the saved photo has the selected format;
7. record a video;
8. open it from Collection;
9. verify the final video has the selected format;
10. restart the app;
11. verify the selected template remains selected.

Detailed and Reporter must be verified specifically because these currently fail.

---

## Error and Missing Data Rules

Never fabricate:

* coordinates;
* altitude;
* accuracy;
* address;
* timestamp;
* timezone.

If values are unavailable:

* omit optional lines;
* show a valid unavailable state in preview only;
* persist null values;
* do not substitute sample Karachi, Islamabad, or other locations.

---

## Completion Standard

This issue is complete only when:

* Detailed updates the live camera overlay;
* Reporter updates the live camera overlay;
* Detailed appears correctly on final photos;
* Reporter appears correctly on final photos;
* selected template is applied to final videos;
* selection persists after restart;
* shared formatter is used;
* no hardcoded default overlay remains in camera UI;
* no fake location/address data exists;
* build, tests, and real-device verification pass.
