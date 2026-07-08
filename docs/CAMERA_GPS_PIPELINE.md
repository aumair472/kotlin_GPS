# Camera, GPS, and Stamp Pipeline

## CameraX use cases

Bind the smallest compatible set needed for the current mode:

- `Preview`
- `ImageCapture` in Photo mode
- `VideoCapture<Recorder>` in Video mode
- optional `ImageAnalysis` only if a real feature requires it

Avoid keeping expensive use cases bound without purpose. Query device capabilities and apply a fallback quality strategy.

## Compose integration

Use the stable CameraX Compose/viewfinder path available in the repository toolchain. Encapsulate it behind `CameraPreviewHost` so a tested `PreviewView` fallback can be used on problematic devices.

The UI layer owns only rendering and gestures. A lifecycle-aware camera controller/session object owns binding and controls.

## Capture-time location policy

A location is acceptable for stamping when:

- it was produced recently, default target ≤10 seconds old;
- its accuracy is within a configurable threshold, default target ≤25 m for a “verified” badge;
- it is not a mocked location when integrity mode rejects mocks;
- the user has granted available foreground permission.

If the cached sample is stale or inaccurate, request a current high-accuracy location with a bounded timeout. Do not block the shutter indefinitely. Persist the exact age and accuracy so the report can distinguish high-confidence and low-confidence captures.

Suggested domain model:

```kotlin
data class GeoSnapshot(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val horizontalAccuracyMeters: Float?,
    val providerTimestamp: Instant,
    val capturedAt: Instant,
    val timezoneId: String,
    val isApproximate: Boolean,
    val isMock: Boolean,
    val provider: String?,
    val address: PostalAddress?
)
```

## Photo finalization

Recommended robust path:

1. CameraX writes to an app-owned temporary JPEG.
2. Read dimensions/orientation without decoding full bitmap.
3. Decode a bounded mutable bitmap at final required resolution.
4. Normalize orientation.
5. Draw the selected stamp using `Canvas` and density-independent layout calculations.
6. Compress to a pending MediaStore item.
7. Open final output with AndroidX `ExifInterface` and write supported datetime/GPS tags.
8. Mark MediaStore item no longer pending.
9. Persist record in Room.
10. Delete temporary input.

If a device/API combination supports a direct effect that preserves quality and metadata, it may be used after verification. Never write metadata before recompression because it may be lost.

## Visible overlay rules

- Use safe margins relative to final media dimensions.
- Scale typography and padding by output size, not screen dp.
- Use a semi-transparent dark/light container chosen by template.
- Wrap or omit optional address to avoid clipping.
- Include only real available fields.
- Store template ID, template version, and rendered text in metadata for auditability.

## Video finalization

Two acceptable production strategies:

### A. CameraX OverlayEffect

Apply a CameraX effect to preview and recording frames so the resulting recording already contains the overlay. Verify support across the selected CameraX version and test devices.

### B. Media3 Transformer post-process

Record a clean source, then enqueue a durable job that adds text/bitmap overlay and exports a finalized MP4. Persist:

- source URI/path;
- processing state and progress;
- finalized URI;
- failure reason and retry count.

Collection shows a processing card until finalization succeeds. The raw source remains private and is deleted only after a verified final output exists.

## MediaStore naming

Use sanitized, collision-resistant names, for example:

```text
GeoSnap_20260617_143205_<short-id>.jpg
GeoSnap_20260617_143205_<short-id>.mp4
```

Store display names separately from report titles. Never derive a filesystem path from user-entered report text.

## Audio

Request `RECORD_AUDIO` only when the user starts a video mode where audio is enabled. If denied, offer silent recording rather than blocking all video capture.

## Location/address resolution

Reverse geocoding is optional and asynchronous. Captures must not fail because an address cannot be resolved. Cache normalized address fields in the database and allow “Coordinates only”.

## Integrity limitations

GPS and device clocks can be manipulated on consumer devices. GeoSnap can record evidence and consistency signals but must not claim cryptographic proof of physical presence without a server-backed attestation/signature design. Product copy must say “GPS-tagged” or “location recorded,” not “legally impossible to alter.”
